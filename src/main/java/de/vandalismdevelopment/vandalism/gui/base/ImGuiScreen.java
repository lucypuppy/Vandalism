package de.vandalismdevelopment.vandalism.gui.base;

import de.vandalismdevelopment.vandalism.gui.ImGuiManager;
import de.vandalismdevelopment.vandalism.gui.ImWindow;
import de.vandalismdevelopment.vandalism.gui.loader.ImLoader;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImGuiScreen extends Screen {

    private final ImGuiManager imGuiManager;
    private final Screen prevScreen;

    public ImGuiScreen(final ImGuiManager imGuiManager, final Screen prevScreen) {
        super(Text.literal("ImGUI"));

        this.imGuiManager = imGuiManager;
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImLoader.draw(() -> {
            for (ImWindow window : this.imGuiManager.getList()) {
                if (ImGui.beginMenuBar()) {
                    for (ImWindow.Category category : this.imGuiManager.getCategories()) {
                        if (ImGui.beginMenu(category.getName())) {
                            for (ImWindow imWindow : this.imGuiManager.getByCategory(category)) {
                                if (ImGui.checkbox(imWindow.getName(), imWindow.isActive())) {
                                    imWindow.toggle();
                                }
                            }
                            ImGui.endMenu();
                        }
                    }
                    ImGui.endMenuBar();
                }
                if (window.isActive()) {
                    window.render(context, mouseX, mouseY, delta);
                }
            }
        });
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ImWindow window : this.imGuiManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ImWindow window : this.imGuiManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        for (ImWindow window : this.imGuiManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.keyPressed(keyCode, scanCode, modifiers, false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        for (ImWindow window : this.imGuiManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.keyPressed(keyCode, scanCode, modifiers, true);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        if (this.client.player == null && this.prevScreen == null) {
            return;
        }
        this.client.setScreen(this.prevScreen);
    }

}
