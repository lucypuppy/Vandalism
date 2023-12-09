package de.vandalismdevelopment.vandalism.base.value.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.Value;
import imgui.ImGui;

import java.util.Arrays;
import java.util.List;

public class ListValue extends Value<String> {

    private final List<String> values;

    public ListValue(final String name, final String description, final IValue parent, final String... values) {
        super(name, description, parent, "list", values[0]);
        this.values = Arrays.asList(values);
    }

    protected ListValue(final String name, final String description, final IValue parent, final String listType, final String... values) {
        super(name, description, parent, listType + " list", values[0]);
        this.values = Arrays.asList(values);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        String value = valueObject.get("value").getAsString();
        if (!this.values.contains(value)) {
            value = this.getDefaultValue();
            Vandalism.getInstance().getLogger().error(
                    "Could not find list value with the name '" + value + "' for the value config '" +
                            this.getParent().getValueName() +
                            "' resetting it to the default value '" + this.getDefaultValue() + "'."
            );
        }
        this.setValue(value);
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", this.getValue());
    }

    @Override
    public void render() {
        if (ImGui.beginCombo("##" + this.getSaveIdentifier(), this.getValue())) {
            for (final String mode : this.values) {
                if (ImGui.selectable(mode, mode.equals(this.getValue()))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
