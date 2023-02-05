package com.example.oceansns.config.annotation;

import io.awspring.cloud.sqs.config.Endpoint;
import io.awspring.cloud.sqs.config.EndpointRegistrar;
import io.awspring.cloud.sqs.config.SqsEndpoint;
import io.awspring.cloud.sqs.listener.SqsHeaders;
import io.awspring.cloud.sqs.support.resolver.QueueAttributesMethodArgumentResolver;
import io.awspring.cloud.sqs.support.resolver.SqsMessageMethodArgumentResolver;
import io.awspring.cloud.sqs.support.resolver.VisibilityHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * {@link BeanPostProcessor} implementation that scans beans for a {@link SurajSqsListener @SqsListener} annotation, extracts
 * information to a {@link SqsEndpoint}, and registers it in the {@link EndpointRegistrar}.
 *
 * @author Tomaz Fernandes
 * @since 3.0
 */
@Component
public class SurajSqsListenerAnnotationBeanPostProcessor extends SurajAbstractListenerAnnotationBeanPostProcessor<SurajSqsListener> {

	@Value("${surajQueue:demo}")
	private String SURAJ_QUEUENAME;

	private static final String GENERATED_ID_PREFIX = "io.awspring.cloud.sqs.sqsListenerEndpointContainer#";

	@Override
	protected Class<SurajSqsListener> getAnnotationClass() {
		return SurajSqsListener.class;
	}

	protected Endpoint createEndpoint(SurajSqsListener surajSqsListenerAnnotation) {
		Set<String> queueList = new HashSet<>();
		queueList.add(SURAJ_QUEUENAME);
		return SqsEndpoint.builder().queueNames(queueList)
//				.factoryBeanName(resolveAsString(surajSqsListenerAnnotation.factory(), "factory"))
				.id(getEndpointId(surajSqsListenerAnnotation.id()))
				.pollTimeoutSeconds(resolveAsInteger(surajSqsListenerAnnotation.pollTimeoutSeconds(), "pollTimeoutSeconds"))
				.maxMessagesPerPoll(resolveAsInteger(surajSqsListenerAnnotation.maxMessagesPerPoll(), "maxMessagesPerPoll"))
				.maxConcurrentMessages(
						resolveAsInteger(surajSqsListenerAnnotation.maxConcurrentMessages(), "maxConcurrentMessages"))
				.messageVisibility(
						resolveAsInteger(surajSqsListenerAnnotation.messageVisibilitySeconds(), "messageVisibility"))
				.build();
	}

	@Override
	protected String getGeneratedIdPrefix() {
		return GENERATED_ID_PREFIX;
	}

	@Override
	protected Collection<HandlerMethodArgumentResolver> createAdditionalArgumentResolvers() {
		return Arrays.asList(new VisibilityHandlerMethodArgumentResolver(SqsHeaders.SQS_VISIBILITY_TIMEOUT_HEADER),
				new SqsMessageMethodArgumentResolver(), new QueueAttributesMethodArgumentResolver());
	}

}
