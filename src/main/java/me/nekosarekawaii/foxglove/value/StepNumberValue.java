package me.nekosarekawaii.foxglove.value;

public abstract class StepNumberValue<T extends Number> extends Value<T> {

    private final T step;

    public StepNumberValue(final String name, final String description, final IValue parent, final T defaultValue, final T step) {
        super(name, description, parent, defaultValue);
        this.step = step;
    }

    public T getStep() {
        return this.step;
    }

}
