package de.nekosarekawaii.vandalism.feature.module.template;

import de.nekosarekawaii.vandalism.base.value.template.ValueModeGeneric;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class ModuleModeValue<T extends AbstractModule> extends ValueModeGeneric<ModuleMulti<T>> {

    @SafeVarargs
    public ModuleModeValue(AbstractModule parent, String name, String description, ModuleMulti<T>... options) {
        super(parent, name, description, ModuleMulti::getName, s -> {
            for (final ModuleMulti<T> module : options) {
                if (module.getName().equals(s)) {
                    return module;
                }
            }
            return null;
        }, options);

        this.onValueChange((oldValue, newValue) -> {
            if (parent.isActive()) {
                oldValue.onDeactivate();
                newValue.onActivate();
            }
        });
    }

}
