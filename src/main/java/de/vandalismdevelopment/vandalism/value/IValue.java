package de.vandalismdevelopment.vandalism.value;

import de.vandalismdevelopment.vandalism.config.Config;
import imgui.ImGui;

import java.util.List;

public interface IValue {

    List<Value<?>> getValues();

    Config getConfig();

    default Value<?> getValue(final String name) {
        for (final Value<?> value : this.getValues()) {
            if (value.getSaveIdentifier().equals(name)) {
                return value;
            }
        }

        return null;
    }

    default void renderValues() {
        for (final Value<?> value : this.getValues()) {
            if (value.isVisible() != null && !value.isVisible().getAsBoolean())
                continue;

            value.render();

            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text(value.getDescription());
                ImGui.endTooltip();
            }
        }
    }

    String getValueName();

}
