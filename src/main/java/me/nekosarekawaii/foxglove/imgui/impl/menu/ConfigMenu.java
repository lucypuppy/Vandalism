package me.nekosarekawaii.foxglove.imgui.impl.menu;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.value.Value;

public class ConfigMenu {

    public static void render() {
        if (ImGui.begin("Config", ImGuiWindowFlags.NoCollapse)) {
            ImGui.setWindowSize(0, 0);
            if (ImGui.beginListBox("###config", 600, 500)) {
                final ObjectArrayList<Value<?>> values = Foxglove.getInstance().getConfigManager().getMainConfig().getValues();

                if (ImGui.button("Reset Config")) {
                    for (final Value<?> value : values) {
                        value.resetValue();
                    }
                }

                ImGui.newLine();

                for (final Value<?> value : values) {
                    if (value.isVisible() != null && !value.isVisible().getAsBoolean())
                        continue;

                    value.render();
                }

                ImGui.endListBox();
            }

            ImGui.end();
        }
    }

}
