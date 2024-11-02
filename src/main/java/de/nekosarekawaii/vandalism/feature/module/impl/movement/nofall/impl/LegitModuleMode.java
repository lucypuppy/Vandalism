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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class LegitModuleMode extends ModuleMulti<NoFallModule> implements PlayerUpdateListener, RotationListener {

    private PrioritizedRotation rotation = null;
    private int prevSlot;

    public LegitModuleMode() {
        super("Legit");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        if(mc.player != null) {
            prevSlot = mc.player.getInventory().selectedSlot;
        }
        ChatUtil.chatMessage("You need to have a water bucket in your hotbar to use this mode.");
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        Vandalism.getInstance().getRotationManager().resetRotation();
        if(mc.player != null) {
            prevSlot = mc.player.getInventory().selectedSlot;
        }
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if(mc.player.fallDistance >= 3 && mc.player.getInventory().contains(new ItemStack(Items.WATER_BUCKET))) {
            for (int i = 0; i <= 8; i++) {
                if (mc.player.getInventory().getStack(i).getItem() == Items.WATER_BUCKET) {
                    prevSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = i;
                    break;
                }
            }

            if (mc.player.getMainHandStack().getItem() == Items.WATER_BUCKET && !mc.world.getBlockState(mc.player.getBlockPos().down()).isAir()) {
                mc.doItemUse();
            }
        }

        if(mc.player.isTouchingWater() && mc.player.getMainHandStack().getItem() == Items.BUCKET) {
            mc.doItemUse();
            mc.player.getInventory().selectedSlot = prevSlot;
        }
    }

    @Override
    public void onRotation(RotationEvent event) {
        if((mc.player.fallDistance < 3 || mc.player.getMainHandStack().getItem() != Items.WATER_BUCKET) && (mc.player.getMainHandStack().getItem() != Items.BUCKET || !mc.player.isTouchingWater())) {
            Vandalism.getInstance().getRotationManager().resetRotation();
            return;
        }
        this.rotation = new PrioritizedRotation(mc.player.getYaw(), 90, RotationPriority.NORMAL);
        Vandalism.getInstance().getRotationManager().setRotation(this.rotation, true, (targetRotation, serverRotation, deltaTime, hasClientRotation) ->
                RotationUtil.rotateMouse(targetRotation, serverRotation, 60.0f, deltaTime, hasClientRotation));
    }
}
