package de.vandalismdevelopment.vandalism.base.value.impl.list;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.base.value.impl.ListValue;

import java.util.Arrays;
import java.util.List;

public class ModuleModeValue<T extends AbstractModule> extends ListValue {

    private final List<ModuleMulti<T>> moduleModes;
    private ModuleMulti<T> selectedMode;

    @SafeVarargs
    public ModuleModeValue(final String name, final String description, final AbstractModule parent, final ModuleMulti<T>... moduleModes) {
        super(name, description, parent, "module mode", Arrays.stream(moduleModes).map(ModuleMulti::getName).toArray(String[]::new));

        this.selectedMode = moduleModes[0];
        this.moduleModes = Arrays.asList(moduleModes);

        this.valueChangedConsumer(s -> {
            if (parent.isEnabled()) this.selectedMode.onDisable();
            this.selectedMode = this.getValue(s);
            if (parent.isEnabled()) this.selectedMode.onEnable();
        });
    }

    private ModuleMulti<T> getValue(final String name) {
        for (final ModuleMulti<T> value : this.moduleModes) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        Vandalism.getInstance().getLogger().error(
                "Could not find module mode with the name '" + name + "' for module '" + this.getParent().getValueName() + "' resetting it to the default module mode '" + this.getDefaultValue() + "'."
        );
        return this.getValue(this.getDefaultValue());
    }

    public ModuleMulti<T> getSelectedMode() {
        return this.selectedMode;
    }

}
