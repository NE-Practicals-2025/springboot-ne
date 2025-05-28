package com.proj.springsecrest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int value() default 10; // number of requests
    int durationInSeconds() default 1; // duration of window in seconds
    String key() default ""; // custom key
}
