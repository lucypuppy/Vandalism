package de.vandalismdevelopment.vandalism.base.value.impl.selection;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import imgui.ImGui;

public class EnumModeValue<T extends IName> extends Value<T> {

    private final T[] options;

    @SafeVarargs
    public EnumModeValue(ValueParent parent, String name, String description, T defaultValue, final T... options) {
        super(parent, name, description, defaultValue);

        this.options = options;
    }

    @Override
    public void load(final JsonObject mainNode) {
        final String selectedOption = mainNode.get(getName()).getAsString();
        for (final T value : this.options) {
            if (value.getName().equals(selectedOption)) {
                this.setValue(value);
                break;
            }
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(getName(), this.getValue().getName());
    }

    @Override
    public void render() {
        if (ImGui.beginCombo("##" + this.getName(), this.getValue().getName())) {
            for (final T mode : this.options) {
                if (ImGui.selectable(mode.getName(), mode.getName().equals(this.getValue().getName()))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
