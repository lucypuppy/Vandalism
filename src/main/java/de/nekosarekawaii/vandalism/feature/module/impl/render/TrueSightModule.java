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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.rclasses.common.ColorUtils;
import de.florianmichael.rclasses.common.model.HSBColor;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.render.LivingEntityRenderBottomLayerListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiItemValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;

import java.awt.*;
import java.util.List;


public class TrueSightModule extends AbstractModule implements LivingEntityRenderBottomLayerListener {

    private static final List<Item> MARKER_BLOCK_ITEMS = ClientWorld.BLOCK_MARKER_ITEMS.stream().toList();

    public final MultiItemValue markerBlocks = new MultiItemValue(
            this,
            "Marker Blocks",
            "Makes invisible marker blocks visible.",
            MARKER_BLOCK_ITEMS,
            MARKER_BLOCK_ITEMS.toArray(Item[]::new)
    );

    private final BooleanValue entities = new BooleanValue(
            this,
            "Entities",
            "Makes invisible entities visible.",
            true
    );

    public final BooleanValue showIllusionerEntity = new BooleanValue(
            this,
            "Show Illusioner Entity",
            "Makes the illusioner entity visible.",
            true
    ).visibleCondition(this.entities::getValue);

    private final Value<HSBColor> entityColor = new ColorValue(
            this,
            "Entity Color",
            "The color of invisible entities.",
            ColorUtils.withAlpha(Color.WHITE, 100)
    ).visibleCondition(this.entities::getValue);

    public TrueSightModule() {
        super("True Sight", "Makes invisible blocks or entities visible.", Category.RENDER);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(LivingEntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(LivingEntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onLivingEntityRenderBottomLayer(final LivingEntityRenderBottomLayerEvent event) {
        if (this.entities.getValue() && event.livingEntity.isInvisible()) {
            final Color color = this.entityColor.getValue().getColor();
            event.red = color.getRed() / 255F;
            event.green = color.getGreen() / 255F;
            event.blue = color.getBlue() / 255F;
            event.alpha = color.getAlpha() / 255F;
        }
    }

}
