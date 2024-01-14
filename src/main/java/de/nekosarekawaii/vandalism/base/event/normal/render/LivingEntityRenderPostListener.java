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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityRenderPostListener {

    void onLivingEntityRenderPost(final LivingEntityRenderPostEvent event);

    class LivingEntityRenderPostEvent extends AbstractEvent<LivingEntityRenderPostListener> {

        public static final int ID = 29;

        public final LivingEntity livingEntity;

        public float yaw, tickDelta;

        public final MatrixStack matrixStack;

        public int light;

        public LivingEntityRenderPostEvent(final LivingEntity livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final int light) {
            this.livingEntity = livingEntity;
            this.yaw = yaw;
            this.tickDelta = tickDelta;
            this.matrixStack = matrixStack;
            this.light = light;
        }

        @Override
        public void call(final LivingEntityRenderPostListener listener) {
            listener.onLivingEntityRenderPost(this);
        }

    }

}
