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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.EntityPushListener;
import de.nekosarekawaii.vandalism.event.player.FluidPushListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.ItemStackUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PushVelocityModule extends AbstractModule implements EntityPushListener, FluidPushListener {

    private final BooleanValue modifyEntityPush = new BooleanValue(
            this,
            "Modify Entity Push",
            "If activated you can modify the entity push velocity.",
            true
    );

    private final DoubleValue entityPushMultiplier = new DoubleValue(
            this,
            "Entity Push Multiplier",
            "Which multiplier of velocity should a entity push apply to you.",
            0.0d,
            -2.0d,
            2.0d
    ).visibleCondition(this.modifyEntityPush::getValue);

    private final BooleanValue modifyFluidPush = new BooleanValue(
            this,
            "Modify Fluid Push",
            "If activated you can modify the fluid push velocity.",
            true
    );

    private final DoubleValue fluidPushSpeed = new DoubleValue(
            this,
            "Fluid Push Value",
            "Which value of speed should a fluid push apply to you.",
            0.0d,
            -2.0d,
            2.0d
    ).visibleCondition(this.modifyFluidPush::getValue);

    public PushVelocityModule() {
        super("Push Velocity", "Modifies the entity and the fluid push velocity you take.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(EntityPushEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(FluidPushEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(EntityPushEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(FluidPushEvent.ID, this);
    }

    @Override
    public void onEntityPush(final EntityPushEvent entityPushEvent) {
        if (!this.modifyEntityPush.getValue()) return;
        final double value = this.entityPushMultiplier.getValue();
        if (value == 0.0d) entityPushEvent.cancel();
        else entityPushEvent.value = value;
    }

    @Override
    public void onFluidPush(final FluidPushEvent fluidPushEvent) {
        if (!this.modifyFluidPush.getValue()) return;

        if (this.mc.options.useKey.isPressed()) {
            final ItemStack mainHandStack = this.mc.player.getMainHandStack();
            if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getLevel(ItemStackUtil.getEnchantment(Enchantments.RIPTIDE), mainHandStack) > 0)
                return;
        }

        if (this.mc.player.isUsingRiptide()) return;

        final double speed = this.fluidPushSpeed.getValue();
        if (speed == 0.0d) fluidPushEvent.cancel();
        else fluidPushEvent.speed = speed;
    }

}
