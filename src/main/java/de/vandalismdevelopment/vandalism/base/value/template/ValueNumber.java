package de.vandalismdevelopment.vandalism.base.value.template;

import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;

import java.util.function.BiConsumer;

public abstract class ValueNumber<T extends Number> extends Value<T> {

    private final T minValue;
    private final T maxValue;

    private String format = "%.1f";

    public ValueNumber(ValueParent parent, String name, String description, T minValue, T defaultValue, T maxValue) {
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
