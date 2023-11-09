package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;

public class EnumValue<T extends EnumNameNormalizer> extends Value<T> {

    private final T[] values;

    @SafeVarargs
    public EnumValue(final String name, final String description, final IValue parent, final T... values) {
        super(name, description, parent, "enum", values[0]);
        this.values = values;
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        for (final T value : this.values) {
            if (value.normalName().equals(valueObject.get("value").getAsString())) {
                this.setValue(value);
                return;
            }
        }
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue().normalName());
    }

    @Override
    public void render() {
        final String selectedName = this.getValue().normalName();
        ImGui.text(selectedName);
        if (ImGui.beginCombo("##" + this.getSaveIdentifier(), selectedName)) {
            for (final T mode : this.values) {
                final String name = mode.normalName();
                if (ImGui.selectable(name, name.equals(selectedName))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
