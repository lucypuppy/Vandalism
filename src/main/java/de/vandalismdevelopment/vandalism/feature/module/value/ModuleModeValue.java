package de.vandalismdevelopment.vandalism.feature.module.value;

import de.vandalismdevelopment.vandalism.base.value.template.ValueModeGeneric;
import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;

public class ModuleModeValue<T extends AbstractModule> extends ValueModeGeneric<T> {

    @SafeVarargs
    public ModuleModeValue(AbstractModule parent, String name, String description, T defaultValue,  T... options) {
        super(parent, name, description, defaultValue, Feature::getName, s -> {
            for (final T module : options) {
                if (module.getName().equals(s)) {
                    return module;
                }
            }
            return null;
        }, options);


        this.onValueChange((oldValue, newValue) -> {
            if (parent.isActive()) {
                oldValue.onDisable();
                newValue.onEnable();
            }
        });
    }

}
