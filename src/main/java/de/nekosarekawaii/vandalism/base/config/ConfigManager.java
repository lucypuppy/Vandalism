package de.nekosarekawaii.vandalism.base.config;

import de.florianmichael.rclasses.pattern.storage.Storage;

public class ConfigManager extends Storage<AbstractConfig<?>> {

    @Override
    public void init() {
        for (final AbstractConfig<?> config : getList()) {
            config.load();
        }
    }

    public void save() {
        for (final AbstractConfig<?> config : getList()) {
            config.save();
        }
    }

}
