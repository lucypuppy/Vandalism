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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoShieldModule extends AbstractModule implements PlayerUpdateListener {

    private Entity target;

    public AutoShieldModule() {
        super(
                "Auto Shield",
                "Automatically blocks attacks from targets with a shield.",
                Category.COMBAT
        );
    }

    private void reset() {
        this.target = null;
        Vandalism.getInstance().getRotationListener().resetRotation();
        this.mc.options.useKey.setPressed(false);
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final ItemStack mainHand = this.mc.player.getMainHandStack();
        final ItemStack offHand = this.mc.player.getOffHandStack();
        if (!(mainHand.getItem() instanceof ShieldItem) && !(offHand.getItem() instanceof ShieldItem)) {
            this.reset();
            return;
        }
        final float range = 14.0f;
        if (this.target == null || this.target.distanceTo(this.mc.player) >= range || this.target instanceof final ProjectileEntity projectileEntity && projectileEntity.getVelocity().y <= 0) {
            this.reset();
            final List<Entity> entities = new ArrayList<>();
            for (final Entity entity : this.mc.world.getEntities()) {
                if (entity == this.mc.player) continue;
                if (entity == this.mc.player.getVehicle()) continue;
                if (this.mc.player.getPos().distanceTo(entity.getPos()) > range) continue;
                entities.add(entity);
                break;
            }
            if (entities.isEmpty()) return;
            entities.sort(Comparator.comparingDouble(entity -> this.mc.player.distanceTo(entity)));
            this.target = entities.get(0);
        }
        else {
            final Rotation rotation = Rotation.Builder.build(this.target.getPos(), this.mc.player.getEyePos());
            this.mc.player.setYaw(rotation.getYaw());
            this.mc.player.setPitch(rotation.getPitch());
            if (!this.mc.player.isBlocking()) {
                this.mc.options.useKey.setPressed(true);
            }
        }
    }

}
