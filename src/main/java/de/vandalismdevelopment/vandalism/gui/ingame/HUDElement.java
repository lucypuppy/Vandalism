package de.vandalismdevelopment.vandalism.gui.ingame;

import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class HUDElement implements IName, IValue, MinecraftWrapper {

    private final String name;
    private final List<Value<?>> values;
    private final BooleanValue enabled;
    public int x, y, width, height;
    public double absoluteX, absoluteY;
    public boolean dragged;
    public HUDElementAlignment alignmentX, alignmentY;

    protected HUDElement(final String name) {
        this.name = name;
        this.values = new ArrayList<>();
        this.enabled = new BooleanValue(
                "Enabled",
                "Whether this HUD element is enabled.",
                this,
                true
        );
        this.resetPosition();
    }

    public abstract void render(final DrawContext context, final float delta);

    public void setScreenPosition(final int x, final int y) {
        final Window window = this.window();
        final double
                scaledWindowWidth = window.getScaledWidth(),
                scaledWindowHeight = window.getScaledHeight(),
                remainingWidth = scaledWindowWidth - width,
                remainingHeight = scaledWindowHeight - height;

        this.absoluteX = x / remainingWidth;
        this.absoluteY = y / remainingHeight;
        this.x = (int) (this.absoluteX * remainingWidth);
        this.y = (int) (this.absoluteY * remainingHeight);
        try {
            Vandalism.getInstance().getConfigManager().getCustomHUDConfig().save();
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Failed to save custom hud config.", throwable);
        }
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

    public void calculatePosition() {
        switch (this.alignmentX) {
            default -> this.x = (int) (this.absoluteX * this.window().getScaledWidth()); //Left
            case MIDDLE ->
                    this.x = (int) (this.absoluteX * (this.window().getScaledWidth() - this.width / 2.0f)); //Middle
            case RIGHT -> this.x = (int) (this.absoluteX * (this.window().getScaledWidth() - this.width)); //Right
        }
        switch (this.alignmentY) {
            default -> this.y = (int) (this.absoluteY * this.window().getScaledHeight()); //Up
            case MIDDLE ->
                    this.y = (int) (this.absoluteY * (this.window().getScaledHeight() - this.height / 2.0f)); //Middle
            case BOTTOM -> this.y = (int) (this.absoluteY * (this.window().getScaledHeight() - this.height)); //Down
        }
    }

    protected void resetPosition() {
        this.alignmentX = HUDElementAlignment.LEFT;
        this.alignmentY = HUDElementAlignment.TOP;
        this.calculatePosition();
    }

    public void reset() {
        this.resetPosition();
        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
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
