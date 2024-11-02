/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.target.TargetGroup;
import de.nekosarekawaii.vandalism.event.render.EntityRenderBottomLayerListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.awt.*;
import java.util.List;

public class TrueSightModule extends Module implements EntityRenderBottomLayerListener {

    private static final List<Item> MARKER_BLOCK_ITEMS = ClientWorld.BLOCK_MARKER_ITEMS.stream().toList();

    public final MultiRegistryValue<Item> markerBlocks = new MultiRegistryValue<>(
            this,
            "Marker Blocks",
            "Makes invisible marker blocks visible.",
            Registries.ITEM,
            MARKER_BLOCK_ITEMS,
            MARKER_BLOCK_ITEMS.toArray(Item[]::new)
    );

    private final TargetGroup entityGroup = new TargetGroup(this, "Entities", "The entities to target.");

    private final ColorValue entityColor = new ColorValue(
            this.entityGroup,
            "Entity Color",
            "The color of invisible entities.",
            ColorUtils.withAlpha(Color.WHITE, 100)
    );

    public TrueSightModule() {
        super("True Sight", "Makes invisible blocks or entities visible.", Category.RENDER);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(EntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(EntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onLivingEntityRenderBottomLayer(final EntityRenderBottomLayerEvent event) {
        if (this.isValid(event.entity)) {
            event.color = this.entityColor.getValue().getColor().getRGB();
        }
    }

    public boolean isValid(final Entity entity) {
        return this.isActive() && entity.isInvisible() && this.entityGroup.isTarget(entity);
    }

}
