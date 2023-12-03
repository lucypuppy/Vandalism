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
    private final List<Value<?>> values;
    public int x, y, width, height;
    public double absoluteX, absoluteY;
    public boolean dragged;
    public ElementAlignment alignmentX, alignmentY;

    protected Element(final String name) {
        this.name = name;
        this.values = new ArrayList<>();
        this.alignmentX = ElementAlignment.LEFT;
        this.alignmentY = ElementAlignment.TOP;
    }

    public abstract void render(final DrawContext context, final float delta);

    public void calculateAlignment() {
        if (this.absoluteX > 0.66) {
            this.alignmentX = ElementAlignment.RIGHT;
        } else if (this.absoluteX > 0.33) {
            this.alignmentX = ElementAlignment.MIDDLE;
        } else {
            this.alignmentX = ElementAlignment.LEFT;
        }
        if (this.absoluteY > 0.66) {
            this.alignmentY = ElementAlignment.BOTTOM;
        } else if (this.absoluteY > 0.33) {
            this.alignmentY = ElementAlignment.MIDDLE;
        } else {
            this.alignmentY = ElementAlignment.TOP;
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
