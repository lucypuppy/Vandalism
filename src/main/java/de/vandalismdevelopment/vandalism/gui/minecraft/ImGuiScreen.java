package de.vandalismdevelopment.vandalism.gui.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class ImGuiScreen extends Screen implements MinecraftWrapper {

    private final Screen prevScreen;
    private boolean hideMenuBar;

    public ImGuiScreen(final Screen prevScreen) {
        super(Text.literal("ImGUI"));
        this.prevScreen = prevScreen;
        this.hideMenuBar = false;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        context.drawText(mc().textRenderer,
                Formatting.YELLOW + "Hide MenuBar by holding key " + Formatting.DARK_AQUA +
                        Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.hideMenuBarKey.getValue().normalName()
                        + Formatting.YELLOW + ".",
                3, (window().getScaledHeight() - 3) - mc().textRenderer.fontHeight,
                -1,
                true);

        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (!this.hideMenuBar) {
                if (ImGui.beginMainMenuBar()) {
                    for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
                        if (ImGui.button(imGuiMenu.getName() + "##barbutton" + imGuiMenu.getName())) {
                            imGuiMenu.toggle();
                        }
                    }
                    ImGui.endMainMenuBar();
                }
            }
            for (final ImGuiMenu imGuiMenu : Vandalism.getInstance().getImGuiHandler().getImGuiMenuRegistry().getImGuiMenus()) {
                if (imGuiMenu.getState()) {
                    imGuiMenu.render(context, mouseX, mouseY, delta);
                }
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
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.hideMenuBarKey.getValue().getKeyCode()) {
            this.hideMenuBar = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.hideMenuBarKey.getValue().getKeyCode()) {
            this.hideMenuBar = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        if (this.player() == null && this.prevScreen == null) {
            return;
        }
        this.setScreen(this.prevScreen);
    }

}