package me.nekosarekawaii.foxglove.feature.impl.command;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The CommandInfo annotation is used to provide metadata for command classes.
 * It specifies the name, description, aliases, category, and experimental status of a command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

    /**
     * Specifies the name of the command.
     *
     * @return The name of the command.
     */
    String name() default "Example Command";

    /**
     * Specifies the description of the command.
     *
     * @return The description of the command.
     */
    String description() default "This is a Command.";

    /**
     * Specifies the aliases of the command.
     *
     * @return The aliases of the command.
     */
    String[] aliases() default {"example"};

    /**
     * Specifies the category of the command.
     *
     * @return The category of the command.
     */
    FeatureCategory category() default FeatureCategory.MISC;

    /**
     * Specifies whether the command is experimental.
     *
     * @return true if the command is experimental, false otherwise.
     */
    boolean isExperimental() default false;

}
