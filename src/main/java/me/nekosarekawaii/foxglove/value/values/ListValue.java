package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class ListValue extends Value<String> {

    private final ObjectArrayList<String> modes = new ObjectArrayList<>();

    public ListValue(final String name, final String description, final Module parent, final String defaultValue, final String... modes) {
        super(name, description, parent, defaultValue);
        this.modes.add(defaultValue);
        this.modes.addAll(new ObjectArrayList<>(modes));
    }

    public ObjectArrayList<String> getModes() {
        return this.modes;
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
        if (ImGui.beginCombo(getName(), getValue())) {
            for (final String mode : getModes()) {
                if (ImGui.selectable(mode, mode.equals(getValue()))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
