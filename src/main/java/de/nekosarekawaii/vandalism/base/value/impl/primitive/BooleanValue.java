package de.nekosarekawaii.vandalism.base.value.impl.primitive;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(ValueParent parent, String name, String description, Boolean defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        this.setValue(mainNode.get(getName()).getAsBoolean());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(getName(), getValue());
    }

    @Override
    public void render() {
        if (ImGui.checkbox("##" + getName(), new ImBoolean(this.getValue()))) {
            this.setValue(!this.getValue());
        }
    }

}
