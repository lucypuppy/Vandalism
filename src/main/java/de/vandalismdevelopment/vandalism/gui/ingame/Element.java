package de.vandalismdevelopment.vandalism.gui.ingame;

import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Element implements IName, IValue, MinecraftWrapper {

    private final String name;
    public int x, y, width, height;
    public double absoluteX, absoluteY;
    public boolean dragged;
    private final List<Value<?>> values;
    public ElementAlignment alignmentX, alignmentY;

    protected Element(final String name) {
        this.name = name;
        this.values = new ArrayList<>();
        this.alignmentX = ElementAlignment.LEFT;
        this.alignmentY = ElementAlignment.TOP;
    }

    public abstract void render(final DrawContext context, final float delta);

    public void calculateAlignment() {
        if (absoluteX > 0.66) {
            alignmentX = ElementAlignment.RIGHT;
        } else if (absoluteX > 0.33) {
            alignmentX = ElementAlignment.MIDDLE;
        } else {
            alignmentX = ElementAlignment.LEFT;
        }

        if (absoluteY > 0.66) {
            alignmentY = ElementAlignment.BOTTOM;
        } else if (absoluteY > 0.33) {
            alignmentY = ElementAlignment.MIDDLE;
        } else {
            alignmentY = ElementAlignment.TOP;
        }
    }

    public void calculatePosition() {
        switch (alignmentX) {
            default -> x = (int) (absoluteX * this.window().getScaledWidth()); //Left
            case MIDDLE -> x = (int) (absoluteX * (this.window().getScaledWidth() - this.width / 2.0f)); //Middle
            case RIGHT -> x = (int) (absoluteX * (this.window().getScaledWidth() - this.width)); //Right
        }

        switch (alignmentY) {
            default -> y = (int) (absoluteY * this.window().getScaledHeight()); //Up
            case MIDDLE -> y = (int) (absoluteY * (this.window().getScaledHeight() - this.height / 2.0f)); //Middle
            case BOTTOM -> y = (int) (absoluteY * (this.window().getScaledHeight() - this.height)); //Down
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public Config getConfig() {
        return null;// Vandalism.getInstance().getConfigManager().getHudConfig();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValueName() {
        return getName();
    }

}
