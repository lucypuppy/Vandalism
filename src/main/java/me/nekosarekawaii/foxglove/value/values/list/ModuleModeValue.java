package me.nekosarekawaii.foxglove.value.values.list;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import me.nekosarekawaii.foxglove.value.values.ListValue;

import java.util.Arrays;

public class ModuleModeValue<T extends Module> extends ListValue {

    private final ObjectArrayList<ModuleMode<T>> moduleModes;
    private ModuleMode<T> selectedMode;

    public ModuleModeValue(final String name, final String description, final Module parent, final ModuleMode<T>... moduleModes) {
        super(name, description, parent, Arrays.stream(moduleModes).map(ModuleMode::getName).toArray(String[]::new));

        this.selectedMode = moduleModes[0];
        this.moduleModes = new ObjectArrayList<>(moduleModes);

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
