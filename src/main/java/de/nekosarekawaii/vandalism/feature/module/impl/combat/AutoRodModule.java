/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.newrotation.Rotation;
import de.nekosarekawaii.vandalism.integration.newrotation.RotationBuilder;
import de.nekosarekawaii.vandalism.integration.newrotation.RotationManager;
import de.nekosarekawaii.vandalism.integration.newrotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

public class AutoRodModule extends AbstractModule implements PlayerUpdateListener, RotationListener {

    private boolean shouldRod;
    private boolean didRod;
    private int prevSlot;
    private HitResult hitResult;
    private Entity target;
    private final RotationManager rotationManager;

    public AutoRodModule() {
        super("Auto Rod", "Automatically rod targets.", Category.COMBAT);

        this.rotationManager = Vandalism.getInstance().getRotationManager();
        this.deactivateAfterSessionDefault();
    }

    private DoubleValue range = new DoubleValue(
            this,
            "Max Range",
            "The maximum range to rod targets.",
            6.0,
            1.0,
            20.0);

    private final FloatValue rotateSpeed = new FloatValue(
            this,
            "Rotate Speed",
            "The speed of the rotation.",
            60.0f,
            1.0f,
            180.0f
    );

    private final BooleanValue predict = new BooleanValue(
            this,
            "Predict",
            "Predict the target's position.",
            true
    );

    private final IntegerValue ticksToPredict = new IntegerValue(
            this,
            "Ticks to predict",
            "The amount of ticks which should get predicted",
            30,
            1,
            100
    ).visibleCondition(predict::getValue);

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        if (mc.player != null) {
            prevSlot = mc.player.getInventory().selectedSlot;
        }
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        if (mc.player != null) {
            prevSlot = mc.player.getInventory().selectedSlot;
        }
        didRod = false;
        hitResult = null;
        this.rotationManager.resetRotation();
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        doRod();
    }

    private void doRod() {
        boolean hasRod = mc.player.getMainHandStack().getItem() == Items.FISHING_ROD;
        if (mc.player.fishHook == null && this.target != null && mc.player.getEyePos().distanceTo(this.target.getPos()) > Vandalism.getInstance().getModuleManager().getKillAuraModule().getRange()) {
            Rotation rotation = this.rotationManager.getRotation() != null ? this.rotationManager.getRotation() : new Rotation(mc.player.getYaw(), mc.player.getPitch());
            hitResult = WorldUtil.raytrace(rotation, range.getValue());

            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && (!predict.getValue() || willRodHitTarget(this.target))) {
                shouldRod = true;
            }

            if (shouldRod && !didRod) {
                if (!hasRod) {
                    prevSlot = mc.player.getInventory().selectedSlot;
                    int rodSlot = getRodSlot();

                    if (rodSlot == -1) {
                        return;
                    }
                    mc.player.getInventory().selectedSlot = rodSlot;
                }
                if (hasRod) {
                    mc.doItemUse();
                    didRod = true;
                }
            }
        } else {
            double targetDistance = this.target != null ? mc.player.getEyePos().distanceTo(this.target.getPos()) : -1;
            double hookDistance = mc.player.fishHook != null ? mc.player.getEyePos().distanceTo(mc.player.fishHook.getPos()) : 9999;
            if (mc.player.fishHook != null && ProjectileUtil.getCollision(mc.player.fishHook,
                    (entity) -> entity instanceof LivingEntity && entity != mc.player).getType() == HitResult.Type.ENTITY || mc.player.fishHook != null && mc.player.fishHook.verticalCollision || target == null || hookDistance > targetDistance) {
                shouldRod = false;
            }
            if (!shouldRod) {
                if (didRod && hasRod) {
                    mc.doItemUse();
                    didRod = false;
                }
                if (prevSlot != mc.player.getInventory().selectedSlot && hasRod) {
                    mc.player.getInventory().selectedSlot = prevSlot;
                }
            }
        }
    }

    @Override
    public void onRotation(RotationEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null || mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }

        updateTarget();

        if (this.target != null && !didRod) {
            Rotation rotation = RotationBuilder.build(this.target, RotationPriority.NORMAL, true, this.range.getValue());

            if (rotation == null) { // Sanity check, crashes if you sneak and have your reach set to 3.0
                this.rotationManager.resetRotation();
                return;
            }

            float rotateSpeed = (float) (this.rotateSpeed.getValue() + Math.random() * 5.0f);
            this.rotationManager.setRotation(
                    rotation,
                    rotateSpeed,
                    0.2f,
                    true
            );
        } else {
            this.rotationManager.resetRotation();
        }

    }

    private int getRodSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != null && stack.getItem() == Items.FISHING_ROD) {
                return i;
            }
        }
        return -1;
    }

    private void updateTarget() {
        final List<Entity> entities = new ArrayList<>();

        for (final Entity entity : this.mc.world.getEntities()) {
            if (entity == this.mc.player || entity == this.mc.player.getVehicle()) {
                continue;
            }
            if (
                    Vandalism.getInstance().getTargetManager().isTarget(entity) &&
                            this.mc.player.distanceTo(entity) <= range.getValue() + 1.0 &&
                            entity.getWidth() > 0.0 && entity.getHeight() > 0.0
            ) {
                PlayerListEntry playerListEntry = null;

                if (entity instanceof PlayerEntity player) {
                    playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
                }

                if (playerListEntry == null || !playerListEntry.getGameMode().isCreative()) {
                    entities.add(entity);
                }
            }
        }

        if (entities.isEmpty()) {
            this.target = null;
            return;
        }

        final Vec3d eyePos = this.mc.player.getEyePos();
        entities.sort((entity1, entity2) -> {
            final double distance1 = eyePos.distanceTo(RotationBuilder.getNearestPoint(entity1));
            final double distance2 = eyePos.distanceTo(RotationBuilder.getNearestPoint(entity2));
            return Double.compare(distance1, distance2);
        });

        this.target = entities.getFirst();
    }

    public boolean willRodHitTarget(Entity target) {
        Vec3d initialPosition = mc.player.getPos();
        Rotation rotation = this.rotationManager.getRotation() != null ? this.rotationManager.getRotation() : new Rotation(mc.player.getYaw(), mc.player.getPitch());
        Vec3d initialVelocity = rotation.getVector().multiply(1.5D); // Assume initial speed of 1.5 blocks/tick

        for (int i = 0; i < ticksToPredict.getValue(); i++) { // Simulate for 100 ticks
            Vec3d futureTargetPos = target.getPos().add(target.getVelocity().multiply(i)); // Predict target's future position
            Vec3d futureHookPos = getFutureHookPos(initialPosition, initialVelocity, i); // Calculate hook's future position

            if (futureHookPos.distanceTo(futureTargetPos) < 1.0D) { // If hook is close to target
                return true;
            }
        }

        return false;
    }

    private Vec3d getFutureHookPos(Vec3d initialPos, Vec3d initialVelocity, int ticks) {
        double gravity = 0.03D; // Gravity value
        double futureX = initialPos.x + initialVelocity.x * ticks;
        double futureY = initialPos.y + initialVelocity.y * ticks - 0.5D * gravity * ticks * ticks;
        double futureZ = initialPos.z + initialVelocity.z * ticks;
        return new Vec3d(futureX, futureY, futureZ);
    }
}
