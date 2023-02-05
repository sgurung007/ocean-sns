package com.example.oceansns.config.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SurajSqsListener {

	/**
	 * Array of queue names or urls. Queues declared in the same annotation will be handled by the same
	 * {@link io.awspring.cloud.sqs.listener.MessageListenerContainer}.
	 * @return list of queue names or urls.
	 */
	String[] value() default {};

	String id() default "";

	/**
	 * The maximum concurrent messages that can be processed simultaneously for each queue. Note that if acknowledgement
	 * batching is being used, the actual maximum number of inflight messages might be higher. Default is 10.
	 * @return the maximum number of concurrent messages.
	 */
	String maxConcurrentMessages() default "";

	/**
	 * The maximum number of seconds to wait for messages in a poll to SQS.
	 * @return the poll timeout.
	 */
	String pollTimeoutSeconds() default "";

	/**
	 * The maximum amount of time to wait for a poll to SQS. If a value greater than 10 is provided, the result of
	 * multiple polls will be combined, which can be useful for
	 * {@link io.awspring.cloud.sqs.listener.ListenerMode#BATCH}
	 * @return the maximum messages per poll.
	 */
	String maxMessagesPerPoll() default "";

	/**
	 * The message visibility to be applied to messages received from the provided queues. For Standard SQS queues and
	 * batch listeners, visibility will be applied at polling. For single message FIFO queues, visibility is changed
	 * before each remaining message from the same message group is processed.
	 */
	String messageVisibilitySeconds() default "";

}
