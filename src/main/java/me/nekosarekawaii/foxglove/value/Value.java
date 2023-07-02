package me.nekosarekawaii.foxglove.value;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class Value<V> {

    private final String name, description;
    private final V defaultValue;
    private V value;
    private BooleanSupplier visible;
    private Consumer<V> valueChangeConsumer, valueChangedConsumer;

    public Value(final String name, final String description, final Module parent, final V defaultValue) {
        this.name = name;
        this.description = description;

        this.defaultValue = defaultValue;
        this.setValue(defaultValue);

        parent.getValues().add(this);
    }

    public void setValue(final V value) {
        if (this.valueChangeConsumer != null)
            this.valueChangeConsumer.accept(value);

        this.value = value;

        if (this.valueChangedConsumer != null)
            this.valueChangedConsumer.accept(value);
    }

    public Value<V> valueChangeConsumer(final Consumer<V> valueChangeConsumer) {
        this.valueChangeConsumer = valueChangeConsumer;
        return this;
    }

    public Value<V> valueChangedConsumer(final Consumer<V> valueChangedConsumer) {
        this.valueChangedConsumer = valueChangedConsumer;
        return this;
    }

    public Value<V> visibleConsumer(final BooleanSupplier visible) {
        this.visible = visible;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public V getValue() {
        return this.value;
    }

    public V getDefaultValue() {
        return this.defaultValue;
    }

    public void resetValue() {
        this.value = this.defaultValue;
    }

    public BooleanSupplier isVisible() {
        return this.visible;
    }

    public abstract void onConfigLoad(final JsonObject valueObject);

    public abstract void onConfigSave(final JsonObject valueObject);

}
