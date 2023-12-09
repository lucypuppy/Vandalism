package de.vandalismdevelopment.vandalism.base.value;

public abstract class NumberValue<T extends Number> extends Value<T> {

    public NumberValue(final String name, final String description, final IValue parent, final String numberType, final T defaultValue) {
        super(name, description, parent, numberType + " number", defaultValue);
    }

}
