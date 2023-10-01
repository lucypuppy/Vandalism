package de.vandalismdevelopment.vandalism.value;

public abstract class StepNumberValue<T extends Number> extends NumberValue<T> {

    private final T step;

    public StepNumberValue(final String name, final String description, final IValue parent, final String stepNumberType, final T defaultValue, final T step) {
        super(name, description, parent, stepNumberType + " step", defaultValue);
        this.step = step;
    }

    public T getStep() {
        return this.step;
    }

}
