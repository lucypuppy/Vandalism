package me.nekosarekawaii.foxglove.value;

import me.nekosarekawaii.foxglove.feature.impl.module.Module;

public abstract class NumberValue<T> extends Value<T> {

    private final T step;

    public NumberValue(final String name, final String description, final Module parent, final T defaultValue, final T step) {
        super(name, description, parent, defaultValue);
        this.step = step;
    }

    public T getStep() {
        return this.step;
    }

}
