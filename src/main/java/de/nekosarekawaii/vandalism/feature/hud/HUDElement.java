/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.util.common.AlignmentX;
import de.nekosarekawaii.vandalism.util.common.AlignmentY;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HUDElement implements IName, ValueParent, MinecraftWrapper {

    protected static final AlignmentX DEFAULT_ALIGNMENT_X = AlignmentX.LEFT;
    protected static final AlignmentY DEFAULT_ALIGNMENT_Y = AlignmentY.TOP;

    private final String name;
    private final List<Value<?>> values;
    private final BooleanValue active;

    @Setter
    @Getter
    protected int width, height;

    @Getter
    @Setter
    protected AlignmentX alignmentX;

    @Getter
    @Setter
    protected AlignmentY alignmentY;

    public HUDElement(final String name) {
        this(name, true);
    }

    public HUDElement(final String name, final boolean defaultActive) {
        this.name = name;
        this.values = new ArrayList<>();
        this.active = new BooleanValue(
                this,
                "Active",
                "Whether this HUD element is active.",
                defaultActive
        );
        this.width = 10;
        this.height = 10;
        this.alignmentX = DEFAULT_ALIGNMENT_X;
        this.alignmentY = DEFAULT_ALIGNMENT_Y;
    }

    public void reset() {
        this.alignmentX = DEFAULT_ALIGNMENT_X;
        this.alignmentY = DEFAULT_ALIGNMENT_Y;
        this.resetValues();
    }

    protected void resetValues() {
        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
    }

    public int getX() {
        final int x;
        switch (this.alignmentX) {
            case RIGHT -> x = this.mc.getWindow().getScaledWidth() - this.width - 2;
            case LEFT -> x = 2;
            default -> {
                final int windowWidth = this.mc.getWindow().getScaledWidth();
                x = (windowWidth - this.width) / 2;
            }
        }
        return x;
    }

    public int getY() {
        final int y;
        switch (this.alignmentY) {
            case BOTTOM -> y = this.mc.getWindow().getScaledHeight() - this.height - 2;
            case TOP -> y = 2;
            default -> {
                final int windowHeight = this.mc.getWindow().getScaledHeight();
                y = (windowHeight - this.height) / 2;
            }
        }
        return y;
    }

    protected abstract void onRender(final DrawContext context, final float delta, final boolean inGame);

    public void render(final DrawContext context, final float delta, final boolean inGame) {
        if (!inGame) {
            if (!this.isActive()) {
                RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
            }
            final int borderPosX = this.getX() - 2;
            final int borderPosY = this.getY() - 2;
            final int borderSizeX = this.width + 4;
            final int borderSizeY = this.height + 3;
            context.drawBorder(
                    borderPosX,
                    borderPosY,
                    borderSizeX,
                    borderSizeY,
                    Color.WHITE.getRGB()
            );
        }
        this.onRender(context, delta, inGame);
        if (!inGame) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

    public boolean isActive() {
        return this.active.getValue();
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
