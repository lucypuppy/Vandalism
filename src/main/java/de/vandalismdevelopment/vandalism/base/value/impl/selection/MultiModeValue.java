package de.vandalismdevelopment.vandalism.base.value.impl.selection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiModeValue extends Value<List<String>> {

    private final List<String> options;

    public MultiModeValue(ValueParent parent, String name, String description, List<String> defaultValue, final String... options) {
        super(parent, name, description, new ArrayList<>(defaultValue) /* Java's Arrays.asList() makes lists unmodifiable */);

        this.options = Arrays.stream(options).toList();
    }

    @Override
    public void load(final JsonObject mainNode) {
        final var selectedOptionsNode = mainNode.get(getName()).getAsJsonArray();
        for (JsonElement element : selectedOptionsNode) {
            this.getValue().add(element.getAsString());
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final var selectedOptionsNode = new JsonArray();
        for (final String value : this.getValue()) {
            selectedOptionsNode.add(value);
        }

        mainNode.add(getName(), selectedOptionsNode);
    }

    private final ImString searchInput = new ImString();

    @Override
    public void render() {
        if (ImGui.beginCombo("##" + this.getName(), this.getValue().toString().substring(1, getValue().toString().length() - 1))) {
            ImGui.inputText("##" + this.getName() + "search", this.searchInput);
            for (final String value : this.options) {
                if (this.searchInput.isNotEmpty() && !StringUtils.contains(value, this.searchInput.get())) {
                    continue;
                }
                final boolean isSelected = this.isSelected(value);
                if (ImGui.selectable(value, isSelected)) {
                    if (isSelected) {
                        this.getValue().remove(value);
                    } else {
                        this.getValue().add(value);
                    }
                }
            }
            ImGui.endCombo();
        }
    }

    public boolean isSelected(final String value) {
        return this.getValue().contains(value);
    }

    public boolean isSelected(final int index) {
        return this.getValue().contains(this.options.get(index));
    }

}