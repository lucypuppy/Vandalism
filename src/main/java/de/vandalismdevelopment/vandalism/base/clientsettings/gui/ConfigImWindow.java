package de.vandalismdevelopment.vandalism.base.clientsettings.gui;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.gui.ImWindow;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;

public class ConfigImWindow extends ImWindow {

    private final ClientSettings clientSettings;

    public ConfigImWindow(final ClientSettings clientSettings) {
        super("Config", Category.CONFIGURATION);

        this.clientSettings = clientSettings;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(getName());
        if (ImGui.beginTabBar("config-value-categories")) {
            for (final Value<?> value : clientSettings.getValues()) {
                if (value instanceof final ValueGroup valueGroup) {
                    if (ImGui.beginTabItem(valueGroup.getName())) {
                        if (ImGui.button("Reset " + valueGroup.getName() + " Config")) {
                            for (final Value<?> valueCategoryValue : valueGroup.getValues()) {
                                valueCategoryValue.resetValue();
                            }
                        }
                        ImGui.separator();
                        ImGui.beginChild("##configvalues" + valueGroup.getName());
                        valueGroup.renderValues();
                        ImGui.endChild();
                        ImGui.endTabItem();
                    }
                } else {
                    clientSettings.renderValue(value);
                }
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

}
