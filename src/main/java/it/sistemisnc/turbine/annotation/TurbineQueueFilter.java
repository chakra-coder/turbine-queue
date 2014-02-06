package it.sistemisnc.turbine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation add filters on your @{link IQueueListener}.
 * When thread messages processor get message, check if listener have annotation
 * and the message type in filters. If the message type don't exists the message
 * won't delivered
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TurbineQueueFilter {

    int[] filters();
}
