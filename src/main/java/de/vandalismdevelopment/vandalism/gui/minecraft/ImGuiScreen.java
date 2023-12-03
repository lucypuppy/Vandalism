package de.vandalismdevelopment.vandalism.gui.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.CustomHudImGuiMenu;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
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
            final CustomHudImGuiMenu customHudImGuiMenu = Vandalism.getInstance()
                    .getImGuiHandler().getImGuiMenuRegistry()
                    .getImGuiMenuByClass(CustomHudImGuiMenu.class);
            if (!customHudImGuiMenu.getState()) {
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
                        imGuiMenu.render(context, mouseX, mouseY, delta);
                    }
                }
            } else {
                customHudImGuiMenu.render(context, mouseX, mouseY, delta);
            }
        });
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
            if (imGuiMenu.getState()) {
                imGuiMenu.onMouseButton(mouseX, mouseY, button, false);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
            if (imGuiMenu.getState()) {
                imGuiMenu.onMouseButton(mouseX, mouseY, button, true);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (this.player() == null && this.prevScreen == null) {
            return;
        }
        this.setScreen(this.prevScreen);
    }

}