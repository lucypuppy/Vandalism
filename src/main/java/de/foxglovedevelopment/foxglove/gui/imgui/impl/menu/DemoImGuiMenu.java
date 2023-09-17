package de.foxglovedevelopment.foxglove.gui.imgui.impl.menu;

import de.foxglovedevelopment.foxglove.gui.imgui.ImGuiMenu;
import imgui.internal.ImGui;

public class DemoImGuiMenu extends ImGuiMenu {

    public DemoImGuiMenu() {
        super("Demo");
    }

    @Override
    public void render() {
        ImGui.showDemoWindow();
    }

}
