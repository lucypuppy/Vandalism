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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class IllegalInteractionModule extends Module implements PlayerUpdateListener, MouseInputListener, Render3DListener {

    private final DoubleValue reach = new DoubleValue(
            this,
            "Reach",
            "The reach of illegal interactions.",
            3.0,
            0.0,
            5.0
    );

    private final KeyBindValue reachChangeKeyBind = new KeyBindValue(
            this,
            "Reach Change KeyBind",
            "If hold down this key you can increase or decrease the reach when using the mouse wheel.",
            GLFW.GLFW_KEY_Z
    );

    public BooleanValue viaVersionBug = new BooleanValue(
            this,
            "ViaVersion Bug",
            "Allows you to place blocks inside yourself when playing on 1.8 servers with the ViaVersion plugin installed.\n" +
                    "Requires you to select 1.8.x in ViaFabricPlus.",
            true
    );

    private double scrollAmount;

    public IllegalInteractionModule() {
        super(
                "Illegal Interaction",
                "Lets you interact with illegal block hit results.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        this.scrollAmount = 0.0;
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, MouseEvent.ID, Render3DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, MouseEvent.ID, Render3DEvent.ID);
        this.scrollAmount = 0.0;
    }

    private BlockHitResult getBlockHitResult() {
        final Entity cameraEntity = mc.getCameraEntity();
        final HitResult hitResult = cameraEntity.raycast(this.reach.getValue(), 0, false);
        final ItemStack mainHandStack = mc.player.getMainHandStack();
        final Item item = mainHandStack.getItem();
        final boolean invalidItem = mainHandStack.isEmpty() || !(item instanceof BlockItem || item instanceof SpawnEggItem);
        if (!(hitResult instanceof final BlockHitResult blockHitResult) || invalidItem) {
            return null;
        }
        final Block block = mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
        if ((block instanceof AirBlock || block instanceof FluidBlock)) {
            return blockHitResult;
        }
        return null;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!mc.options.useKey.isPressed()) return;
        final BlockHitResult blockHitResult = this.getBlockHitResult();
        if (blockHitResult == null) return;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (this.reachChangeKeyBind.isPressed() && event.type == Type.SCROLL) {
            event.setCancelled(true);
            this.scrollAmount += event.vertical;
            this.scrollAmount = Math.max(this.reach.getMinValue(), Math.min(this.reach.getMaxValue(), this.scrollAmount));
            if (this.reach.getValue() == this.scrollAmount) return;
            this.reach.setValue(this.scrollAmount);
            ChatUtil.chatMessage(Text.literal(
                    Formatting.GREEN + "Changed reach of " + this.getName() + " Module to" +
                            Formatting.GRAY + ": " + Formatting.DARK_AQUA + this.reach.getValue()
            ), true);
        }
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        final BlockHitResult blockHitResult = this.getBlockHitResult();
        if (blockHitResult == null) return;
        final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        matrixStack.push();
        final BlockPos pos = blockHitResult.getBlockPos();
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
