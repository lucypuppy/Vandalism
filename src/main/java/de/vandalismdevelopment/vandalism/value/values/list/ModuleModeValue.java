package de.vandalismdevelopment.vandalism.value.values.list;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.value.values.ListValue;

import java.util.Arrays;
import java.util.List;

public class ModuleModeValue<T extends Module> extends ListValue {

    private final List<ModuleMode<T>> moduleModes;
    private ModuleMode<T> selectedMode;

    @SafeVarargs
    public ModuleModeValue(final String name, final String description, final Module parent, final ModuleMode<T>... moduleModes) {
        super(name, description, parent, "module mode", Arrays.stream(moduleModes).map(ModuleMode::getName).toArray(String[]::new));

        this.selectedMode = moduleModes[0];
        this.moduleModes = Arrays.asList(moduleModes);

        this.valueChangedConsumer(s -> {
            if (parent.isEnabled()) this.selectedMode.onDisable();
            this.selectedMode = this.getValue(s);
            if (parent.isEnabled()) this.selectedMode.onEnable();
        });
    }

    private ModuleMode<T> getValue(final String name) {
        for (final ModuleMode<T> value : this.moduleModes) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        Vandalism.getInstance().getLogger().error(
                "Could not find module mode with the name '" + name + "' for module '" + this.getParent().iName() + "' resetting it to the default module mode '" + this.getDefaultValue() + "'."
        );
        return this.getValue(this.getDefaultValue());
    }

    public ModuleMode<T> getSelectedMode() {
        return this.selectedMode;
    }

}
