package de.nekosarekawaii.vandalism.base.config;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.base.config.impl.IRCConfig;

public class ConfigManager extends Storage<AbstractConfig<?>> {

    public IRCConfig ircConfig;

    @Override
    public void init() {
        this.add(
                this.ircConfig = new IRCConfig()
        );

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
