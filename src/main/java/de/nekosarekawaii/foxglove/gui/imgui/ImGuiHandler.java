package de.nekosarekawaii.foxglove.gui.imgui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.KeyboardListener;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.gui.imgui.impl.widget.NBTEditWidget;
import imgui.flag.ImGuiHoveredFlags;
import imgui.internal.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiHandler implements KeyboardListener, RenderListener {

    private boolean renderBar, hovered;

    private final ImGuiRenderer imGuiRenderer;

    private final ImGuiMenuRegistry imGuiMenuRegistry;

    private final NBTEditWidget nbtEditWidget;

    public ImGuiHandler(final File dir) {
        this.renderBar = this.hovered = false;
        this.imGuiRenderer = new ImGuiRenderer(dir);
        this.imGuiMenuRegistry = new ImGuiMenuRegistry();
        this.nbtEditWidget = new NBTEditWidget();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        Foxglove.getInstance().getLogger().info("ImGui loaded.");
    }

    private void renderImGuiContext() {
        this.imGuiRenderer.addRenderInterface(io -> {
            if (this.renderBar) {
                if (ImGui.beginMainMenuBar()) {
                    for (final ImGuiMenu imGuiMenu : this.imGuiMenuRegistry.getImGuiMenus()) {
                        if (ImGui.button(imGuiMenu.getName() + "##barbutton")) {
                            imGuiMenu.toggle();
                        }
                    }
                    ImGui.endMainMenuBar();
                }
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
        if (key == Foxglove.getInstance().getConfigManager().getMainConfig().menuBarKey.getValue().left()) {
            this.renderBar = !this.renderBar;
        }
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (MinecraftClient.getInstance().currentScreen != null) {
            this.renderImGuiContext();
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (MinecraftClient.getInstance().currentScreen == null) {
            this.renderImGuiContext();
        }
    }

    public boolean isHovered() {
        return this.hovered;
    }

}
