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

package de.nekosarekawaii.vandalism.base.event.normal.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityRenderBottomLayerListener {

    void onLivingEntityRenderBottomLayer(final LivingEntityRenderBottomLayerEvent event);

    class LivingEntityRenderBottomLayerEvent extends AbstractEvent<LivingEntityRenderBottomLayerListener> {

        public static final int ID = 28;

        public final LivingEntity livingEntity;

        public final MatrixStack matrixStack;

        public final VertexConsumer vertexConsumer;

        public int light, overlay;

        public float red, green, blue, alpha;

        public LivingEntityRenderBottomLayerEvent(final LivingEntity livingEntity, final MatrixStack matrixStack, final VertexConsumer vertexConsumer, final int light, final int overlay, final float red, final float green, final float blue, final float alpha) {
            this.livingEntity = livingEntity;
            this.matrixStack = matrixStack;
            this.vertexConsumer = vertexConsumer;
            this.light = light;
            this.overlay = overlay;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public void call(final LivingEntityRenderBottomLayerListener listener) {
            listener.onLivingEntityRenderBottomLayer(this);
        }

    }


}
