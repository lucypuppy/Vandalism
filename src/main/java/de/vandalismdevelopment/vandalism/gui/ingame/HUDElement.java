package de.vandalismdevelopment.vandalism.gui.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.rclasses.math.integration.Boundings;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HUDElement implements IName, IValue, MinecraftWrapper {

    private final String name;
    private final List<Value<?>> values;
    private final BooleanValue enabled;
    public double absoluteX;
    public double absoluteY;
    public boolean shouldSave;
    private boolean dragged;
    private final int defaultX, defaultY;
    protected int x, y, width, height;
    protected HUDElementAlignment alignmentX, alignmentY;

    protected HUDElement(final String name, final int defaultX, final int defaultY) {
        this.name = name;
        this.values = new ArrayList<>();
        this.enabled = new BooleanValue(
                "Enabled",
                "Whether this HUD element is enabled.",
                this,
                true
        );
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.updateScreenPosition(this.defaultX, this.defaultY);
    }

    public void reset() {
        this.updateScreenPosition(this.defaultX, this.defaultY);
        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
    }

    public void updateScreenPosition(final double x, final double y) {
        final Window window = this.window();
        final double scaledWindowWidth = window.getScaledWidth();
        final double scaledWindowHeight = window.getScaledHeight();
        final double remainingWidth = scaledWindowWidth - this.width;
        final double remainingHeight = scaledWindowHeight - this.height;
        this.absoluteX = x / remainingWidth;
        this.absoluteY = y / remainingHeight;
        this.x = (int) (this.absoluteX * remainingWidth);
        this.y = (int) (this.absoluteY * remainingHeight);
        this.calculateAlignment();
    }

    public void calculateAlignment() {
        if (this.absoluteX > 0.66) {
            this.alignmentX = HUDElementAlignment.RIGHT;
        } else if (this.absoluteX > 0.33) {
            this.alignmentX = HUDElementAlignment.MIDDLE;
        } else {
            this.alignmentX = HUDElementAlignment.LEFT;
        }
        if (this.absoluteY > 0.66) {
            this.alignmentY = HUDElementAlignment.BOTTOM;
        } else if (this.absoluteY > 0.33) {
            this.alignmentY = HUDElementAlignment.MIDDLE;
        } else {
            this.alignmentY = HUDElementAlignment.TOP;
        }
    }

    protected abstract void onRender(final DrawContext context, final float delta);

    public void render(
            final boolean mouseDown,
            final int mouseX,
            final int mouseY,
            final double mouseDeltaX,
            final double mouseDeltaY,
            final double scaledWidth,
            final double scaledHeight,
            final DrawContext context,
            final float delta
    ) {
        if (!this.isEnabled()) {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.9F);
        }
        final boolean mouseOver = Boundings.isInBounds(
                mouseX,
                mouseY,
                this.x - 2,
                this.y - 2,
                this.width + 4,
                this.height + 3
        );
        if (mouseDown) {
            if (mouseOver) {
                this.dragged = true;
            }
            if (this.dragged) {
                final double
                        remainingWidth = scaledWidth - this.width,
                        remainingHeight = scaledHeight - this.height,
                        absoluteX = (this.x + mouseDeltaX) / remainingWidth,
                        absoluteY = (this.y + mouseDeltaY) / remainingHeight;
                final int x = (int) (absoluteX * remainingWidth);
                final int y = (int) (absoluteY * remainingHeight);
                if (x + this.width < scaledWidth && y + this.height < scaledHeight && x > 0 && y > 0) {
                    this.absoluteX = absoluteX;
                    this.absoluteY = absoluteY;
                    this.x = x;
                    this.y = y;
                    this.calculateAlignment();
                    this.shouldSave = true;
                }
            }
        } else {
            this.dragged = false;
        }
        final int borderPosX = this.x - 2;
        final int borderPosY = this.y - 2;
        final int borderSizeX = this.width + 4;
        final int borderSizeY = this.height + 3;
        final boolean show = mouseOver || this.dragged;
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
        this.onRender(context, delta);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public Config getConfig() {
        return Vandalism.getInstance().getConfigManager().getCustomHUDConfig();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

}
