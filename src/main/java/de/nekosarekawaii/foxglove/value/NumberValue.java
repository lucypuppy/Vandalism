package de.nekosarekawaii.foxglove.value;

public abstract class NumberValue<T extends Number> extends Value<T> {

    public NumberValue(final String name, final String description, final IValue parent, final T defaultValue) {
        super(name, description, parent, defaultValue);
    }

}
