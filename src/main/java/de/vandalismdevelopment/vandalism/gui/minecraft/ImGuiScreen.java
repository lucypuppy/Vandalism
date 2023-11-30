package de.vandalismdevelopment.vandalism.gui.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImGuiScreen extends Screen implements MinecraftWrapper {

    private final Screen prevScreen;

    public ImGuiScreen(final Screen prevScreen) {
        super(Text.literal("ImGUI"));
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (ImGui.beginMainMenuBar()) {
                for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
                    if (ImGui.button(imGuiMenu.getName() + "##barbutton" + imGuiMenu.getName())) {
                        imGuiMenu.toggle();
                    }
                }
                ImGui.endMainMenuBar();
            }
            for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
                if (imGuiMenu.getState()) {
                    imGuiMenu.render();
                }
            }
        });
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.player() == null && this.prevScreen == null) {
            return;
        }
        this.setScreen(this.prevScreen);
    }

}