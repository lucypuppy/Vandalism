package de.nekosarekawaii.vandalism;

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

    static String getEntrypointName() {
        return "vandalism:onLaunch";
    }

}
