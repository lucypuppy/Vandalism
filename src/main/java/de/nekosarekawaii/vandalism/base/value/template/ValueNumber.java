package de.nekosarekawaii.vandalism.base.value.template;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;

public abstract class ValueNumber<T extends Number> extends Value<T> {

    private final T minValue;
    private final T maxValue;

    private String format = "%.1f";

    public ValueNumber(ValueParent parent, String name, String description, T defaultValue, T minValue, T maxValue) {
        super(parent, name, description, defaultValue);

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public <S extends Value<T>> S format(final String format) {
        this.format = format;
        return (S) this;
    }


    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public String getFormat() {
        return format;
    }

}
