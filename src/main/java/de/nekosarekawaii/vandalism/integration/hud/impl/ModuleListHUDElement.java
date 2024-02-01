/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.hud.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.internal.ModuleToggleListener;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleListHUDElement extends HUDElement implements ModuleToggleListener {

    private final List<String> activatedModules = new CopyOnWriteArrayList<>();
    private final List<String> externalModules = new CopyOnWriteArrayList<>();

    private boolean sort;

    private final BooleanValue showExternalClientName = new BooleanValue(
            this,
            "Show External Client Name",
            "Whether or not to show the name of an external client.",
            true
    ).onValueChange((oldValue, newValue) -> this.sort = true);

    private final BooleanValue shadow = new BooleanValue(
            this,
            "Shadow",
            "Whether or not the text should have a shadow.",
            true
    );

    private final IntegerValue heightOffset = new IntegerValue(
            this,
            "Height Offset",
            "The height offset of the text.",
            0,
            0,
            5
    );

    private final BooleanValue background = new BooleanValue(
            this,
            "Background",
            "Whether or not to draw a background.",
            false
    );

    private final IntegerValue widthOffset = new IntegerValue(
            this,
            "Width Offset",
            "The width offset of background.",
            0,
            0,
            5
    );

    private final ColorValue color = new ColorValue(
            this,
            "Color",
            "The color of the text.",
            Color.WHITE
    );

    public ModuleListHUDElement() {
        super("Module List", 2, 180);
        Vandalism.getInstance().getEventSystem().subscribe(ModuleToggleEvent.ID, this);
    }

    @Override
    public void onModuleToggle(final ModuleToggleEvent event) {
        this.sort = true;
    }

    @Override
    public void reset() {
        super.reset();
        this.sort = true;
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        this.sort();
        int yOffset = 0;
        for (final String activatedModule : this.activatedModules) {
            final int textWidth = this.mc.textRenderer.getWidth(activatedModule);
            switch (this.alignmentX) {
                case MIDDLE -> {
                    if (this.background.getValue()) {
                        context.fill(
                                (this.x + this.width / 2) - (textWidth / 2) - this.widthOffset.getValue(),
                                this.y + yOffset,
                                (this.x + this.width / 2) + (textWidth / 2) + this.widthOffset.getValue(),
                                this.y + yOffset + this.mc.textRenderer.fontHeight + this.heightOffset.getValue(),
                                Integer.MIN_VALUE
                        );
                    }
                    this.drawText(context, activatedModule, (this.x + this.width / 2) - (textWidth / 2), this.y + yOffset + this.heightOffset.getValue());
                }
                case RIGHT -> {
                    if (this.background.getValue()) {
                        context.fill(
                                (this.x + this.width) - textWidth - this.widthOffset.getValue(),
                                this.y + yOffset,
                                (this.x + this.width) + this.widthOffset.getValue(),
                                this.y + yOffset + this.mc.textRenderer.fontHeight + this.heightOffset.getValue(),
                                Integer.MIN_VALUE
                        );
                    }
                    this.drawText(context, activatedModule, (this.x + this.width) - textWidth, this.y + yOffset + this.heightOffset.getValue());
                }
                default -> {
                    if (this.background.getValue()) {
                        context.fill(
                                this.x - this.widthOffset.getValue(),
                                this.y + yOffset,
                                this.x + textWidth + this.widthOffset.getValue(),
                                this.y + yOffset + this.mc.textRenderer.fontHeight + this.heightOffset.getValue(),
                                Integer.MIN_VALUE
                        );
                    }
                    this.drawText(context, activatedModule, this.x, this.y + yOffset + this.heightOffset.getValue());
                }
            }
            this.width = Math.max(this.width, textWidth);
            yOffset += this.mc.textRenderer.fontHeight + this.heightOffset.getValue();
        }
        this.height = yOffset;
    }

    private void drawText(final DrawContext context, final String text, final int x, final int y) {
        context.drawText(
                this.mc.textRenderer,
                text, x, y,
                this.color.getColor(-y * 20).getRGB(),
                this.shadow.getValue()
        );
    }

    @Override
    public void calculateAlignment() {
        super.calculateAlignment();
        this.sort = true;
    }

    private void sort() {
        if (this.sort) {
            this.sort = false;
            this.activatedModules.clear();
            final List<AbstractModule> modules = Vandalism.getInstance().getModuleManager().getList();
            for (final AbstractModule module : modules) {
                if (module.isActive() && module.isShowInHUD()) {
                    this.activatedModules.add(module.getName());
                }
            }
            for (String activatedModule : this.externalModules) {
                if (!this.showExternalClientName.getValue()) {
                    activatedModule = activatedModule.split("\\s", 2)[1];
                }
                this.activatedModules.add(activatedModule);
            }
            this.activatedModules.sort((s1, s2) -> {
                final int compare;
                switch (this.alignmentY) {
                    case TOP, MIDDLE ->
                            compare = Integer.compare(this.mc.textRenderer.getWidth(s2), this.mc.textRenderer.getWidth(s1));
                    case BOTTOM ->
                            compare = Integer.compare(this.mc.textRenderer.getWidth(s1), this.mc.textRenderer.getWidth(s2));
                    default -> compare = 0;
                }
                return compare;
            });
        }
    }

    public void addExternalModule(final String source, final String name) {
        final String module = source + " " + name;
        if (this.externalModules.contains(module)) {
            return;
        }
        this.externalModules.add(module);
        this.sort = true;
    }

    public void removeExternalModule(final String source, final String name) {
        this.externalModules.remove(source + " " + name);
        this.sort = true;
    }

}
