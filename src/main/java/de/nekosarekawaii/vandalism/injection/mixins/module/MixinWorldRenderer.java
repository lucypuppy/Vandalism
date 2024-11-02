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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.ESP2Module;
import de.nekosarekawaii.vandalism.util.render.gl2mc.PostProcessDoubleBufferSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private void onRenderEntity(WorldRenderer instance, Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumer) {
        final ESP2Module esp = Vandalism.getInstance().getModuleManager().getEsp2Module();
        if (esp.isActive() && esp.shouldOutlineEntity(entity)) {
            esp.prepareRenderingFor(entity);
            if (esp.isFillEnabled()) {
                this.renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, new PostProcessDoubleBufferSource.WithFill(esp.getOutlineBufferSource(), esp.getFillBufferSource(), vertexConsumer, esp.getColorForEntity(entity)));
            } else {
                this.renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, new PostProcessDoubleBufferSource(esp.getOutlineBufferSource(), vertexConsumer, esp.getColorForEntity(entity)));
            }
        } else {
            this.renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, vertexConsumer);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onPostRenderLevel(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (Vandalism.getInstance().getModuleManager().getEsp2Module().isActive()) {
            final Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.mul(matrix4f);
            RenderSystem.applyModelViewMatrix();
            Vandalism.getInstance().getModuleManager().getEsp2Module().renderFill();
            Vandalism.getInstance().getModuleManager().getEsp2Module().renderESP();
            modelViewStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
        }
    }
    
}
