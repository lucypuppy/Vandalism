package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.ingame.CustomHUDRenderer;
import de.vandalismdevelopment.vandalism.gui.ingame.HUDElement;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.awt.*;

public class CustomHUDConfigImGuiMenu extends ImGuiMenu {

    private boolean mouseDown;
    private double lastMouseX, lastMouseY;

    public CustomHUDConfigImGuiMenu() {
        super("Custom HUD Config");
        this.mouseDown = false;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final CustomHUDRenderer customHUDRenderer = Vandalism.getInstance().getCustomHUDRenderer();
        if (ImGui.begin("Custom HUD Config##customhudconfig", Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags())) {
            if (ImGui.button("Close Custom HUD Config##closecustomhudconfig")) {
                this.toggle();
            }
            if (ImGui.button("Reset Custom HUD Config##resetcustomhudconfig")) {
                for (final HUDElement hudElement : Vandalism.getInstance().getCustomHUDRenderer().getHudElements()) {
                    hudElement.reset();
                }
                try {
                    Vandalism.getInstance().getConfigManager().getCustomHUDConfig().save();
                } catch (final Exception e) {
                    Vandalism.getInstance().getLogger().error("Failed to save custom hud config.", e);
                }
            }
            ImGui.separator();
            for (final HUDElement hudElement : Vandalism.getInstance().getCustomHUDRenderer().getHudElements()) {
                if (ImGui.treeNodeEx(hudElement.getName() + "##" + hudElement.getName() + "customhudconfig")) {
                    if (ImGui.button("Reset##reset" + hudElement.getName() + "customhudconfig")) {
                        hudElement.reset();
                        try {
                            Vandalism.getInstance().getConfigManager().getCustomHUDConfig().save();
                        } catch (final Exception e) {
                            Vandalism.getInstance().getLogger().error("Failed to save custom hud config.", e);
                        }
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
        final Window window = this.window();
        final double scaledWidth = window.getScaledWidth(), scaledHeight = window.getScaledHeight();
        for (final HUDElement hudElement : customHUDRenderer.getHudElements()) {
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
    public void mouseClick(final double mouseX, final double mouseY, final int button, final boolean release) {
        if (button == 0) {
            this.mouseDown = !release;
            if (release) {
                boolean save = false;
                for (final HUDElement hudElement : Vandalism.getInstance().getCustomHUDRenderer().getHudElements()) {
                    if (hudElement.shouldSave) {
                        hudElement.shouldSave = false;
                        save = true;
                    }
                }
                if (save) {
                    try {
                        Vandalism.getInstance().getConfigManager().getCustomHUDConfig().save();
                    } catch (final Exception e) {
                        Vandalism.getInstance().getLogger().error("Failed to save custom hud config.", e);
                    }
                }
            }
        }
    }

}
