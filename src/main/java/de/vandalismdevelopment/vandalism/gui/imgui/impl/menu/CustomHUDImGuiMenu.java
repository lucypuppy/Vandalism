package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.ingame.CustomHUDSystem;
import de.vandalismdevelopment.vandalism.gui.ingame.Element;
import de.vandalismdevelopment.vandalism.util.MouseUtils;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.awt.*;

public class CustomHUDImGuiMenu extends ImGuiMenu {

    private boolean mouseDown;
    private double lastMouseX, lastMouseY;

    public CustomHUDImGuiMenu() {
        super("Custom HUD");
        this.mouseDown = false;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final CustomHUDSystem customHUDSystem = Vandalism.getInstance().getCustomHUDSystem();
        final double deltaX = mouseX - this.lastMouseX, deltaY = mouseY - this.lastMouseY;
        ImGui.begin("Custom HUD##customhud");
        if (ImGui.button("Close Custom HUD Config##closecustomhud")) {
            this.toggle();
        }
        ImGui.end();
        for (final Element element : customHUDSystem.getElements()) {
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            final boolean mouseOver = MouseUtils.isHovered(mouseX,
                    mouseY,
                    element.x - 2,
                    element.y - 2,
                    element.width + 4,
                    element.height + 3
            );
            if (this.mouseDown) {
                if (mouseOver) {
                    element.dragged = true;
                }
                if (element.dragged) {
                    final Window window = this.window();
                    final double
                            scaledWindowWidth = window.getScaledWidth(),
                            scaledWindowHeight = window.getScaledHeight(),
                            remainingWidth = scaledWindowWidth - element.width,
                            remainingHeight = scaledWindowHeight - element.height,
                            absoluteX = (element.x + deltaX) / remainingWidth,
                            absoluteY = (element.y + deltaY) / remainingHeight;
                    final int x = (int) (absoluteX * remainingWidth);
                    final int y = (int) (absoluteY * remainingHeight);
                    if (x + element.width < scaledWindowWidth && y + element.height < scaledWindowHeight && x > 0 && y > 0) {
                        element.absoluteX = absoluteX;
                        element.absoluteY = absoluteY;
                        element.x = x;
                        element.y = y;
                        element.calculateAlignment();
                    }
                }
            } else {
                element.dragged = false;
            }
            context.drawBorder(
                    element.x - 2,
                    element.y - 2,
                    element.width + 4,
                    element.height + 3,
                    mouseOver || element.dragged ? Color.RED.getRGB() : Color.WHITE.getRGB()
            );
            element.render(context, delta);
            if (!element.isEnabled()) {
                RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
            }
        }
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
