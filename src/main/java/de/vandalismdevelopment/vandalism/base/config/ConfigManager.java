package de.vandalismdevelopment.vandalism.base.config;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.config.template.ConfigWithValues;

public class ConfigManager extends Storage<AbstractConfig<?>> {

    public ConfigManager() {
        setAddConsumer(AbstractConfig::load);
    }

    @Override
    public void init() {
        // TODO | Move this into systems
        this.add(new ConfigWithValues("scripts", Vandalism.getInstance().getScriptRegistry().getScripts()));
        this.add(new ConfigWithValues("modules", Vandalism.getInstance().getModuleRegistry().getModules()));
    }

    public void save() {
        for (final AbstractConfig<?> config : getList()) {
            config.save();
        }
    }

}
