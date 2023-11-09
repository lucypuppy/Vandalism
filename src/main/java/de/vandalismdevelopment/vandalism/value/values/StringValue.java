package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

public class StringValue extends Value<String> {

    public StringValue(final String name, final String description, final IValue parent, final String defaultValue) {
        super(name, description, parent, "string", defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsString());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImString imString = new ImString(this.getValue());
        ImGui.text(this.getValue());
        if (ImGui.inputText("##" + this.getSaveIdentifier(), imString, ImGuiInputTextFlags.CallbackResize)) {
            this.setValue(imString.get());
        }
    }

}
