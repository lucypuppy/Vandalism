package de.vandalismdevelopment.vandalism.value;

public abstract class SliderNumberValue<T extends Number> extends NumberValue<T> {

    private final T min, max;
    private final String format;

    public SliderNumberValue(final String name, final String description, final IValue parent, final String sliderNumberType, final T defaultValue, final T min, final T max, final String format) {
        super(name, description, parent, sliderNumberType + " slider", defaultValue);

        this.min = min;
        this.max = max;
        this.format = format;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

    public String getFormat() {
        return this.format;
    }

}
