package de.vandalismdevelopment.vandalism.base.config;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.config.impl.IRCConfig;
import de.vandalismdevelopment.vandalism.base.config.template.ConfigWithValues;

public class ConfigManager extends Storage<AbstractConfig<?>> {

    public IRCConfig ircConfig;

    public ConfigManager() {
        setAddConsumer(AbstractConfig::load);
    }

    @Override
    public void init() {
        this.add(
                this.ircConfig = new IRCConfig()
        );
    }

    public void save() {
        for (final AbstractConfig<?> config : getList()) {
            config.save();
        }
    }

}
