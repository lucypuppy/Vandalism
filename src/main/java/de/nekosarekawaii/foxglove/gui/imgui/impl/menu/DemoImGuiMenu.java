package de.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import de.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
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
