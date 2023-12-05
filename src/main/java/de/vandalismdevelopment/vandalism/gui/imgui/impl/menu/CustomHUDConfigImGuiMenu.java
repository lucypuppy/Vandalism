package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.ingame.CustomHUDRenderer;
import de.vandalismdevelopment.vandalism.gui.ingame.HUDElement;
import de.vandalismdevelopment.vandalism.util.MouseUtils;
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
        final double deltaX = mouseX - this.lastMouseX, deltaY = mouseY - this.lastMouseY;
        final double scaledWidth = window().getScaledWidth(), scaledHeight = window().getScaledHeight();

        if (ImGui.begin("Custom HUD Config##customhudconfig", Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags())) {
            if (ImGui.button("Close Custom HUD Config##closecustomhudconfig")) {
                this.toggle();
            }
            if (ImGui.button("Reset Custom HUD Config##resetcustomhudconfig")) {
                for (final HUDElement HUDElement : Vandalism.getInstance().getCustomHUDRenderer().getHudElements()) {
                    HUDElement.reset();
                }
            }
            ImGui.separator();
            for (final HUDElement HUDElement : Vandalism.getInstance().getCustomHUDRenderer().getHudElements()) {
                if (ImGui.treeNodeEx(HUDElement.getName() + "##" + HUDElement.getName() + "customhudconfig")) {
                    if (ImGui.button("Reset##reset" + HUDElement.getName() + "customhudconfig")) {
                        HUDElement.reset();
                    }
                    ImGui.spacing();
                    HUDElement.renderValues();
                    ImGui.treePop();
                }
            }
            ImGui.separator();
            ImGui.spacing();
            ImGui.end();
        }
        for (final HUDElement HUDElement : customHUDRenderer.getHudElements()) {
            if (!HUDElement.isEnabled()) {
                RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.9F);
            }
            final boolean mouseOver = MouseUtils.isHovered(mouseX,
                    mouseY,
                    HUDElement.x - 2,
                    HUDElement.y - 2,
                    HUDElement.width + 4,
                    HUDElement.height + 3
            );
            if (this.mouseDown) {
                if (mouseOver) {
                    HUDElement.dragged = true;
                }
                if (HUDElement.dragged) {
                    final Window window = this.window();
                    final double
                            remainingWidth = scaledWidth - HUDElement.width,
                            remainingHeight = scaledHeight - HUDElement.height,
                            absoluteX = (HUDElement.x + deltaX) / remainingWidth,
                            absoluteY = (HUDElement.y + deltaY) / remainingHeight;
                    final int x = (int) (absoluteX * remainingWidth);
                    final int y = (int) (absoluteY * remainingHeight);
                    if (x + HUDElement.width < scaledWidth && y + HUDElement.height < scaledHeight && x > 0 && y > 0) {
                        HUDElement.absoluteX = absoluteX;
                        HUDElement.absoluteY = absoluteY;
                        HUDElement.x = x;
                        HUDElement.y = y;
                        HUDElement.calculateAlignment();
                    }
                }
            } else {
                HUDElement.dragged = false;
            }

            final int
                    borderPosX = HUDElement.x - 2,
                    borderPosY = HUDElement.y - 2,
                    borderSizeX = HUDElement.width + 4,
                    borderSizeY = HUDElement.height + 3;
            final boolean show = mouseOver || HUDElement.dragged;

            if (show) {
                context.drawHorizontalLine(0, (int) scaledWidth, borderPosY, Color.CYAN.getRGB());
                context.drawHorizontalLine(0, (int) scaledWidth, borderPosY + borderSizeY - 1, Color.CYAN.getRGB());
                context.drawVerticalLine(borderPosX, 0, (int) scaledHeight, Color.CYAN.getRGB());
                context.drawVerticalLine(borderPosX + borderSizeX - 1, 0, (int) scaledHeight, Color.CYAN.getRGB());
            }

            context.drawBorder(
                    borderPosX,
                    borderPosY,
                    borderSizeX,
                    borderSizeY,
                    show ? Color.red.getRGB() : Color.WHITE.getRGB()
            );

            HUDElement.render(context, delta);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
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
        }
    }

}
