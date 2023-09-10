package de.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.nekosarekawaii.foxglove.value.IValue;
import de.nekosarekawaii.foxglove.value.Value;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class MultiselectionValue extends Value<ArrayList<String>> {

    private final List<String> existingValues;

    public MultiselectionValue(final String name, final String description, final IValue parent, final String[] existingValues, final String... selectedValues) {
        super(name, description, parent, selectedValues == null ? new ArrayList<>() : new ArrayList<>(List.of(selectedValues)));
        this.existingValues = List.of(existingValues);
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        final JsonArray jsonArray = valueObject.get("value").getAsJsonArray();
        this.setValue(new ArrayList<>(jsonArray.asList().stream().map(JsonElement::getAsString).toList()));
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        final JsonArray jsonArray = new JsonArray();
        for (final String value : this.getValue()) {
            jsonArray.add(value);
        }
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