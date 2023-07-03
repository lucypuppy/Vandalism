package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class StringValue extends Value<String> {

    public StringValue(final String name, final String description, final Module parent, final String defaultValue) {
        super(name, description, parent, defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        setValue(valueObject.get("value").getAsString());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImString imString = new ImString(this.getValue());

        if (ImGui.inputText(this.getName(), imString, ImGuiInputTextFlags.CallbackResize)) {
            this.setValue(imString.get());
        }
    }

}
