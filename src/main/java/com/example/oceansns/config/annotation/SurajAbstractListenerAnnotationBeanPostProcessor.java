package com.example.oceansns.config.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.ConfigUtils;
import io.awspring.cloud.sqs.config.*;
import io.awspring.cloud.sqs.support.resolver.AcknowledgmentHandlerMethodArgumentResolver;
import io.awspring.cloud.sqs.support.resolver.BatchAcknowledgmentArgumentResolver;
import io.awspring.cloud.sqs.support.resolver.BatchPayloadMethodArgumentResolver;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.handler.annotation.support.*;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public abstract class SurajAbstractListenerAnnotationBeanPostProcessor<A extends Annotation>
		implements BeanPostProcessor, BeanFactoryAware, SmartInitializingSingleton, EmbeddedValueResolverAware {

	private final AtomicInteger counter = new AtomicInteger();

	private final Collection<Class<?>> nonAnnotatedClasses = Collections.synchronizedSet(new HashSet<>());

	private final EndpointRegistrar endpointRegistrar = createEndpointRegistrar();

	private final DelegatingMessageHandlerMethodFactory delegatingHandlerMethodFactory = new DelegatingMessageHandlerMethodFactory();

	private BeanFactory beanFactory;

	private StringValueResolver resolver;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (this.nonAnnotatedClasses.contains(targetClass)) {
			return bean;
		}
		detectAnnotationsAndRegisterEndpoints(bean, targetClass);
		return bean;
	}

	@Nullable
	protected ConfigurableBeanFactory getConfigurableBeanFactory() {
		return this.beanFactory instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) this.beanFactory : null;
	}

	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	protected void detectAnnotationsAndRegisterEndpoints(Object bean, Class<?> targetClass) {
		Map<Method, A> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
				(MethodIntrospector.MetadataLookup<A>) method -> AnnotatedElementUtils.findMergedAnnotation(method,
						getAnnotationClass()));
		if (annotatedMethods.isEmpty()) {
			this.nonAnnotatedClasses.add(targetClass);
		}
		annotatedMethods.entrySet().stream()
				.map(entry -> createAndConfigureEndpoint(bean, entry.getKey(), entry.getValue()))
				.forEach(this.endpointRegistrar::registerEndpoint);
	}

	protected abstract Class<A> getAnnotationClass();

	private Endpoint createAndConfigureEndpoint(Object bean, Method method, A annotation) {
		Endpoint endpoint = createEndpoint(annotation);
		ConfigUtils.INSTANCE.acceptIfInstance(endpoint, HandlerMethodEndpoint.class, hme -> {
			hme.setBean(bean);
			hme.setMethod(method);
			hme.setHandlerMethodFactory(this.delegatingHandlerMethodFactory);
		});
		return endpoint;
	}

	protected abstract Endpoint createEndpoint(A sqsListenerAnnotation);

	protected String resolveAsString(String value, String propertyName) {
		try {
			return getValueResolver().resolveStringValue(value);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Error resolving property " + propertyName, e);
		}
	}

	protected StringValueResolver getValueResolver() {
		return this.resolver;
	}

	protected Integer resolveAsInteger(String value, String propertyName) {
		String resolvedValue = resolveAsString(value, propertyName);
		return StringUtils.hasText(resolvedValue) ? Integer.parseInt(resolvedValue) : null;
	}

	protected Set<String> resolveStringArray(String[] destinationNames, String propertyName) {
		return Arrays.stream(destinationNames).map(destinationName -> resolveAsString(destinationName, propertyName))
				.collect(Collectors.toSet());
	}

	protected String getEndpointId(String id) {
		if (StringUtils.hasText(id)) {
			return resolveAsString(id, "id");
		}
		else {
			return getGeneratedIdPrefix() + this.counter.getAndIncrement();
		}
	}

	protected abstract String getGeneratedIdPrefix();

	@Override
	public void afterSingletonsInstantiated() {
		if (this.beanFactory instanceof ListableBeanFactory) {
			((ListableBeanFactory) this.beanFactory).getBeansOfType(SqsListenerConfigurer.class).values()
					.forEach(customizer -> customizer.configure(this.endpointRegistrar));
		}
		this.endpointRegistrar.setBeanFactory(getBeanFactory());
		initializeHandlerMethodFactory();
		this.endpointRegistrar.afterSingletonsInstantiated();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	protected void initializeHandlerMethodFactory() {
		MessageHandlerMethodFactory handlerMethodFactory = this.endpointRegistrar.getMessageHandlerMethodFactory();
		ConfigUtils.INSTANCE.acceptIfInstance(handlerMethodFactory, DefaultMessageHandlerMethodFactory.class,
				this::configureDefaultHandlerMethodFactory);
		this.delegatingHandlerMethodFactory.setDelegate(handlerMethodFactory);
	}

	protected void configureDefaultHandlerMethodFactory(DefaultMessageHandlerMethodFactory handlerMethodFactory) {
		CompositeMessageConverter compositeMessageConverter = createCompositeMessageConverter();

		List<HandlerMethodArgumentResolver> methodArgumentResolvers = new ArrayList<>(
				createAdditionalArgumentResolvers());
		methodArgumentResolvers.addAll(createArgumentResolvers(compositeMessageConverter));
		this.endpointRegistrar.getMethodArgumentResolversConsumer().accept(methodArgumentResolvers);
		handlerMethodFactory.setArgumentResolvers(methodArgumentResolvers);
		handlerMethodFactory.afterPropertiesSet();
	}

	protected Collection<HandlerMethodArgumentResolver> createAdditionalArgumentResolvers() {
		return Collections.emptyList();
	}

	protected CompositeMessageConverter createCompositeMessageConverter() {
		List<MessageConverter> messageConverters = new ArrayList<>();
		messageConverters.add(new StringMessageConverter());
		messageConverters.add(new SimpleMessageConverter());
		messageConverters.add(createDefaultMappingJackson2MessageConverter(this.endpointRegistrar.getObjectMapper()));
		this.endpointRegistrar.getMessageConverterConsumer().accept(messageConverters);
		return new CompositeMessageConverter(messageConverters);
	}

	// @formatter:off
	protected List<HandlerMethodArgumentResolver> createArgumentResolvers(MessageConverter messageConverter) {
		return Arrays.asList(
				new AcknowledgmentHandlerMethodArgumentResolver(),
				new BatchAcknowledgmentArgumentResolver(),
				new HeaderMethodArgumentResolver(new DefaultConversionService(), getConfigurableBeanFactory()),
				new HeadersMethodArgumentResolver(),
				new BatchPayloadMethodArgumentResolver(messageConverter),
				new MessageMethodArgumentResolver(messageConverter),
				new PayloadMethodArgumentResolver(messageConverter));
	}
	// @formatter:on

	protected MappingJackson2MessageConverter createDefaultMappingJackson2MessageConverter(ObjectMapper objectMapper) {
		MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
		jacksonMessageConverter.setSerializedPayloadClass(String.class);
		jacksonMessageConverter.setStrictContentTypeMatch(false);
		if (objectMapper != null) {
			jacksonMessageConverter.setObjectMapper(objectMapper);
		}
		return jacksonMessageConverter;
	}

	protected EndpointRegistrar createEndpointRegistrar() {
		return new EndpointRegistrar();
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.resolver = resolver;
	}

	private static class DelegatingMessageHandlerMethodFactory implements MessageHandlerMethodFactory {

		private MessageHandlerMethodFactory delegate;

		@Override
		public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
			Assert.notNull(this.delegate, "No delegate MessageHandlerMethodFactory set.");
			return this.delegate.createInvocableHandlerMethod(bean, method);
		}

		public void setDelegate(MessageHandlerMethodFactory delegate) {
			this.delegate = delegate;
		}
	}

}
