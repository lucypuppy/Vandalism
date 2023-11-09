package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(final String name, final String description, final IValue parent, final boolean defaultValue) {
        super(name, description, parent, "boolean", defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsBoolean());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImBoolean imValue = new ImBoolean(this.getValue());
        if (ImGui.checkbox("##" + this.getSaveIdentifier(), imValue)) {
            this.setValue(imValue.get());
        }
    }

}
