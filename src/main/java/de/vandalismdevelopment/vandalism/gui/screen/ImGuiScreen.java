package de.vandalismdevelopment.vandalism.gui.screen;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImGuiScreen extends Screen {

    private final Screen prevScreen;

    public ImGuiScreen(final Screen prevScreen) {
        super(Text.literal("ImGui"));
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (ImGui.beginMainMenuBar()) {
                for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
                    if (ImGui.button(imGuiMenu.getName() + "##barbutton")) {
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
        if (this.client != null) {
            if (this.client.player == null && this.prevScreen == null) {
                return;
            }
            this.client.setScreen(this.prevScreen);
        }
    }

}