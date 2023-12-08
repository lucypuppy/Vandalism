package de.vandalismdevelopment.vandalism.gui.imgui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenuCategory;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.CustomHUDConfigImGuiMenu;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

//TODO: Fix performance when scanning for menus.
public class ImGuiScreen extends Screen implements MinecraftWrapper {

    private final Screen prevScreen;

    public ImGuiScreen(final Screen prevScreen) {
        super(Text.literal("ImGUI"));
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            final CustomHUDConfigImGuiMenu customHudConfigImGuiMenu = Vandalism.getInstance()
                    .getImGuiHandler().getImGuiMenuCategoryRegistry()
                    .getImGuiMenuByClass(CustomHUDConfigImGuiMenu.class);
            if (!customHudConfigImGuiMenu.getState()) {
                final List<ImGuiMenuCategory> imGuiMenuCategories = Vandalism.getInstance()
                        .getImGuiHandler()
                        .getImGuiMenuCategoryRegistry()
                        .getImGuiMenuCategories();
                if (ImGui.beginMainMenuBar()) {
                    for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
                        if (ImGui.beginMenu(imGuiMenuCategory.getName() + "##barmenu" + imGuiMenuCategory.getName())) {
                            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                                if (ImGui.menuItem(imGuiMenu.getName() + "##barbutton" + imGuiMenu.getName())) {
                                    imGuiMenu.toggle();
                                }
                            }
                            ImGui.endMenu();
                        }

                    }
                    ImGui.endMainMenuBar();
                }
                for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
                    for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                        if (imGuiMenu.getState()) {
                            imGuiMenu.render(context, mouseX, mouseY, delta);
                        }
                    }
                }
            } else {
                customHudConfigImGuiMenu.render(context, mouseX, mouseY, delta);
            }
        });
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final List<ImGuiMenuCategory> imGuiMenuCategories = Vandalism.getInstance()
                .getImGuiHandler()
                .getImGuiMenuCategoryRegistry()
                .getImGuiMenuCategories();
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                if (imGuiMenu.getState()) {
                    imGuiMenu.mouseClick(mouseX, mouseY, button, false);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        final List<ImGuiMenuCategory> imGuiMenuCategories = Vandalism.getInstance()
                .getImGuiHandler()
                .getImGuiMenuCategoryRegistry()
                .getImGuiMenuCategories();
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                if (imGuiMenu.getState()) {
                    imGuiMenu.mouseClick(mouseX, mouseY, button, true);
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final List<ImGuiMenuCategory> imGuiMenuCategories = Vandalism.getInstance()
                .getImGuiHandler()
                .getImGuiMenuCategoryRegistry()
                .getImGuiMenuCategories();
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                if (imGuiMenu.getState()) {
                    imGuiMenu.keyPress(keyCode, scanCode, modifiers, false);
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        final List<ImGuiMenuCategory> imGuiMenuCategories = Vandalism.getInstance()
                .getImGuiHandler()
                .getImGuiMenuCategoryRegistry()
                .getImGuiMenuCategories();
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                if (imGuiMenu.getState()) {
                    imGuiMenu.keyPress(keyCode, scanCode, modifiers, true);
                }
            }
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