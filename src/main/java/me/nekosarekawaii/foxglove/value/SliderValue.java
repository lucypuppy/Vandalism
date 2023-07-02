package me.nekosarekawaii.foxglove.value;

import me.nekosarekawaii.foxglove.feature.impl.module.Module;

public abstract class SliderValue<T> extends Value<T> {

    private final T min, max;

    public SliderValue(final String name, final String description, final Module parent, final T defaultValue, final T min, final T max) {
        super(name, description, parent, defaultValue);

        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

}
