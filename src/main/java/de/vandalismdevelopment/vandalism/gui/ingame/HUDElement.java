package de.vandalismdevelopment.vandalism.gui.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.util.MouseUtils;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HUDElement implements IName, IValue, MinecraftWrapper {

    private final String name;
    private final List<Value<?>> values;
    private final BooleanValue enabled;
    public boolean shouldSave;
    private boolean dragged;
    private final int defaultX, defaultY;
    protected int width, height;
    public int x, y;
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
        this.x = defaultX;
        this.y = defaultY;
        calculateAlignment();
    }

    public void reset() {
        this.x = this.defaultX;
        this.y = this.defaultY;
        calculateAlignment();

        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
    }

    public void calculateAlignment() {
        final int scaledWidth = this.window().getScaledWidth();
        final int scaledHeight = this.window().getScaledHeight();

        if (scaledWidth * 0.66 < x + width / 2.0) {
            this.alignmentX = HUDElementAlignment.RIGHT;
        } else if (scaledWidth * 0.33 < x + width / 2.0) {
            this.alignmentX = HUDElementAlignment.MIDDLE;
        } else {
            this.alignmentX = HUDElementAlignment.LEFT;
        }

        if (scaledHeight * 0.66 < y + height / 2.0) {
            this.alignmentY = HUDElementAlignment.BOTTOM;
        } else if (scaledHeight * 0.33 < y + height / 2.0) {
            this.alignmentY = HUDElementAlignment.MIDDLE;
        } else {
            this.alignmentY = HUDElementAlignment.TOP;
        }
    }

    public void calculatePosition() {
        final int scaledWidth = this.window().getScaledWidth();
        final int scaledHeight = this.window().getScaledHeight();

        //Todo fix rescale
    }

    protected abstract void onRender(final DrawContext context, final float delta);

    public void render(
            final boolean mouseDown,
            final int mouseX,
            final int mouseY,
            final int mouseDeltaX,
            final int mouseDeltaY,
            final double scaledWidth,
            final double scaledHeight,
            final DrawContext context,
            final float delta
    ) {
        if (!this.isEnabled()) {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.9F);
        }

        final boolean mouseOver = MouseUtils.isHovered(
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
                final int x = this.x + mouseDeltaX,
                        y = this.y + mouseDeltaY;
                if (x + this.width < scaledWidth && y + this.height < scaledHeight && x > 0 && y > 0) {
                    this.x = x;
                    this.y = y;
                    calculateAlignment();
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