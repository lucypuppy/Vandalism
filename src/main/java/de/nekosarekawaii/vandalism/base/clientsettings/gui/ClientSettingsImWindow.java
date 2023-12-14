package de.nekosarekawaii.vandalism.base.clientsettings.gui;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.gui.base.ImWindow;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;

public class ClientSettingsImWindow extends ImWindow {

    private final ClientSettings clientSettings;

    public ClientSettingsImWindow(final ClientSettings clientSettings) {
        super("Client Settings", Category.CONFIGURATION);

        this.clientSettings = clientSettings;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(getName());
        if (ImGui.beginTabBar("##config")) {
            for (final Value<?> value : clientSettings.getValues()) {
                if (value instanceof final ValueGroup valueGroup) {
                    if (ImGui.beginTabItem(valueGroup.getName())) {
                        if (ImGui.button("Reset " + valueGroup.getName())) {
                            for (final Value<?> valueCategoryValue : valueGroup.getValues()) {
                                valueCategoryValue.resetValue();
                            }
                        }
                        ImGui.separator();
                        if (ImGui.beginChild("##" + valueGroup.getName())) {
                            valueGroup.renderValues();
                            ImGui.endChild();
                        }
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
