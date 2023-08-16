package de.nekosarekawaii.foxglove.value.values.list;

import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import de.nekosarekawaii.foxglove.value.values.ListValue;

import java.util.Arrays;
import java.util.List;

public class ModuleModeValue<T extends Module> extends ListValue {

    private final List<ModuleMode<T>> moduleModes;
    private ModuleMode<T> selectedMode;

    @SafeVarargs
    public ModuleModeValue(final String name, final String description, final Module parent, final ModuleMode<T>... moduleModes) {
        super(name, description, parent, Arrays.stream(moduleModes).map(ModuleMode::getName).toArray(String[]::new));

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

        return null;
    }

    public ModuleMode<T> getSelectedMode() {
        return this.selectedMode;
    }
}
