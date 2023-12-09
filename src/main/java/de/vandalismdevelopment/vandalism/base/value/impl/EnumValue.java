package de.vandalismdevelopment.vandalism.base.value.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.Value;
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
        valueObject.addProperty("value", this.getValue().normalName());
    }

    @Override
    public void render() {
        final String selectedName = this.getValue().normalName();
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
