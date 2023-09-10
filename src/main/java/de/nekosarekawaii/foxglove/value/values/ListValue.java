package de.nekosarekawaii.foxglove.value.values;

import de.nekosarekawaii.foxglove.value.IValue;
import imgui.ImGui;

import java.util.Arrays;
import java.util.List;

public class ListValue extends StringValue {

    private final List<String> values;

    public ListValue(final String name, final String description, final IValue parent, final String... values) {
        super(name, description, parent, values[0]);
        this.values = Arrays.asList(values);
    }

    @Override
    public void render() {
        if (ImGui.beginCombo(this.getName() + "##" + this.getHashIdent(), getValue())) {
            for (final String mode : this.values) {
                if (ImGui.selectable(mode, mode.equals(getValue()))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
