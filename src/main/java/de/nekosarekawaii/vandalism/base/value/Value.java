package de.nekosarekawaii.vandalism.base.value;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.pattern.functional.IName;

import java.util.function.BooleanSupplier;

public abstract class Value<V> implements IName {

    private final ValueParent parent;
    private final String name;
    private final String description;
    private final V defaultValue;

    private V value;
    private ValueChangeConsumer<V, V> valueChangeConsumer;
    private BooleanSupplier visibleCondition;

    public Value(ValueParent parent, String name, String description, V defaultValue) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;

        this.value = defaultValue;

        parent.getValues().add(this);
    }

    public void setValue(final V value) {
        final V oldValue = this.value;
        this.value = this.valueChangeConsumer != null ? this.valueChangeConsumer.getNewValue(oldValue, value) : value;
    }

    public <S extends Value<V>> S onValueChange(final ValueChangeConsumer<V, V> valueChangeConsumer) {
        this.valueChangeConsumer = valueChangeConsumer;
        return (S) this;
    }

    public <S extends Value<V>> S visibleCondition(final BooleanSupplier visible) {
        this.visibleCondition = visible;
        return (S) this;
    }

    @Override
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
        this.setValue(this.defaultValue);
    }

    public BooleanSupplier isVisible() {
        return this.visibleCondition;
    }

    public ValueParent getParent() {
        return this.parent;
    }

    public abstract void load(final JsonObject mainNode);
    public abstract void save(final JsonObject mainNode);

    public abstract void render();

}
