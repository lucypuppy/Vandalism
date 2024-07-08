/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.BlockBreakListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.inventory.InventoryUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;

public class AutoToolModule extends AbstractModule implements BlockBreakListener, PlayerUpdateListener {

    private final BooleanValue weaponsOnly = new BooleanValue(
            this,
            "Weapons Only",
            "Only switch to weapons for entities.",
            false
    );

    private int oldSlot = -1;
    private HitResult lastCrosshairTarget;

    public AutoToolModule() {
        super(
                "Auto Tool",
                "Automatically switches to the best tool for blocks and entities.",
                Category.MISC
        );
    }

    private void resetSlot() {
        if (this.oldSlot == -1) return;
        InventoryUtil.setSlot(this.oldSlot);
        this.oldSlot = -1;
    }

    @Override
    public void onActivate() {
        if (this.mc.player != null) {
            this.oldSlot = this.mc.player.getInventory().selectedSlot;
            this.lastCrosshairTarget = this.mc.crosshairTarget;
        }
        Vandalism.getInstance().getEventSystem().subscribe(this, BlockBreakEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, BlockBreakEvent.ID, PlayerUpdateEvent.ID);
        this.resetSlot();
        this.lastCrosshairTarget = null;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
            final Entity entity = entityHitResult.getEntity();
            if (entity == null) return;
            if (!Vandalism.getInstance().getTargetManager().isTarget(entity)) return;
            final List<Pair<Float, Integer>> toolList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (!PlayerInventory.isValidHotbarIndex(i)) continue;
                final ItemStack itemStack = this.mc.player.getInventory().getStack(i);
                if (itemStack == null || itemStack.isEmpty()) continue;
                final Item item = itemStack.getItem();
                if (item instanceof final ToolItem toolItem) {
                    if (!(item instanceof SwordItem) && !(item instanceof AxeItem)) {
                        continue;
                    }
                    toolList.add(new Pair<>(toolItem.getMaterial().getAttackDamage(), i));
                }
            }
            if (toolList.isEmpty()) return;
            toolList.sort((o1, o2) -> Float.compare(o1.getLeft(), o2.getLeft()));
            final Pair<Float, Integer> bestTool = toolList.getFirst();
            final ItemStack mainHandStack = this.mc.player.getInventory().getMainHandStack();
            if (mainHandStack.getItem() instanceof final ToolItem toolItem) {
                if (bestTool.getLeft() <= toolItem.getMaterial().getAttackDamage()) {
                    return;
                }
            }
            this.oldSlot = this.mc.player.getInventory().selectedSlot;
            InventoryUtil.setSlot(bestTool.getRight());
        } else if (!(this.lastCrosshairTarget instanceof BlockHitResult)) {
            this.resetSlot();
        }
        this.lastCrosshairTarget = this.mc.crosshairTarget;
    }

    @Override
    public void onBlockBreak(final BlockBreakEvent event) {
        if (this.weaponsOnly.getValue()) return;
        if (event.state.equals(BlockBreakState.START)) {
            final BlockState blockState = this.mc.world.getBlockState(event.pos);
            final Block block = blockState.getBlock();
            if (block instanceof AirBlock || block instanceof FluidBlock) return;
            final List<Pair<ItemStack, Integer>> toolList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (!PlayerInventory.isValidHotbarIndex(i)) continue;
                final ItemStack itemStack = this.mc.player.getInventory().getStack(i);
                if (itemStack == null || itemStack.isEmpty() || !(itemStack.getItem() instanceof MiningToolItem)) {
                    continue;
                }
                toolList.add(new Pair<>(itemStack, i));
            }
            if (toolList.isEmpty()) return;
            toolList.sort((o1, o2) -> {
                final float speed1 = o1.getLeft().getMiningSpeedMultiplier(blockState);
                final float speed2 = o2.getLeft().getMiningSpeedMultiplier(blockState);
                return Float.compare(speed2, speed1);
            });
            final Pair<ItemStack, Integer> bestTool = toolList.getFirst();
            final ItemStack mainHandStack = this.mc.player.getInventory().getMainHandStack();
            if (bestTool.getLeft().getMiningSpeedMultiplier(blockState) <= mainHandStack.getMiningSpeedMultiplier(blockState)) {
                return;
            }
            this.oldSlot = this.mc.player.getInventory().selectedSlot;
            InventoryUtil.setSlot(bestTool.getRight());
        } else {
            this.resetSlot();
        }
    }

}
