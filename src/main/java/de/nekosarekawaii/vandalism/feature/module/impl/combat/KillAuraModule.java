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
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.RaytraceListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.base.event.normal.render.Render2DListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.HitboxSelectMode;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationPriority;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.util.click.ClickType;
import de.nekosarekawaii.vandalism.util.click.Clicker;
import de.nekosarekawaii.vandalism.util.click.impl.BoxMuellerClicker;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAuraModule extends AbstractModule implements PlayerUpdateListener, Render2DListener, RotationListener, RaytraceListener {

    private final ValueGroup targetSelectionGroup = new ValueGroup(
            this,
            "Target Selection",
            "Settings for the target selection."
    );

    private final DoubleValue range = new DoubleValue(
            this.targetSelectionGroup,
            "Range",
            "The range extension for the aim.",
            3.0,
            2.0,
            6.0
    );

    private final DoubleValue preAimRangeExtension = new DoubleValue(
            this.targetSelectionGroup,
            "Pre Aim Range Extension",
            "The range extension for the aim.",
            3.0,
            0.0,
            3.0
    );

    private final ValueGroup reachExploitGroup = new ValueGroup(
            this.targetSelectionGroup,
            "Reach Exploit",
            "Settings for the reach exploit."
    );

    private final BooleanValue firstHitExtender = new BooleanValue(
            this.reachExploitGroup,
            "First Hit Extender",
            "Whether the first hit extender should be used.",
            false
    );

    private final IntegerValue firstHitExtenderOffTime = new IntegerValue(
            this.reachExploitGroup,
            "First Hit Extender Off Time",
            "The time in milliseconds after the first hit extender should be disabled.",
            1000,
            0,
            5000
    ).visibleCondition(this.firstHitExtender::getValue);

    private final DoubleValue firstHitRangeExtender = new DoubleValue(
            this.reachExploitGroup,
            "First Hit Range Extender",
            "The range extender for the first hit extender.",
            1.0,
            0.0,
            2.0
    ).visibleCondition(this.firstHitExtender::getValue);

    private final BooleanValue switchTarget = new BooleanValue(
            this.targetSelectionGroup,
            "Switch Target",
            "Whether the target should be switched.",
            true
    );

    private final ValueGroup clicking = new ValueGroup(
            this,
            "Clicking",
            "Settings for the clicking."
    );

    private final EnumModeValue<ClickType> clickType = new EnumModeValue<>(
            this.clicking,
            "Click Type",
            "The type of clicking.",
            ClickType.BoxMueller,
            ClickType.values()
    ).onValueChange((oldValue, newValue) -> {
        oldValue.getClicker().setClickAction(aBoolean -> {});
        this.updateClicker(newValue.getClicker());
    });

    private final ValueGroup rotationGroup = new ValueGroup(
            this,
            "Rotation",
            "Settings for the rotations."
    );

    private final FloatValue rotateSpeed = new FloatValue(
            this.rotationGroup,
            "Rotate Speed",
            "The speed of the rotation.",
            60.0f,
            0.0f,
            180.0f
    );

    private final FloatValue correlationStrength = new FloatValue(
            this.rotationGroup,
            "Correlation Strength",
            "The strength of the correlation.",
            0.2f,
            0.0f,
            1.0f
    );

    private final IntegerValue aimPoints = new IntegerValue(
            this.rotationGroup,
            "Aim Points",
            "The amount of aim points. (Higher values can cause lag)",
            32,
            1,
            100
    );

    private final BooleanValue movementFix = new BooleanValue(
            this.rotationGroup,
            "Movement Fix",
            "Whether the movement fix should be used.",
            true
    );

    private LivingEntity target;
    private int targetIndex = 0;

    private double raytraceDistance = -1.0;
    private boolean isLooking = false;

    private long lastPossibleHit = -1;

    private final AutoBlockModule autoBlock;

    private final de.nekosarekawaii.vandalism.integration.rotation.RotationListener rotationListener;

    public KillAuraModule(final AutoBlockModule autoBlock) {
        super(
                "Kill Aura",
                "Automatically attacks nearby enemies.",
                Category.COMBAT
        );
        this.autoBlock = autoBlock;
        this.rotationListener = Vandalism.getInstance().getRotationListener();
        this.updateClicker(this.clickType.getValue().getClicker());
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID, Render2DEvent.ID,
                RotationEvent.ID, RaytraceEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID, Render2DEvent.ID,
                RotationEvent.ID, RaytraceEvent.ID
        );

        this.rotationListener.resetRotation();
        this.targetIndex = 0;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        this.getTarget();
        if (this.target == null ||
                this.rotationListener.getRotation() == null ||
                Float.isNaN(this.rotationListener.getRotation().getYaw()) ||
                Float.isNaN(this.rotationListener.getRotation().getPitch())) {
            return;
        }

        // Check if the target is looking at us
        final Rotation pseudoRotation = Rotation.Builder.build(mc.player.getPos(), this.target.getEyePos());
        this.isLooking = Math.abs(MathHelper.wrapDegrees(pseudoRotation.getYaw()) - MathHelper.wrapDegrees(this.target.getYaw())) <= 80.0 &&
                mc.player.getPos().distanceTo(this.target.getPos()) <= 6.0;

        final Vec3d eyePos = mc.player.getEyePos();
        final HitResult raytrace = WorldUtil.raytrace(this.rotationListener.getRotation(), Math.pow(getAimRange(), 2));
        this.raytraceDistance = raytrace != null ? eyePos.distanceTo(raytrace.getPos()) : -1.0;

        if (this.raytraceDistance > getAimRange() || this.raytraceDistance <= 0) {
            return;
        }

        this.clickType.getValue().getClicker().onUpdate();
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (this.target != null) {
            final Rotation rotation = Rotation.Builder.build(this.target, getAimRange(), this.aimPoints.getValue(), HitboxSelectMode.Circular);

            if (rotation == null) { //Sanity check, crashes if you sneak and have your reach set to 3.0
                this.rotationListener.resetRotation();
                return;
            }

            final float rotationPercentage;

            if (rotationListener.getRotation() == null) {
                final Rotation playerRotation = new Rotation(this.mc.player.getYaw(), this.mc.player.getPitch());
                rotationPercentage = RotationUtil.calculateRotationPercentage(playerRotation, rotation, true);
            } else {
                rotationPercentage = RotationUtil.calculateRotationPercentage(rotationListener.getRotation(), rotation, true);
            }

            float rotateSpeed = 0.0f;
            if (this.rotateSpeed.getValue() > 0.0f) {
                rotateSpeed = (float) (this.rotateSpeed.getValue() + Math.random() * 5.0f);
                rotateSpeed *= 1.0f - rotationPercentage;
            }

            this.rotationListener.setRotation(rotation, RotationPriority.HIGH, rotateSpeed,
                    this.correlationStrength.getValue(), this.movementFix.getValue());
        } else {
            this.rotationListener.resetRotation();
        }
    }

    @Override
    public void onRaytrace(final RaytraceEvent event) {
        if (this.target != null && this.rotationListener.getRotation() != null) {
            event.range = Math.pow(getRange() - 0.05, 2);
        }
    }

    private void getTarget() {
        final List<LivingEntity> entities = new ArrayList<>();

        for (final Entity entity : mc.world.getEntities()) {
            if (WorldUtil.isTarget(entity) && mc.player.distanceTo(entity) <= getAimRange() + 1.0) {
                entities.add((LivingEntity) entity);
            }
        }

        if (entities.isEmpty()) {
            this.target = null;
            return;
        }

        //Sort entities by distance
        entities.sort(Comparator.comparingDouble(entity -> this.mc.player.distanceTo(entity)));

        if (!this.switchTarget.getValue() || this.targetIndex >= entities.size()) {
            this.targetIndex = 0;
        }

        this.target = entities.get(this.targetIndex);
    }

    private double getAimRange() {
        return getRange() + this.preAimRangeExtension.getValue();
    }

    private double getRange() {
        if (this.firstHitExtender.getValue() &&
                (System.currentTimeMillis() - this.lastPossibleHit) >= this.firstHitExtenderOffTime.getValue() &&
                this.isLooking) {
            return this.range.getValue() + this.firstHitRangeExtender.getValue();
        }

        return this.range.getValue();
    }

    private void updateClicker(final Clicker clicker) {
        if (clicker instanceof final BoxMuellerClicker boxMuellerClicker) {
            boxMuellerClicker.setStd(5);
            boxMuellerClicker.setMean(15);
            boxMuellerClicker.setCpsUpdatePossibility(80);
        }

        clicker.setClickAction(attack -> {
            if (attack) {
                if (this.raytraceDistance <= getRange()) {
                    this.lastPossibleHit = System.currentTimeMillis();
                }

                this.mc.doAttack();
                this.targetIndex++;
            } else if (autoBlock.isActive()) {
                autoBlock.setBlocking(true);
            }
        });
    }

}
