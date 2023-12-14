package de.nekosarekawaii.vandalism.base.value.impl.primitive;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

public class StringValue extends Value<String> {

    public StringValue(ValueParent parent, String name, String description, String defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        this.setValue(mainNode.get(this.getName()).getAsString());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), this.getValue());
    }

    @Override
    public void render() {
        final ImString input = new ImString(this.getValue());
        if (ImGui.inputText("##" + this.getName() + this.getParent().getName(), input, ImGuiInputTextFlags.CallbackResize)) {
            this.setValue(input.get());
        }
    }

}
