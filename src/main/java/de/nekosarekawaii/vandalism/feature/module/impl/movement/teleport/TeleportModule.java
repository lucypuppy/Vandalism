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
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.teleport.impl.VanillaModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class TeleportModule extends Module implements Render3DListener, PlayerUpdateListener {

    public final IntegerValue maxDistance = new IntegerValue(
            this,
            "Max Distance",
            "Max teleport distance.",
            100,
            10,
            250
    );

    private final KeyBindValue teleportSelectKey = new KeyBindValue(
            this,
            "Teleport Select Key",
            "The key to select the block you want to teleport to.",
            GLFW.GLFW_MOUSE_BUTTON_RIGHT
    );

    private final KeyBindValue teleportKey = new KeyBindValue(
            this,
            "Teleport Key",
            "The key to teleport to the selected block.",
            GLFW.GLFW_KEY_LEFT_SHIFT
    );

    public final ModuleModeValue<TeleportModule> mode = new ModuleModeValue<>(this, "Mode", "The mode of the teleport.",
            new VanillaModuleMode(this)
            // new VulcanModuleMode(this) TODO: Fix vulcan teleport
    );

    public boolean teleport;
    private BlockPos hoverPos, selectedPos;
    private final List<Block> blackList = Arrays.asList(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR);

    public TeleportModule() {
        super(
                "Teleport",
                "Teleport to the block you click on.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, Render3DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, Render3DEvent.ID);
    }

    private BlockPos getBlockHitResult() {
        final HitResult result = this.mc.player.raycast(this.maxDistance.getValue(), this.mc.getRenderTickCounter().getTickDelta(false), false);
        if (result instanceof BlockHitResult blockHitResult) {
            final Block block = this.mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            if (!this.blackList.contains(block)) {
                return blockHitResult.getBlockPos();
            }
        }
        return null;
    }

    public boolean isTeleportPositionValid() {
        if (this.selectedPos == null) return false;
        final Block block = this.mc.world.getBlockState(new BlockPos(this.selectedPos.up())).getBlock();
        final Block block2 = this.mc.world.getBlockState(new BlockPos(this.selectedPos.up().up())).getBlock();
        return this.blackList.contains(block) && this.blackList.contains(block2);
    }

    public boolean isPositionValid(BlockPos blockPos) {
        final Block block = this.mc.world.getBlockState(new BlockPos(blockPos.up())).getBlock();
        final Block block2 = this.mc.world.getBlockState(new BlockPos(blockPos.up().up())).getBlock();
        return this.blackList.contains(block) && this.blackList.contains(block2);
    }

    public Vec3d getSelectedPos() {
        return this.selectedPos.toCenterPos();
    }

    public void reset() {
        this.selectedPos = null;
        this.teleport = false;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.teleportSelectKey.isPressed() && this.hoverPos != null && this.mc.currentScreen == null) {
            if (this.isPositionValid(this.hoverPos)) {
                this.selectedPos = this.hoverPos;
            }
        }
        this.hoverPos = this.getBlockHitResult();
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (this.selectedPos != null && this.teleportKey.isPressed() && this.mc.currentScreen == null) {
            this.teleport = true;
        }
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        final VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        final Vec3d vec = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().negate();
        matrixStack.push();
        matrixStack.translate(vec.x, vec.y, vec.z);
        if (this.hoverPos != null && !this.hoverPos.equals(this.selectedPos)) {
            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    this.hoverPos.getX(),
                    this.hoverPos.getY(),
                    this.hoverPos.getZ(),
                    this.hoverPos.getX() + 1,
                    this.hoverPos.getY() + 1,
                    this.hoverPos.getZ() + 1,
                    1F,
                    0F,
                    0F,
                    0.5F
            );
        }
        if (this.selectedPos != null) {
            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    this.selectedPos.getX(),
                    this.selectedPos.getY(),
                    this.selectedPos.getZ(),
                    this.selectedPos.getX() + 1,
                    this.selectedPos.getY() + 1,
                    this.selectedPos.getZ() + 1,
                    0F,
                    1F,
                    0F,
                    0.5F
            );
        }
        immediate.draw();
        matrixStack.pop();
    }

}
