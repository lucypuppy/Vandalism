package de.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import de.nekosarekawaii.foxglove.value.IValue;
import de.nekosarekawaii.foxglove.value.Value;
import imgui.ImGui;

import java.util.Arrays;
import java.util.List;

public class ListValue extends Value<String> {

    private final List<String> modes;

    public ListValue(final String name, final String description, final IValue parent, final String... modes) {
        super(name, description, parent, modes[0]);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() {
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
        if (ImGui.beginCombo(this.getName() + "##" + this.getHashIdent(), getValue())) {
            for (final String mode : getModes()) {
                if (ImGui.selectable(mode, mode.equals(getValue()))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
