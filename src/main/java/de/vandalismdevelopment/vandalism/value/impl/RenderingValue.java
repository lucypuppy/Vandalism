package de.vandalismdevelopment.vandalism.value.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.gui.imgui.RenderInterface;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;

public class RenderingValue extends Value<RenderInterface> {

    public RenderingValue(final String name, final String description, final IValue parent, final RenderInterface defaultValue) {
        super(name, description, parent, "rendering", defaultValue);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
    }

    @Override
    public void render() {
        ImGui.separator();
        this.getValue().render(ImGui.getIO());
        ImGui.separator();
        ImGui.spacing();
    }

}
