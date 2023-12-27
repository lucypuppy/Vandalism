package de.nekosarekawaii.vandalism;

import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Consumer;

/**
 * Integration interface for Vandalism addons to be launched after Vandalism has been initialized.
 */
public interface VandalismAddonLauncher {

    /**
     * Launches the addon after Vandalism has been initialized and before the config files are loaded.
     *
     * @param vandalism The Vandalism instance.
     */
    void onLaunch(final Vandalism vandalism);

    /**
     * Launches the addon after both Vandalism and the config files have been loaded.
     *
     * @param vandalism The Vandalism instance.
     */
    default void onLateLaunch(final Vandalism vandalism) {
    }

    /**
     * Invokes the given consumer for all registered addons that implement this interface.
     *
     * @param consumer The consumer to invoke.
     */
    static void call(final Consumer<VandalismAddonLauncher> consumer) {
        for (final VandalismAddonLauncher entrypoint : FabricLoader.getInstance().getEntrypoints("vandalism:onLaunch", VandalismAddonLauncher.class)) {
            consumer.accept(entrypoint);
        }
    }

}
