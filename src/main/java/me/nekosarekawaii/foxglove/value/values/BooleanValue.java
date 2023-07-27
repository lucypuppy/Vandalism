package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.type.ImBoolean;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;

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
        final var imValue = new ImBoolean(this.getValue());

        if (ImGui.checkbox(this.getName() + "##" + this.getHashIdent(), imValue))
            this.setValue(imValue.get());
    }

}
