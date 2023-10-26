package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;

public class EnumValue<T extends IName> extends Value<T> {

    private final T[] values;

    public EnumValue(final String name, final String description, final IValue parent, final T... values) {
        super(name, description, parent, "enum", values[0]);
        this.values = values;
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        for (final T value : this.values) {
            if (value.getName().equals(valueObject.get("value").getAsString())) {
                this.setValue(value);
                return;
            }
        }
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue().getName());
    }

    @Override
    public void render() {
        final String selectedName = this.getValue().getName();

        if (ImGui.beginCombo(this.getName() + "##" + this.getSaveIdentifier(), selectedName)) {
            for (final T mode : this.values) {
                final String name = mode.getName();

                if (ImGui.selectable(name, name.equals(selectedName))) {
                    this.setValue(mode);
                }
            }

            ImGui.endCombo();
        }
    }

}
