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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureBlockBlockEntityRenderer.class)
public abstract class MixinStructureBlockBlockEntityRenderer {

    @Shadow protected abstract void renderInvisibleBlocks(StructureBlockBlockEntity entity, VertexConsumerProvider vertexConsumers, MatrixStack matrices);

    @Redirect(method = "render(Lnet/minecraft/block/entity/StructureBlockBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDDDDFFFFFFF)V"))
    private void hookExploitFixer_BoundingBoxes(final MatrixStack matrices, final VertexConsumer vertexConsumer, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final float red, final float green, final float blue, final float alpha, final float xAxisRed, final float yAxisGreen, final float zAxisBlue) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.renderSettings.blockStructureBlockBoundingBoxes.getValue()) {
            return;
        }
        WorldRenderer.drawBox(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, xAxisRed, yAxisGreen, zAxisBlue);
    }

    @Redirect(method = "render(Lnet/minecraft/block/entity/StructureBlockBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/StructureBlockBlockEntityRenderer;renderInvisibleBlocks(Lnet/minecraft/block/entity/StructureBlockBlockEntity;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void hookExploitFixer_AirBoxes(StructureBlockBlockEntityRenderer instance, StructureBlockBlockEntity entity, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.renderSettings.blockStructureBlockAirBoxes.getValue()) {
            return;
        }
        renderInvisibleBlocks(entity, vertexConsumers, matrices);
    }

}
