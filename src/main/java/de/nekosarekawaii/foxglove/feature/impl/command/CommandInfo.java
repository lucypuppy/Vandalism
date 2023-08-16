package de.nekosarekawaii.foxglove.feature.impl.command;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

    String name() default "Example Command";

    String description() default "This is a Command.";

    String[] aliases() default {"example"};

    FeatureCategory category() default FeatureCategory.MISC;

    boolean isExperimental() default false;

}
