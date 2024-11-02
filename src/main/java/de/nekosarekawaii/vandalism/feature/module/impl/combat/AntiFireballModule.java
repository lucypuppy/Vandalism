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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AntiFireballModule extends Module implements PlayerUpdateListener {

    private final FloatValue range = new FloatValue(
            this,
            "Range",
            "The range to target fire balls.",
            14.0f, 1.0f, 20.0f
    );

    private final IntegerValue rotateSpeed = new IntegerValue(
            this,
            "Rotate Speed",
            "The speed to rotate to a fire ball.",
            180,
            1,
            180
    );

    private FireballEntity target;

    public AntiFireballModule() {
        super("Anti Fireball", "Prevents taking damage from fire balls and throws them back to the enemy.", Category.COMBAT);
    }

    private void reset() {
        this.target = null;
        Vandalism.getInstance().getRotationManager().resetRotation(RotationPriority.HIGHEST);
    }

    @Override
    protected void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final float range = this.range.getValue();
        if (this.target == null || this.target.isRemoved() || this.target.distanceTo(mc.player) >= range) {
            this.reset();
            final List<FireballEntity> fireballEntities = new ArrayList<>();
            for (final Entity entity : mc.world.getEntities()) {
                if (entity == mc.player) continue;
                if (mc.player.getPos().distanceTo(entity.getPos()) > range) continue;
                if (entity instanceof final FireballEntity fireballEntity) {
                    fireballEntities.add(fireballEntity);
                    break;
                }
            }
            if (fireballEntities.isEmpty()) return;
            fireballEntities.sort(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity)));
            this.target = fireballEntities.getFirst();
        } else {
            Vandalism.getInstance().getRotationManager().setRotation(
                    RotationUtil.rotationToVec(this.target.getPos(), RotationPriority.HIGHEST),
                    false, (targetRotation, serverRotation, deltaTime, hasClientRotation) ->
                            RotationUtil.rotateMouse(targetRotation, serverRotation,
                                    this.rotateSpeed.getValue(), deltaTime, hasClientRotation)
            );
        }
        if (mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof FireballEntity) {
                mc.doAttack();
            }
        }
    }

}
