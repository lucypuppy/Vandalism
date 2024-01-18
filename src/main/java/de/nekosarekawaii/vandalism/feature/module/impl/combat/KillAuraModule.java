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

import de.florianmichael.rclasses.pattern.evicting.EvictingList;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.*;
import de.nekosarekawaii.vandalism.base.event.normal.render.Render2DListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.HitboxSelectMode;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationPriority;
import de.nekosarekawaii.vandalism.util.click.Clicker;
import de.nekosarekawaii.vandalism.util.click.impl.BoxMuellerClicker;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAuraModule extends AbstractModule implements PlayerUpdateListener, StrafeListener, Render2DListener, MoveInputListener, RotationListener, RaytraceListener {

    private final ValueGroup targetSelectionGroup = new ValueGroup(
            this,
            "Target Selection",
            "Settings for the target selection."
    );

    private final Value<Double> range = new DoubleValue(
            this.targetSelectionGroup,
            "Range",
            "The range extension for the aim.",
            3.0,
            2.0,
            6.0
    );

    private final Value<Double> preAimRangeExtension = new DoubleValue(
            this.targetSelectionGroup,
            "Pre Aim Range Extension",
            "The range extension for the aim.",
            3.0,
            0.0,
            3.0
    );

    private final Value<Boolean> reachExtendExploit = new BooleanValue(
            this.targetSelectionGroup,
            "Reach Extend Exploit",
            "Whether the reach extend exploit should be used.",
            false
    );

    private final Value<Boolean> switchTarget = new BooleanValue(
            this.targetSelectionGroup,
            "Switch Target",
            "Whether the target should be switched.",
            true
    );

    private final ValueGroup rotationGroup = new ValueGroup(
            this,
            "Rotation",
            "Settings for the rotations."
    );

    private final Value<Integer> aimPoints = new IntegerValue(
            this.rotationGroup,
            "Aim Points",
            "The amount of aim points. (Higher values can cause lag)",
            32,
            1,
            100
    );

    private LivingEntity target;
    private int targetIndex = 0;

    private Vec3d rotationVector;

    private double raytraceDistance = -1.0;
    private final EvictingList<Double> reachList = new EvictingList<>(new ArrayList<>(), 10);

    private final Clicker clicker = new BoxMuellerClicker();
    private final de.nekosarekawaii.vandalism.integration.rotation.RotationListener rotationListener;
    private final AutoBlockModule autoBlockModule;

    public KillAuraModule() {
        super(
                "Kill Aura",
                "Automatically attacks nearby enemies.",
                Category.COMBAT
        );

        this.rotationListener = Vandalism.getInstance().getRotationListener();
        this.autoBlockModule = Vandalism.getInstance().getModuleManager().getAutoBlockModule();
        this.markExperimental();

        if (clicker instanceof final BoxMuellerClicker clicker) {
            clicker.setStd(5);
            clicker.setMean(15);
            clicker.setCpsUpdatePossibility(80);
        }

        this.clicker.setClickAction(attack -> {
            if (attack) {
                this.mc.doAttack();

                // if (this.raytraceDistance > this.range.getValue()) {
                //     ChatUtil.infoChatMessage("Extended by " + (this.raytraceDistance - this.range.getValue()));
                // }

                this.reachList.add(this.raytraceDistance);
                this.targetIndex++;
            } else if (this.autoBlockModule.isActive()) {
                this.autoBlockModule.setBlocking(true);
            }
        });
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID, StrafeEvent.ID,
                Render2DEvent.ID, MoveInputEvent.ID,
                RotationEvent.ID, RaytraceEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID, StrafeEvent.ID,
                Render2DEvent.ID, MoveInputEvent.ID,
                RotationEvent.ID, RaytraceEvent.ID
        );

        this.rotationListener.resetRotation();
        this.targetIndex = 0;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        this.getTarget();
        if (this.target == null ||
                this.rotationVector == null ||
                this.rotationListener.getRotation() == null ||
                Float.isNaN(this.rotationListener.getRotation().getYaw()) ||
                Float.isNaN(this.rotationListener.getRotation().getPitch())) {
            return;
        }

        final Vec3d eyePos = mc.player.getEyePos();
        final HitResult raytrace = WorldUtil.raytrace(this.rotationListener.getRotation(), Math.pow(getAimRange(), 2));
        this.raytraceDistance = raytrace != null ? eyePos.distanceTo(raytrace.getPos()) : -1.0;

        if (this.raytraceDistance > getAimRange() || this.raytraceDistance <= 0) {
            return;
        }

        this.clicker.onUpdate();
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        /*Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (ImGui.begin("Graph##killauramodule", Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags())) {
                if (this.clicker instanceof final BoxMuellerClicker clicker) {
                    final int size = clicker.getDelays().getNormalList().size();
                    if (size > 5) {
                        final Long[] xAxis = new Long[size];
                        final Long[] yAxis = new Long[size];
                        final Long[] yAxis2 = new Long[size];
                        for (int i = 0; i < size; i++) {
                            xAxis[i] = (long) i;
                            yAxis[i] = clicker.getDelays().getNormalList().get(i).getLeft();
                            yAxis2[i] = clicker.getDelays().getNormalList().get(i).getRight().longValue() * 20L;
                        }
                        if (ImPlot.beginPlot("KillAuraCPSGraph")) {
                            ImPlot.plotLine("Delay", xAxis, yAxis);
                            ImPlot.plotLine("CPS", xAxis, yAxis2);
                            ImPlot.endPlot();
                        }
                    }
                }
                ImGui.end();
            }
        });*/
    }

    @Override
    public void onMoveInput(final MoveInputEvent event) {
        /*final Rotation rotation = this.rotationListener.getRotation();
        if (rotation == null) return;
        float deltaYaw = mc.player.getYaw() - rotation.getYaw();

        float x = event.movementSideways;
        float z = event.movementForward;

        float newX = x * MathHelper.cos(deltaYaw * 0.017453292f) - z * MathHelper.sin(deltaYaw * 0.017453292f);
        float newZ = z * MathHelper.cos(deltaYaw * 0.017453292f) + x * MathHelper.sin(deltaYaw * 0.017453292f);

        event.movementSideways = Math.round(newX);
        event.movementForward = Math.round(newZ);*/
    }

    @Override
    public void onStrafe(final StrafeEvent event) {
        if (this.rotationListener.getRotation() == null || this.rotationListener.getTargetRotation() == null) return;
        event.yaw = this.rotationListener.getRotation().getYaw();

       /* float[] INPUTS = MovementUtil.getFixedMoveInputs(event.yaw);
        if (INPUTS[0] == 0f && INPUTS[1] == 0f) {
            return;
        }
        event.movementInput = new Vec3d(INPUTS[0], mc.player.upwardSpeed, INPUTS[1]);*/
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (this.target != null) {
            final Rotation rotation = Rotation.Builder.build(this.target, getAimRange(), this.aimPoints.getValue(), HitboxSelectMode.Circular);

            if (rotation == null) { //Sanity check, crashes if you sneak and have your reach set to 3.0
                this.rotationVector = null;
                this.rotationListener.resetRotation();
                return;
            }

            this.rotationListener.setRotation(rotation, 60, RotationPriority.HIGH);
            this.rotationVector = new Vec3d(1, 1, 1);
        } else {
            this.rotationListener.resetRotation();
        }
    }

    @Override
    public void onRaytrace(RaytraceEvent event) {
        if (this.target != null && this.rotationListener.getRotation() != null) {
            event.range = Math.pow(getRange() - 0.05, 2);
            // ChatUtil.infoChatMessage("Raytrace Range: " + Math.sqrt(event.range));
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
        if (this.reachExtendExploit.getValue() && !this.reachList.getNormalList().isEmpty()) {
            final double average = this.reachList.getNormalList().stream()
                    .mapToDouble(Double::doubleValue).average().orElse(0.0);
            final double extend = (this.range.getValue() - average) / 4.0;

            if (extend > 0.0) {
                //ChatUtil.infoChatMessage("Reach " + (this.range.getValue() + extend) + " (+" + extend + ")");
                return this.range.getValue() + extend;
            }

            return this.range.getValue();
        }

        return this.range.getValue();
    }

}
