/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.teleport;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.teleport.impl.VanillaModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TeleportModule extends Module implements Render3DListener {

    public final FloatValue maxDistance = new FloatValue(
            this,
            "Max Distance",
            "Max teleport distance.",
            10.0f,
            0.0f,
            100.0f
    );

    public final ModuleModeValue<TeleportModule> mode = new ModuleModeValue<>(this, "Mode", "The mode of the teleport.",
            new VanillaModuleMode(this)
            //new VulcanModuleMode(this) TODO: Fix vulcan teleport
    );

    public TeleportModule() {
        super(
                "Teleport",
                "Teleport to the block you click on.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, Render3DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, Render3DEvent.ID);
    }

    public boolean canTeleport() {
        return this.mc.player.isUsingItem() || this.mc.options.useKey.isPressed();
    }

    public Vec3d getBlockHitResult() {
        final HitResult result = this.mc.player.raycast(this.maxDistance.getValue(), this.mc.getRenderTickCounter().getTickDelta(false), false);
        if (result instanceof BlockHitResult) {
            return result.getPos();
        }
        return null;
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        final Vec3d target = this.getBlockHitResult();
        if (target == null) return;
        final BlockPos pos = new BlockPos((int) target.getX(), (int) target.getY(), (int) target.getZ());
        final VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        matrixStack.push();
        float[] color = new float[]{1f, 0f, 0f};
        DebugRenderer.drawBox(
                matrixStack,
                immediate,
                pos,
                pos,
                color[0], color[1], color[2], 0.5f
        );
        matrixStack.pop();
        immediate.draw();
    }

}
