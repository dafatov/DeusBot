package ru.demetrious.deus.bot.fw.annotation.quartz;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.quartz.utils.Key.DEFAULT_GROUP;

@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface InitScheduled {
    String groupName() default DEFAULT_GROUP;

    String name();

    String cron();
}
