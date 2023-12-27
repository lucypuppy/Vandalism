package de.nekosarekawaii.vandalism.base.value;

import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

import java.util.List;

public interface ValueParent extends IName {

    List<Value<?>> getValues();

    default Value<?> byName(final String name) {
        for (final Value<?> value : this.getValues()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    default void renderValues() {
        for (final Value<?> value : this.getValues()) {
            renderValue(value);
        }
    }

    default void renderValue(final Value<?> value) {
        if (value.isVisible() == null || value.isVisible().getAsBoolean()) {
            if (value instanceof ValueGroup) {
                this.renderValueDescription(value);
                ImGui.sameLine();
                value.render();
                return;
            }
            ImGui.text(value.getName());
            this.renderValueDescription(value);
            ImGui.sameLine();
            value.render();
            if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
                value.resetValue();
            }
            this.renderValueDescription(value);
        }
    }

    default void renderValueDescription(final Value<?> value) {
        if (value.getDescription() == null) {
            return;
        }
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(value.getDescription());
            ImGui.endTooltip();
        }
    }

}
