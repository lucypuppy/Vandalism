package de.nekosarekawaii.vandalism.clientmenu.base;

import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.util.imgui.ImLoader;
import de.nekosarekawaii.vandalism.integration.hud.gui.HUDClientMenuWindow;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClientMenuScreen extends Screen {

    private final ClientMenuManager clientMenuManager;
    private final Screen prevScreen;

    public ClientMenuScreen(final ClientMenuManager clientMenuManager, final Screen prevScreen) {
        super(Text.literal("ImGUI"));
        this.clientMenuManager = clientMenuManager;
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        ImLoader.draw(() -> {
            final HUDClientMenuWindow hudImWindow = this.clientMenuManager.getByClass(HUDClientMenuWindow.class);
            if (hudImWindow.isActive()) {
                hudImWindow.render(context, mouseX, mouseY, delta);
            } else {
                if (ImGui.beginMainMenuBar()) {
                    for (final ClientMenuWindow.Category category : this.clientMenuManager.getCategories()) {
                        if (ImGui.beginMenu(category.getName())) {
                            for (final ClientMenuWindow clientMenuWindow : this.clientMenuManager.getByCategory(category)) {
                                if (ImGui.checkbox(clientMenuWindow.getName(), clientMenuWindow.isActive())) {
                                    clientMenuWindow.toggle();
                                }
                            }
                            ImGui.endMenu();
                        }
                    }
                    ImGui.endMainMenuBar();
                }
                for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
                    if (window.isActive()) {
                        window.render(context, mouseX, mouseY, delta);
                    }
                }
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.keyPressed(keyCode, scanCode, modifiers, false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
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
