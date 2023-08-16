package de.nekosarekawaii.foxglove.feature.impl.module;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {

    String name() default "Example Module";

    String description() default "This is a Module.";

    FeatureCategory category() default FeatureCategory.MISC;

    boolean isDefaultEnabled() default false;

    boolean isExperimental() default false;

}
