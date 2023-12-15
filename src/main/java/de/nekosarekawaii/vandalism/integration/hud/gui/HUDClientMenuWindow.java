package de.nekosarekawaii.vandalism.integration.hud.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.integration.hud.HUDManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.awt.*;

public class HUDClientMenuWindow extends ClientMenuWindow {

    private final HUDManager hudManager;

    private boolean mouseDown = false;
    private int lastMouseX, lastMouseY;

    public HUDClientMenuWindow(final HUDManager hudManager) {
        super("HUD Config", Category.CONFIGURATION);

        this.hudManager = hudManager;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (ImGui.begin("Custom HUD Config##customhudconfig", ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.button("Close Custom HUD Config##closecustomhudconfig")) {
                this.setActive(false);
            }

            if (ImGui.button("Reset Custom HUD Config##resetcustomhudconfig")) {
                for (final HUDElement hudElement : this.hudManager.getList()) {
                    hudElement.reset();
                }

                Vandalism.getInstance().getConfigManager().save();
            }

            ImGui.separator();

            for (final HUDElement hudElement : hudManager.getList()) {
                if (ImGui.treeNodeEx(hudElement.getName() + "##" + hudElement.getName() + "customhudconfig")) {
                    if (ImGui.button("Reset##reset" + hudElement.getName() + "customhudconfig")) {
                        hudElement.reset();
                        Vandalism.getInstance().getConfigManager().save();
                    }

                    ImGui.spacing();
                    hudElement.renderValues();
                    ImGui.treePop();
                }
            }

            ImGui.separator();
            ImGui.spacing();
            ImGui.end();
        }

        final Window window = this.mc.getWindow();
        final double scaledWidth = window.getScaledWidth(), scaledHeight = window.getScaledHeight();

        for (final HUDElement hudElement : hudManager.getList()) {
            hudElement.render(
                    this.mouseDown,
                    mouseX,
                    mouseY,
                    mouseX - this.lastMouseX,
                    mouseY - this.lastMouseY,
                    scaledWidth,
                    scaledHeight,
                    context,
                    delta
            );
        }

        context.drawHorizontalLine(0, (int) scaledWidth, (int) (scaledHeight * 0.66), Color.green.getRGB());
        context.drawHorizontalLine(0, (int) scaledWidth, (int) (scaledHeight * 0.33), Color.green.getRGB());
        context.drawVerticalLine((int) (scaledWidth * 0.66), 0, (int) scaledHeight, Color.green.getRGB());
        context.drawVerticalLine((int) (scaledWidth * 0.33), 0, (int) scaledHeight, Color.green.getRGB());

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
        if (button == 0) {
            this.mouseDown = !release;

            if (release) {
                boolean save = false;

                for (final HUDElement hudElement : hudManager.getList()) {
                    if (hudElement.shouldSave) {
                        hudElement.shouldSave = false;
                        save = true;
                    }
                }

                if (save) {
                    Vandalism.getInstance().getConfigManager().save();
                }
            }
        }
    }

}