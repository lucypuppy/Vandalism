package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import imgui.internal.ImGui;
import net.minecraft.client.gui.DrawContext;

public class DemoImGuiMenu extends ImGuiMenu {

    public DemoImGuiMenu() {
        super("Demo");
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.showDemoWindow();
    }

}
