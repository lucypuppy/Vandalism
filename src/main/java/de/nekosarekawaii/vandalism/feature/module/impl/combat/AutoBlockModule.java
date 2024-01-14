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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.AttackListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.raphimc.vialoader.util.VersionEnum;

public class AutoBlockModule extends AbstractModule implements AttackListener, PlayerUpdateListener {

    private long lastAttack;

    public AutoBlockModule() {
        super(
                "Auto Block",
                "Automatically blocks attacks.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (event.target instanceof LivingEntity) {
            this.setBlocking(false);
            this.lastAttack = System.currentTimeMillis();
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (System.currentTimeMillis() - this.lastAttack > 500L) {
            this.setBlocking(false);
        }
    }

    public void setBlocking(final boolean blocking) {
        final ItemStack mainHandStack = this.mc.player.getMainHandStack();
        final ItemStack offHandStack = this.mc.player.getOffHandStack();

        if (mainHandStack.isEmpty() && offHandStack.isEmpty()) {
            return;
        }

        final Item mainHandItem = mainHandStack.getItem();
        final Item offHandItem = offHandStack.getItem();
        final boolean isOldVersion = ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8);

        if (isOldVersion) {
            if (!(mainHandItem instanceof SwordItem)) {
                return;
            }
        } else if (!mainHandItem.equals(Items.SHIELD) && !offHandItem.equals(Items.SHIELD)) {
            return;
        }

        this.mc.options.useKey.setPressed(blocking);
    }

}
