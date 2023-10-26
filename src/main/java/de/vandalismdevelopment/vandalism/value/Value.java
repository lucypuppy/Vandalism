package de.vandalismdevelopment.vandalism.value;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class Value<V> {

    private final String name, description;
    private final V defaultValue;
    private V value;
    private BooleanSupplier visible;
    private Consumer<V> valueChangeConsumer, valueChangedConsumer;
    private final String saveIdentifier;
    private final IValue parent;

    public Value(final String name, final String description, final IValue parent, final String dataType, final V defaultValue) {
        this.name = name;
        this.description = description;

        this.saveIdentifier = name + " (" + dataType + ")" + " [" + parent.getValueName().hashCode() + "]";

        this.defaultValue = defaultValue;
        this.setValue(defaultValue);

        this.parent = parent;
        parent.getValues().add(this);
    }

    public void setValue(final V value) {
        if (this.value == value) {
            return;
        }

        if (this.valueChangeConsumer != null) {
            this.valueChangeConsumer.accept(value);
        }

        this.value = value;

        if (this.valueChangedConsumer != null) {
            this.valueChangedConsumer.accept(value);
        }

        if (Vandalism.getInstance().getConfigManager() != null && this.parent != null) {
            if (this.parent.getConfig() == null) {
                throw new IllegalStateException("Value config for the parent " + this.parent.getValueName() + " is null!");
            }

            Vandalism.getInstance().getConfigManager().save(this.parent.getConfig());
        }
    }

    public <S extends Value<V>> S valueChangeConsumer(final Consumer<V> valueChangeConsumer) {
        this.valueChangeConsumer = valueChangeConsumer;
        return (S) this;
    }

    public <S extends Value<V>> S valueChangedConsumer(final Consumer<V> valueChangedConsumer) {
        this.valueChangedConsumer = valueChangedConsumer;
        return (S) this;
    }

    public <S extends Value<V>> S visibleConsumer(final BooleanSupplier visible) {
        this.visible = visible;
        return (S) this;
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
        this.setValue(this.defaultValue);
    }

    public BooleanSupplier isVisible() {
        return this.visible;
    }

    public String getSaveIdentifier() {
        return this.saveIdentifier;
    }

    public IValue getParent() {
        return this.parent;
    }

    public abstract void onConfigLoad(final JsonObject valueObject);

    public abstract void onConfigSave(final JsonObject valueObject);

    public abstract void render();

}
