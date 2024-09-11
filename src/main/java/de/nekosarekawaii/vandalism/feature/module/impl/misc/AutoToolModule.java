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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.player.PreBlockBreakListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.template.target.TargetGroup;
import de.nekosarekawaii.vandalism.util.InventoryUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoToolModule extends Module implements PlayerUpdateListener, PreBlockBreakListener {

    private final ModeValue mode = new ModeValue(
            this,
            "Mode",
            "The mode of the module.",
            "Both",
            "Tools",
            "Weapons"
    );

    private final TargetGroup entityGroup = new TargetGroup(this, "Entities", "The entities to target.").visibleCondition(() -> !this.mode.getValue().equals("Tools"));

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
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, PreBlockBreakEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, PreBlockBreakEvent.ID);
        this.resetSlot();
        this.lastCrosshairTarget = null;
    }

    private void handleBlockInteraction(final BlockPos blockPos) {
        final BlockState blockState = this.mc.world.getBlockState(blockPos);
        final Block block = blockState.getBlock();
        if (block instanceof AirBlock || block instanceof FluidBlock) return;
        final List<Pair<ItemStack, Integer>> toolList = new ArrayList<>();
        final AtomicBoolean foundSwordForBamboo = new AtomicBoolean(false);
        for (int i = 0; i < 9; i++) {
            if (!PlayerInventory.isValidHotbarIndex(i)) continue;
            final ItemStack itemStack = this.mc.player.getInventory().getStack(i);
            if (itemStack == null) {
                continue;
            }
            if (!(block instanceof PlantBlock)) {
                final Item item = itemStack.getItem();
                if (!(item instanceof ToolItem) && !(item instanceof ShearsItem)) {
                    continue;
                }
                // Mojang is so smart...
                if (block instanceof BambooBlock) {
                    if (item instanceof SwordItem) {
                        foundSwordForBamboo.set(true);
                    } else if (!(item instanceof AxeItem)) {
                        continue;
                    }
                }
                toolList.add(new Pair<>(itemStack, i));
            }
        }
        if (toolList.isEmpty()) return;
        toolList.sort((i1, i2) -> {
            final ItemStack first = i1.getLeft();
            final ItemStack second = i2.getLeft();
            if (foundSwordForBamboo.get()) {
                final Item firstItem = first.getItem();
                if (firstItem instanceof SwordItem) {
                    return -1;
                }
                final Item secondItem = second.getItem();
                if (secondItem instanceof SwordItem) {
                    return 1;
                }
            }
            final float speed1 = first.getMiningSpeedMultiplier(blockState);
            final float speed2 = second.getMiningSpeedMultiplier(blockState);
            return Float.compare(speed2, speed1);
        });
        final Pair<ItemStack, Integer> bestTool = toolList.getFirst();
        final ItemStack mainHandStack = this.mc.player.getInventory().getMainHandStack();
        final float bestToolSpeed = bestTool.getLeft().getMiningSpeedMultiplier(blockState);
        if (!foundSwordForBamboo.get()) {
            if (bestToolSpeed <= mainHandStack.getMiningSpeedMultiplier(blockState)) {
                for (int i = 0; i < this.mc.player.getInventory().main.size(); i++) {
                    if (!PlayerInventory.isValidHotbarIndex(i)) {
                        continue;
                    }
                    final ItemStack itemStack = this.mc.player.getInventory().getStack(i);
                    if (itemStack == null) {
                        continue;
                    }
                    final Item item = itemStack.getItem();
                    if (!(item instanceof ToolItem) && !(item instanceof ShearsItem) && itemStack.getMiningSpeedMultiplier(blockState) == bestToolSpeed) {
                        this.oldSlot = this.mc.player.getInventory().selectedSlot;
                        this.mc.player.getInventory().selectedSlot = i;
                        break;
                    }
                }
                return;
            }
        }
        this.oldSlot = this.mc.player.getInventory().selectedSlot;
        this.mc.player.getInventory().selectedSlot = bestTool.getRight();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.mc.options.attackKey.isPressed()) {
            this.resetSlot();
        }
        if (!this.mode.getValue().equals("Tools")) {
            if (this.mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
                final Entity entity = entityHitResult.getEntity();
                if (entity == null) return;
                if (!this.entityGroup.isTarget(entity)) return;
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
                if (this.mc.player.getInventory().getMainHandStack().getItem() instanceof final ToolItem toolItem) {
                    if ((toolItem instanceof SwordItem) || (toolItem instanceof AxeItem)) {
                        if (bestTool.getLeft() <= toolItem.getMaterial().getAttackDamage()) {
                            return;
                        }
                    }
                }
                this.oldSlot = this.mc.player.getInventory().selectedSlot;
                InventoryUtil.setSlot(bestTool.getRight());
            }
        }
        if (!(this.lastCrosshairTarget instanceof BlockHitResult)) {
            this.resetSlot();
        }
        this.lastCrosshairTarget = this.mc.crosshairTarget;
    }

    @Override
    public void onPreBlockBreak(final PreBlockBreakEvent event) {
        this.handleBlockInteraction(event.pos);
    }

}
