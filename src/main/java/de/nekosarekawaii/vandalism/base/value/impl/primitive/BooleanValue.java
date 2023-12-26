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
        if (mainNode.has(this.getName())) {
            this.setValue(mainNode.get(this.getName()).getAsBoolean());
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), this.getValue());
    }

    @Override
    public void render() {
        if (ImGui.checkbox("##" + this.getName() + this.getParent().getName(), new ImBoolean(this.getValue()))) {
            this.setValue(!this.getValue());
        }
    }

}
