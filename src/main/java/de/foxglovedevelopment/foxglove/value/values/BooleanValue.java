package de.foxglovedevelopment.foxglove.value.values;

import com.google.gson.JsonObject;
import de.foxglovedevelopment.foxglove.value.IValue;
import de.foxglovedevelopment.foxglove.value.Value;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(final String name, final String description, final IValue parent, final boolean defaultValue) {
        super(name, description, parent, defaultValue);
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

        if (ImGui.checkbox(this.getName() + "##" + this.getHashIdent(), imValue))
            this.setValue(imValue.get());
    }

}
