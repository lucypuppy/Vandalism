package de.vandalismdevelopment.vandalism.value.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectionValue extends Value<List<String>> {

    private final List<String> existingValues;

    private final ImString searchInput;

    public MultiSelectionValue(final String name, final String description, final IValue parent, final String[] existingValues, final String... selectedValues) {
        super(name, description, parent, "multi selection", selectedValues == null ? new ArrayList<>() : new ArrayList<>(List.of(selectedValues)));
        this.existingValues = List.of(existingValues);
        this.searchInput = new ImString();
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
        if (ImGui.beginCombo("##" + this.getSaveIdentifier(), this.getValue().toString().substring(1, getValue().toString().length() - 1))) {
            ImGui.inputText("##" + this.getSaveIdentifier() + "search", this.searchInput);
            final String searchInputValue = this.searchInput.get();
            for (final String value : this.existingValues) {
                if (!searchInputValue.isBlank() && !StringUtils.contains(value, searchInputValue)) {
                    continue;
                }
                final boolean isSelected = this.isSelected(value);
                if (ImGui.selectable(value, isSelected)) {
                    if (isSelected) this.getValue().remove(value);
                    else this.getValue().add(value);
                }
            }
            ImGui.endCombo();
        }
    }

    public boolean isSelected(final String value) {
        return this.getValue().contains(value);
    }

}