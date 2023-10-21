package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class MultiselectionValue extends Value<List<String>> {

    private final List<String> existingValues;

    public MultiselectionValue(final String name, final String description, final IValue parent, final String[] existingValues, final String... selectedValues) {
        super(name, description, parent, "multi selection", selectedValues == null ? new ArrayList<>() : new ArrayList<>(List.of(selectedValues)));
        this.existingValues = List.of(existingValues);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(new ArrayList<>(valueObject.get("value").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList()));
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        final JsonArray jsonArray = new JsonArray();
        for (final String value : this.getValue()) jsonArray.add(value);
        valueObject.add("value", jsonArray);
    }

    @Override
    public void render() {
        final String displayString = getValue().toString().substring(1, getValue().toString().length() - 1);
        if (ImGui.beginCombo(this.getName() + "##" + this.getHashIdent(), displayString)) {
            for (final String value : this.existingValues) {
                final boolean isSelected = isSelected(value);
                if (ImGui.selectable(value, isSelected)) {
                    if (isSelected) getValue().remove(value);
                    else getValue().add(value);
                }
            }
            ImGui.endCombo();
        }
    }

    public boolean isSelected(final String value) {
        return this.getValue().contains(value);
    }

}