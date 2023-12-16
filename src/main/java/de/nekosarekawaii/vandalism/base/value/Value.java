package de.nekosarekawaii.vandalism.base.value;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.pattern.functional.IName;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

public abstract class Value<V> implements IName {

    private final ValueParent parent;
    private final String name;
    private final String description;

    private final V defaultValue;
    private V value;
    private BiConsumer<V, V> valueChangeConsumer;
    private BooleanSupplier visibleCondition;

    public Value(ValueParent parent, String name, String description, V defaultValue) {
        this(parent, name, description, defaultValue, defaultValue);
    }

    public Value(ValueParent parent, String name, String description, V defaultValue, V value) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = value;
        parent.getValues().add(this);
    }

    public void setValue(V value) {
        final V oldValue = this.value;
        if (this.valueChangeConsumer != null) {
            // Allows the event to change the value
            this.valueChangeConsumer.accept(oldValue, value);
        }
        this.value = value;
    }

    public <S extends Value<V>> S onValueChange(final BiConsumer<V, V> valueChangeConsumer) {
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
