package me.nekosarekawaii.foxglove.value.values.list;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.feature.impl.module.Mode;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.values.ListValue;

import java.util.Arrays;

public class ModeValue<T extends Module> extends ListValue {

    private final ObjectArrayList<Mode<T>> modes = new ObjectArrayList<>();
    private Mode<T> selectedMode;

    public ModeValue(final String name, final String description, final Module parent, final Mode<T>... modes) {
        super(name, description, parent, modes[0].getName(), Arrays.stream(modes).map(Mode::getName)
                .filter(t -> !modes[0].getName().equals(t)).toArray(String[]::new));

        this.selectedMode = modes[0];
        this.modes.addAll(new ObjectArrayList<>(modes));

        this.valueChangedConsumer(s -> {
            if (parent.isEnabled()) this.selectedMode.onDisable();
            this.selectedMode = this.getValue(s);
            if (parent.isEnabled()) this.selectedMode.onEnable();
        });
    }


    private Mode<T> getValue(final String name) {
        for (final Mode<T> value : this.modes) {
            if (value.getName().equals(name)) {
                return value;
            }
        }

        return null;
    }

    public Mode<T> getSelectedMode() {
        return this.selectedMode;
    }
}
