package me.nekosarekawaii.foxglove.feature.impl.module;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The ModuleInfo annotation is used to provide metadata for a Module class.
 * It specifies the name, description, category, default enabled state, and experimental status of the module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {

    /**
     * The name of the module.
     *
     * @return The name of the module.
     */
    String name() default "Example Module";

    /**
     * The description of the module.
     *
     * @return The description of the module.
     */
    String description() default "This is a Module.";

    /**
     * The category of the module.
     *
     * @return The category of the module.
     */
    FeatureCategory category() default FeatureCategory.MISC;

    /**
     * The default enabled state of the module.
     * If set to true, the module will be enabled by default when the Foxglove mod starts.
     *
     * @return The default enabled state of the module.
     */
    boolean isDefaultEnabled() default false;

    /**
     * The experimental status of the module.
     * If set to true, the module is considered experimental and may have unstable or unfinished features.
     *
     * @return The experimental status of the module.
     */
    boolean isExperimental() default false;

}
