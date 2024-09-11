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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.template.target.TargetGroup;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.raphimc.vialoader.util.VersionRange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoShieldModule extends Module implements PlayerUpdateListener {

    private final TargetGroup entityGroup = new TargetGroup(this, "Entities", "The entities to target.");

    private final FloatValue range = new FloatValue(
            this,
            "Range",
            "The range to target entities.",
            14.0f, 1.0f, 20.0f
    );

    private final IntegerValue rotateSpeed = new IntegerValue(
            this,
            "Rotate Speed",
            "The speed to rotate to the target.",
            180,
            1,
            180
    );

    private Entity target;

    public AutoShieldModule() {
        super(
                "Auto Shield",
                "Automatically blocks attacks from targets with a shield.",
                Category.COMBAT,
                VersionRange.andNewer(ProtocolVersion.v1_9)
        );
    }

    private void reset() {
        this.target = null;
        Vandalism.getInstance().getRotationManager().resetRotation();
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
        final float range = this.range.getValue();
        if (this.target == null || !this.entityGroup.isTarget(this.target) || this.target.distanceTo(this.mc.player) >= range || this.target instanceof final ProjectileEntity projectileEntity && projectileEntity.getVelocity().y <= 0) {
            this.reset();
            final List<Entity> entities = new ArrayList<>();
            for (final Entity entity : this.mc.world.getEntities()) {
                if (entity == this.mc.player) continue;
                if (entity == this.mc.player.getVehicle()) continue;
                if (!this.entityGroup.isTarget(entity)) continue;
                if (this.mc.player.getPos().distanceTo(entity.getPos()) > range) continue;
                entities.add(entity);
                break;
            }
            if (entities.isEmpty()) return;
            entities.sort(Comparator.comparingDouble(entity -> this.mc.player.distanceTo(entity)));
            this.target = entities.getFirst();
        } else {
            final PrioritizedRotation rotation = RotationUtil.rotationToVec(this.target.getPos(), RotationPriority.NORMAL);
            Vandalism.getInstance().getRotationManager().setRotation(rotation, this.rotateSpeed.getValue(),
                    0.0f, false);
            if (!this.mc.player.isBlocking()) {
                this.mc.options.useKey.setPressed(true);
            }
        }
    }

}
