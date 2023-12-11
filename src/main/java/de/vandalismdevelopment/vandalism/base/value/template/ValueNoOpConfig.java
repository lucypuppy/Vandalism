package de.vandalismdevelopment.vandalism.base.value.template;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;

public abstract class ValueNoOpConfig<T> extends Value<T> {

    public ValueNoOpConfig(ValueParent parent, String name, String description, T defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(JsonObject mainNode) {
    }

    @Override
    public void save(JsonObject mainNode) {
    }

}
