package de.vandalismdevelopment.vandalism.base.value.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.Value;
import imgui.ImGui;

public class RenderingValue extends Value<Runnable> {

    public RenderingValue(final String name, final String description, final IValue parent, final Runnable defaultValue) {
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
        this.getValue().run();
        ImGui.separator();
        ImGui.spacing();
    }

}
