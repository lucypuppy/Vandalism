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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.render.EntityRenderBottomLayerListener;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderer.class)
public class MixinFeatureRenderer {

    @Unique
    private static Entity vandalism$entity;

    @Inject(method = "renderModel", at = @At("HEAD"))
    private static void captureEntity(EntityModel model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float red, float green, float blue, CallbackInfo ci) {
        vandalism$entity = entity;
    }

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private static void callLivingEntityRenderBottomLayerListener(final EntityModel instance, final MatrixStack matrices, final VertexConsumer vertices, final int light, final int overlay, final float red, final float green, final float blue, final float alpha) {
        final var event = new EntityRenderBottomLayerListener.EntityRenderBottomLayerEvent(vandalism$entity, matrices, vertices, light, overlay, red, green, blue, alpha);
        Vandalism.getInstance().getEventSystem().postInternal(EntityRenderBottomLayerListener.EntityRenderBottomLayerEvent.ID, event);
        instance.render(matrices, vertices, event.light, event.overlay, event.red, event.green, event.blue, event.alpha);
    }


}
