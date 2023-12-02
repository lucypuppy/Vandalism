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

    protected Element(final String name) {
        this.name = name;
        this.values = new ArrayList<>();
    }

    public abstract void render(final DrawContext context, final float delta);

    public void calculatePosition() {
        if (absoluteX > 0.66) {
            final float width = this.window().getScaledWidth() - this.width; //Right
            x = (int) (absoluteX * width);
        } else if (absoluteX > 0.33) {
            final float width = this.window().getScaledWidth() - this.width / 2.0f; //Middle
            x = (int) (absoluteX * width);
        } else {
            x = (int) (absoluteX * this.window().getScaledWidth()); //Left
        }

        if (absoluteY > 0.66) {
            final float height = this.window().getScaledHeight() - this.height; //Right
            y = (int) (absoluteY * height);
        } else if (absoluteY > 0.33) {
            final float height = this.window().getScaledHeight() - this.height / 2.0f; //Middle
            y = (int) (absoluteY * height);
        } else {
            y = (int) (absoluteY * this.window().getScaledHeight()); //Left
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
