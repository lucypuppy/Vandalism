package de.foxglovedevelopment.foxglove.gui.imgui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.event.KeyboardListener;
import de.foxglovedevelopment.foxglove.event.RenderListener;
import de.foxglovedevelopment.foxglove.gui.imgui.impl.widget.NBTEditWidget;
import de.foxglovedevelopment.foxglove.util.MinecraftWrapper;
import imgui.flag.ImGuiHoveredFlags;
import imgui.internal.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiHandler implements KeyboardListener, RenderListener, MinecraftWrapper {

    private boolean render, hovered;

    private final ImGuiRenderer imGuiRenderer;

    private final ImGuiMenuRegistry imGuiMenuRegistry;

    private final NBTEditWidget nbtEditWidget;

    public ImGuiHandler(final File dir) {
        this.render = false;
        this.hovered = false;
        this.imGuiRenderer = new ImGuiRenderer(dir);
        this.imGuiMenuRegistry = new ImGuiMenuRegistry();
        this.nbtEditWidget = new NBTEditWidget();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        Foxglove.getInstance().getLogger().info("ImGui loaded.");
    }

    private void renderImGuiContext() {
        this.imGuiRenderer.addRenderInterface(io -> {
            if (this.render && !mouse().isCursorLocked()) {
                if (ImGui.beginMainMenuBar()) {
                    for (final ImGuiMenu imGuiMenu : this.imGuiMenuRegistry.getImGuiMenus()) {
                        if (ImGui.button(imGuiMenu.getName() + "##barbutton")) {
                            imGuiMenu.toggle();
                        }
                    }
                    ImGui.endMainMenuBar();
                }
                boolean renderingAMenu = false;
                for (final ImGuiMenu imGuiMenu : this.imGuiMenuRegistry.getImGuiMenus()) {
                    if (imGuiMenu.getState()) {
                        renderingAMenu = true;
                        imGuiMenu.render();
                    }
                }
                if (renderingAMenu) this.hovered = ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
                else this.hovered = false;
            }
            this.nbtEditWidget.render();
        });
        this.imGuiRenderer.render();
    }

    public ImGuiRenderer getImGuiRenderer() {
        return this.imGuiRenderer;
    }

    public NBTEditWidget getNbtEditWidget() {
        return this.nbtEditWidget;
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        if (key == this.getKey()) {
            this.render = !this.render;
            if (this.render && player() != null && mouse().isCursorLocked()) {
                mc().execute(() -> setScreen(new ChatScreen("")));
            }
        }
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (currentScreen() != null) {
            this.renderImGuiContext();
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (currentScreen() == null) {
            this.renderImGuiContext();
        }
    }

    public int getKey() {
        return Foxglove.getInstance().getConfigManager().getMainConfig().menuBarKey.getValue().left();
    }

    public boolean isHovered() {
        return this.render && this.hovered;
    }

}
