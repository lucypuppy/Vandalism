/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.BezierValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.SeparatorValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.player.RaytraceListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.AutoSprintModule;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationBuilder;
import de.nekosarekawaii.vandalism.integration.rotation.RotationManager;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.click.ClickType;
import de.nekosarekawaii.vandalism.util.click.Clicker;
import de.nekosarekawaii.vandalism.util.click.impl.BezierClicker;
import de.nekosarekawaii.vandalism.util.click.impl.BoxMuellerClicker;
import de.nekosarekawaii.vandalism.util.click.impl.CooldownClicker;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KillAuraModule extends AbstractModule implements PlayerUpdateListener, Render2DListener, Render3DListener, RotationListener, RaytraceListener {

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

    private final SeparatorValue reach = new SeparatorValue(
            this.targetSelectionGroup,
            "Reach",
            "Settings for the reach."
    );

    private final EnumModeValue<SelectionMode> selectionMode = new EnumModeValue<>(
            this.targetSelectionGroup,
            "Target Selection Mode",
            "The mode of the target selection.",
            SelectionMode.RANGE,
            SelectionMode.values()
    );

    private final BooleanValue switchTarget = new BooleanValue(
            this.targetSelectionGroup,
            "Switch Target",
            "Whether the target should be switched.",
            true
    );

    private final BooleanValue ignoreCreativePlayers = new BooleanValue(
            this.targetSelectionGroup,
            "Ignore Creative Players",
            "Whether creative players should be ignored.",
            false
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
            ClickType.COOLDOWN,
            ClickType.values()
    ).onValueChange((oldValue, newValue) -> {
        oldValue.getClicker().setClickAction(aBoolean -> {
        });
        this.updateClicker(newValue.getClicker());
    });

    private final FloatValue std = new FloatValue(
            this.clicking,
            "Standard Deviation",
            "The standard deviation for the Box-Mueller clicker.",
            5.0f,
            1.0f,
            10.0f
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final FloatValue mean = new FloatValue(
            this.clicking, "Mean",
            "The mean for the Box-Mueller clicker.",
            15.0f, 1.0f, 30.0f
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final IntegerValue minCps = new IntegerValue(
            this.clicking,
            "Minimum CPS",
            "The minimum CPS for the Box-Mueller clicker.",
            10,
            1,
            20
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final IntegerValue maxCps = new IntegerValue(
            this.clicking,
            "Maximum CPS",
            "The maximum CPS for the Box-Mueller clicker.",
            20,
            1,
            30
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final BezierValue cpsBezier = new BezierValue(
            this.clicking,
            "CPS Bezier Curve",
            "The bezier curve for the CPS.",
            25.0f,
            17.0f,
            14.0f,
            25.0f,
            1.0f,
            25.0f
    ).visibleCondition(() -> this.clickType.getValue() == ClickType.BEZIER);

    private final IntegerValue updatePossibility = new IntegerValue(
            this.clicking,
            "Update Possibility",
            "The possibility of the CPS update.",
            80,
            0,
            100
    ).visibleCondition(() -> this.clickType.getValue() != ClickType.COOLDOWN);

    private final BooleanValue preHit = new BooleanValue(
            this.clicking,
            "Pre Hit",
            "Whether you want to pre hit in the extended range (doesnt work in cooldown mode).",
            false
    ).visibleCondition(() -> this.clickType.getValue() != ClickType.COOLDOWN);

    private final ValueGroup rotationGroup = new ValueGroup(
            this,
            "Rotation",
            "Settings for the rotations."
    );

    private final ValueGroup rotationSpeedGroup = new ValueGroup(
            this.rotationGroup,
            "Rotation Speed Generation",
            "Settings for the rotation speed generation."
    );

    private final EnumModeValue<SmoothingType> rotationSpeedType = new EnumModeValue<>(
            this.rotationSpeedGroup,
            "Rotation Speed Type",
            "The type of the rotation speed.",
            SmoothingType.BEZIER,
            SmoothingType.values()
    );

    private final FloatValue rotateSpeed = new FloatValue(
            this.rotationSpeedGroup,
            "Rotate Speed",
            "The speed of the rotation.",
            60.0f,
            1.0f,
            180.0f
    ).visibleCondition(() -> this.rotationSpeedType.getValue() == SmoothingType.NORMAL);

    private final BezierValue rotationSpeedBezier = new BezierValue(
            this.rotationSpeedGroup,
            "Rotation Speed Bezier Curve",
            "The bezier curve for the rotation speed.",
            40.0f,
            20.0f,
            90.0f,
            50.0f,
            1.0f,
            180.0f
    ).visibleCondition(() -> this.rotationSpeedType.getValue() == SmoothingType.BEZIER);

    private final FloatValue correlationStrength = new FloatValue(
            this.rotationGroup,
            "Correlation Strength",
            "The strength of the correlation.",
            0.2f,
            0.0f,
            1.0f
    );

    private final ValueGroup windMouseGroup = new ValueGroup(
            this.rotationGroup,
            "Wind Mouse Settings",
            "Settings for the WindMouse algorithm."
    );

    private final BooleanValue windMouse = new BooleanValue(
            this.windMouseGroup,
            "Wind Mouse",
            "Whether the WindMouse algorithm should be used.",
            true);

    private final FloatValue gravitationalForce = new FloatValue(
            this.windMouseGroup,
            "Gravitational Force",
            "The strength pf the Gravitational Force.",
            9f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final FloatValue windForceMagnitude = new FloatValue(
            this.windMouseGroup,
            "Wind Force Magnitude",
            "The strength of the Wind Force Magnitude.",
            3f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final FloatValue maxStepSize = new FloatValue(
            this.windMouseGroup,
            "Max Step Size",
            "The Max Step Size.",
            15f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final FloatValue distanceThreshold = new FloatValue(
            this.windMouseGroup,
            "Distance threshold",
            "The Distance Threshold.",
            12f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

//    private final SeparatorValue movementFixSeparator = new SeparatorValue(
//            this.rotationGroup,
//            "Movement Fix Separator",
//            "Settings for the movement fix."
//    );

    private final BooleanValue movementFix = new BooleanValue(
            this.rotationGroup,
            "Movement Fix",
            "Whether the movement fix should be used.",
            true
    );

    private final ValueGroup autoBlockGroup = new ValueGroup(
            this,
            "Auto Block",
            "Settings for the auto block."
    );

    private final EnumModeValue<AutoBlockMode> autoBlockMode = new EnumModeValue<>(
            this.autoBlockGroup,
            "Auto Block Mode",
            "The mode of the auto block.",
            AutoBlockMode.OFF,
            AutoBlockMode.values()
    );

    private final ValueGroup extraSettings = new ValueGroup(
            this,
            "Extra Settings",
            "Extra settings for the auto block."
    );

    public final BooleanValue noHitSlow = new BooleanValue(
            this.extraSettings,
            "No Hit Slowdown",
            "Whether you should slow down when hitting.",
            false
    );

    private final BooleanValue unsprintOnAttack = new BooleanValue(
            this.extraSettings,
            "Unsprint On Attack",
            "Whether you should unsprint on attack.",
            true
    );

    private final BooleanValue resprintAfterAttack = new BooleanValue(
            this.extraSettings,
            "Resprint After Attack",
            "Whether you should resprint after attack.",
            true
    );

    private final ValueGroup renderGroup = new ValueGroup(
            this,
            "Render",
            "Settings for aura render stuff."
    );

    private final BooleanValue targetESP = new BooleanValue(
            this.renderGroup,
            "Target ESP",
            "Whether to render the target ESP.",
            true
    );

    private final ColorValue targetColor1 = new ColorValue(
            this.renderGroup,
            "First Target Color",
            "The first color of the target ESP.",
            new Color(0x00FF00)
    ).visibleCondition(this.targetESP::getValue);

    private final ColorValue targetColor2 = new ColorValue(
            this.renderGroup,
            "Second Target Color",
            "The second color of the target ESP.",
            new Color(0x00FF00)
    ).visibleCondition(this.targetESP::getValue);

    private Entity target;
    private int targetIndex = 0;

    private double raytraceDistance = -1.0;
    private boolean isLooking = false;

    private long lastPossibleHit = -1;

    private final RotationManager rotationManager;

    public boolean isBlocking = false;
    private Rotation prevRotation;

    public KillAuraModule() {
        super(
                "Kill Aura",
                "Automatically attacks nearby enemies.",
                Category.COMBAT
        );

        this.rotationManager = Vandalism.getInstance().getRotationManager();
        this.updateClicker(this.clickType.getValue().getClicker());

        this.deactivateAfterSessionDefault();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID, Render2DEvent.ID, RotationEvent.ID, RaytraceEvent.ID, Render3DEvent.ID
        );
        updateClicker(this.clickType.getValue().getClicker());
        this.autoSprintModule = Vandalism.getInstance().getModuleManager().getByClass(AutoSprintModule.class);
        if (mc.player != null) {
            this.prevRotation = new Rotation(mc.player.prevYaw, mc.player.prevPitch);
        }
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID, Render2DEvent.ID, RotationEvent.ID, RaytraceEvent.ID, Render3DEvent.ID
        );

        this.rotationManager.resetRotation(RotationPriority.HIGH);
        this.targetIndex = 0;

        stopBlocking(BlockState.ERROR);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.target == null || this.rotationManager.getRotation() == null) {
            stopBlocking(BlockState.ERROR);
            return;
        }

        // Check if the target is looking at us
        final Rotation pseudoRotation = RotationBuilder.build(this.mc.player.getPos(), this.target.getEyePos());
        this.isLooking = Math.abs(MathHelper.wrapDegrees(pseudoRotation.getYaw()) - MathHelper.wrapDegrees(this.target.getYaw())) <= 80.0 &&
                this.mc.player.getPos().distanceTo(this.target.getPos()) <= 6.0;

        final double raytraceReach = this.preHit.getValue() && this.clickType.getValue() != ClickType.COOLDOWN ? this.getAimRange() : this.getRange();
        final Vec3d eyePos = mc.player.getEyePos();
        final HitResult raytrace = WorldUtil.raytrace(this.rotationManager.getRotation(), raytraceReach);
        this.raytraceDistance = raytrace != null && raytrace.getType() != HitResult.Type.MISS ? eyePos.distanceTo(raytrace.getPos()) : -1.0;

        if (this.raytraceDistance > raytraceReach || this.raytraceDistance < 0) {
            stopBlocking(BlockState.ERROR);
            this.targetIndex = 0;
            return;
        }

        stopBlocking(BlockState.PRE_CLICKING);
        this.clickType.getValue().getClicker().onUpdate();
        startBlocking(BlockState.POST_CLICKING);
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (mc.player == null || mc.interactionManager == null || mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }

        this.updateTarget();

        if (this.target != null) {
            if (this.raytraceDistance <= this.getAimRange()) {
                this.clickType.getValue().getClicker().onRotate();
            }

            Rotation rotation = RotationBuilder.build(this.target, RotationPriority.HIGH,
                    true, this.getAimRange());

            if (rotation == null) { // Sanity check, crashes if you sneak and have your reach set to 3.0
                this.rotationManager.resetRotation(RotationPriority.HIGH);
                return;
            }

            float rotateSpeed = 0.0f;

            if (this.rotationSpeedType.getValue() == SmoothingType.BEZIER) {
                float rotationPercentage;

                if (this.rotationManager.getRotation() == null) {
                    final Rotation playerRotation = new Rotation(this.mc.player.getYaw(), this.mc.player.getPitch());
                    rotationPercentage = RotationUtil.calculateRotationPercentage(playerRotation.getYaw(), rotation.getYaw(), true);
                } else {
                    rotationPercentage = RotationUtil.calculateRotationPercentage(rotationManager.getRotation().getYaw(), rotation.getYaw(), true);
                }

                rotateSpeed = this.rotationSpeedBezier.getValue(rotationPercentage);
            } else if (this.rotationSpeedType.getValue() == SmoothingType.NORMAL) {
                rotateSpeed = (float) (this.rotateSpeed.getValue() + Math.random() * 5.0f);
            }

            if (windMouse.getValue()) {
                rotation = RotationUtil.windMouseSmooth(
                        rotation, prevRotation,
                        gravitationalForce.getValue(), windForceMagnitude.getValue(), maxStepSize.getValue(), distanceThreshold.getValue()
                );
            }

//            rotation = WindMouse.updateMouse(rotation, prevRotation);

            // rotation.setPitch((float) (rotation.getPitch() + Math.random() * 5));

            prevRotation = rotation;
            this.rotationManager.setRotation(
                    rotation,
                    rotateSpeed,
                    this.correlationStrength.getValue(),
                    this.movementFix.getValue()
            );
        } else {
            this.rotationManager.resetRotation(RotationPriority.HIGH);
        }
    }

    @Override
    public void onRaytrace(final RaytraceEvent event) {
        if (this.target != null && this.rotationManager.getRotation() != null) {
            event.range = getRange();
        }
    }

    private void updateTarget() {
        final List<Entity> entities = new ArrayList<>();

        for (final Entity entity : this.mc.world.getEntities()) {
            if (entity == this.mc.player || entity == this.mc.player.getVehicle()) {
                continue;
            }
            if (
                    Vandalism.getInstance().getTargetManager().isTarget(entity) &&
                            this.mc.player.distanceTo(entity) <= getAimRange() + 1.0 &&
                            entity.getWidth() > 0.0 && entity.getHeight() > 0.0
            ) {
                PlayerListEntry playerListEntry = null;

                if (entity instanceof PlayerEntity player) {
                    playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
                }

                if (playerListEntry == null || !this.ignoreCreativePlayers.getValue() || !playerListEntry.getGameMode().isCreative()) {
                    entities.add(entity);
                }
            }
        }

        if (entities.isEmpty()) {
            this.target = null;
            return;
        }

        final Vec3d eyePos = this.mc.player.getEyePos();
        switch (this.selectionMode.getValue()) {
            case RANGE -> entities.sort((entity1, entity2) -> {
                final double distance1 = eyePos.distanceTo(RotationBuilder.getNearestPoint(entity1));
                final double distance2 = eyePos.distanceTo(RotationBuilder.getNearestPoint(entity2));
                return Double.compare(distance1, distance2);
            });

            case HEALTH -> entities.sort((entity1, entity2) -> {
                final double health1 = entity1 instanceof LivingEntity living1 ? living1.getHealth() : 9999;
                final double health2 = entity2 instanceof LivingEntity living2 ? living2.getHealth() : 9999;
                return Double.compare(health1, health2);
            });

            case ARMOR -> entities.sort((entity1, entity2) -> {
                final double armor1 = entity1 instanceof LivingEntity living1 ? living1.getArmor() : 9999;
                final double armor2 = entity2 instanceof LivingEntity living2 ? living2.getArmor() : 9999;
                return Double.compare(armor1, armor2);
            });

            case GOMME_HEALTH -> entities.sort((entity1, entity2) -> {
                double health1 = getHealthFromScoreboard(entity1);
                double health2 = getHealthFromScoreboard(entity2);
                if (health1 == 9999 && health2 == 9999) {
                    final double distance1 = eyePos.distanceTo(RotationBuilder.getNearestPoint(entity1));
                    final double distance2 = eyePos.distanceTo(RotationBuilder.getNearestPoint(entity2));
                    return Double.compare(distance1, distance2);
                }
                return Double.compare(health1, health2);
            });
        }

        if (!this.switchTarget.getValue() || this.targetIndex >= entities.size()) {
            this.targetIndex = 0;
        }

        this.target = entities.get(this.targetIndex);
    }

    private double getHealthFromScoreboard(Entity entity) {
        Scoreboard scoreboard = mc.player.getScoreboard();

        for (ScoreboardEntry entry : scoreboard.getScoreboardEntries(scoreboard.getNullableObjective("health"))) {
            if (entry.owner().equalsIgnoreCase(entity.getName().getString())) {
                return entry.value();
            }
        }

        for (ScoreboardEntry entry : scoreboard.getScoreboardEntries(scoreboard.getNullableObjective("showhealth"))) {
            if (entry.owner().equalsIgnoreCase(entity.getName().getString())) {
                return entry.value();
            }
        }
        return 9999; // default value if health is not found
    }

    public BooleanValue getPreHit() {
        return preHit;
    }

    public double getAimRange() {
        return getRange() + this.preAimRangeExtension.getValue();
    }

    public double getRange() {
        if (
                this.firstHitExtender.getValue() &&
                        (System.currentTimeMillis() - this.lastPossibleHit) >= this.firstHitExtenderOffTime.getValue() &&
                        this.isLooking
        ) {
            return this.range.getValue() + this.firstHitRangeExtender.getValue();
        }

        return this.range.getValue();
    }

    private void updateClicker(final Clicker clicker) {
        if (clicker instanceof final BoxMuellerClicker boxMuellerClicker) {
            boxMuellerClicker.setKillAuraModule(this);
            boxMuellerClicker.setStd(this.std.getValue());
            boxMuellerClicker.setMean(this.mean.getValue());
            boxMuellerClicker.setMinCps(this.minCps.getValue());
            boxMuellerClicker.setMaxCps(this.maxCps.getValue());
            boxMuellerClicker.setCpsUpdatePossibility(this.updatePossibility.getValue());
        } else if (clicker instanceof final BezierClicker bezierClicker) {
            bezierClicker.setBezierValue(this.cpsBezier);
            bezierClicker.setCpsUpdatePossibility(this.updatePossibility.getValue());
        } else if (clicker instanceof final CooldownClicker cooldownClicker) {
            cooldownClicker.setKillAuraModule(this);
        }

        clicker.setClickAction(attack -> {
            if (attack) {
                final boolean possibleHit = this.raytraceDistance <= getRange();

                if (possibleHit) {
                    this.lastPossibleHit = System.currentTimeMillis();
                }

                this.hit();
            }
        });
    }

    private AutoSprintModule autoSprintModule;

    private void hit() {
        if (this.unsprintOnAttack.getValue()) {
            this.mc.player.setSprinting(false);
            autoSprintModule.stopSprinting(true);
        }

        stopBlocking(BlockState.PRE_ATTACK);
        this.mc.doAttack();
        startBlocking(BlockState.POST_ATTACK);

        if (this.resprintAfterAttack.getValue()) {
            this.mc.player.setSprinting(true);
            autoSprintModule.stopSprinting(false);
        }

        this.targetIndex++;
    }

    private void startBlocking(final BlockState blockState) {
        if (this.autoBlockMode.getValue() == AutoBlockMode.OFF) {
            return;
        }

        if (this.autoBlockMode.getValue() == AutoBlockMode.RIGHT_CLICK_PERMANENT) {
            mc.options.useKey.setPressed(true); // Ensure we are blocking permanently
        } else if (this.autoBlockMode.getValue() == AutoBlockMode.TEST) {
            if (blockState == BlockState.POST_ATTACK) {
                final ActionResult actionResult = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                if (actionResult.isAccepted()) {
                    if (actionResult.shouldSwingHand()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            }

            this.isBlocking = true;
        }
    }

    private void stopBlocking(final BlockState blockState) {
        if (this.autoBlockMode.getValue() == AutoBlockMode.OFF) {
            return;
        }

        if (this.autoBlockMode.getValue() == AutoBlockMode.RIGHT_CLICK_PERMANENT) {
            mc.options.useKey.setPressed(blockState != BlockState.ERROR);
        } else if (this.autoBlockMode.getValue() == AutoBlockMode.TEST) {
            if (this.mc.player.isBlocking() || this.isBlocking) {
                if (blockState == BlockState.PRE_ATTACK) {
                    this.mc.interactionManager.stopUsingItem(mc.player);
                }

                this.isBlocking = false;
            }
        }
    }

    public Entity getTarget() {
        return this.target;
    }

    @Override
    public void onRender3D(float tickDelta, long limitTime, MatrixStack matrixStack) {
        //    if (this.clickType.getValue().getClicker() instanceof final BoxMuellerClicker boxMuellerClicker) {
        //        final List<Vector4d> cpsHistory = boxMuellerClicker.getCpsHistory().getNormalList();
//
        //        if (cpsHistory.size() < 5) {
        //            return;
        //        }
//
        //        final Double[] x = new Double[cpsHistory.size()];
        //        final Double[] y1 = new Double[cpsHistory.size()];
        //        final Double[] y2 = new Double[cpsHistory.size()];
        //        final Double[] y3 = new Double[cpsHistory.size()];
        //        final Double[] y4 = new Double[cpsHistory.size()];
//
        //        for (int i = 0; i < cpsHistory.size(); i++) {
        //            final Vector4d cps = cpsHistory.get(i);
        //            x[i] = (double) i;
        //            y1[i] = cps.x;
        //            y2[i] = cps.y;
        //            y3[i] = cps.z;
        //            y4[i] = cps.w;
        //        }
//
        //        ImLoader.draw(() -> {
        //            if (ImGui.begin("CPS History")) {
        //                if (ImPlot.beginPlot("CPS History")) {
        //                    ImPlot.plotLine("CPS", x, y1);
        //                    ImPlot.plotLine("Gaussian", x, y2);
        //                    ImPlot.plotLine("Gaussian Percentage", x, y3);
        //                    ImPlot.plotLine("Gaussian Density", x, y4);
        //                    ImPlot.endPlot();
        //                }
        //            }
        //            ImGui.end();
        //        });
        //    }

        if (!this.targetESP.getValue() || this.getTarget() == null) {
            return;
        }

        final Vec3d pos = this.getTarget().getPos();

        if (this.getTarget() instanceof final LivingEntity entity) {
            matrixStack.push();

            final Box box = new Box(
                    pos.x - entity.getWidth() / 2f,
                    pos.y + entity.getHeight() / 2 + Math.sin(System.currentTimeMillis() * 0.005) * 0.9,
                    pos.z - entity.getWidth() / 2f,
                    pos.x + entity.getWidth() / 2f,
                    pos.y + entity.getHeight() / 2 + 0.11 + Math.sin(System.currentTimeMillis() * 0.005) * 0.9,
                    pos.z + entity.getWidth() / 2f
            );

            final Box box2 = new Box(
                    pos.x - entity.getWidth() / 2f,
                    pos.y + entity.getHeight() / 2 + Math.sin(System.currentTimeMillis() * 0.005 + Math.PI) * 0.9,
                    pos.z - entity.getWidth() / 2f,
                    pos.x + entity.getWidth() / 2f,
                    pos.y + entity.getHeight() / 2 + 0.11 + Math.sin(System.currentTimeMillis() * 0.005 + Math.PI) * 0.9,
                    pos.z + entity.getWidth() / 2f
            );

            final Vec3d center = box.getCenter();
            final Vec3d center2 = box2.getCenter();

            final Vec3d camPos = this.mc.gameRenderer.getCamera().getPos();
            matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.disableCull();
            final Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            int points = 24;
            for (int i = 0; i <= points; i++) {
                double angle = (i / (double) points) * Math.PI * 2;
                double radius = this.getTarget().getWidth();
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                buffer.vertex(matrixStack.peek().getPositionMatrix(), (float) (center.x + x), (float) center.y, (float) (center.z + z)).color(targetColor1.getColor().getRGB());
            }
            BufferRenderer.drawWithGlobalProgram(buffer.end());

            buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i <= points; i++) {
                double angle = (i / (double) points) * Math.PI * 2;
                double radius = this.getTarget().getWidth();
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                buffer.vertex(matrixStack.peek().getPositionMatrix(), (float) (center2.x + x), (float) center2.y, (float) (center2.z + z)).color(targetColor2.getColor().getRGB());
            }
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            RenderSystem.enableCull();
            matrixStack.pop();
        }
    }

    private enum SmoothingType implements IName {

        BEZIER,
        NORMAL,
        NONE;

        private final String name;

        SmoothingType() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private enum AutoBlockMode implements IName {

        OFF,
        RIGHT_CLICK_PERMANENT,
        TEST;

        private final String name;

        AutoBlockMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private enum BlockState {

        ERROR,
        PRE_CLICKING,
        POST_CLICKING,
        PRE_ATTACK,
        POST_ATTACK

    }

    private enum SelectionMode implements IName {

        RANGE,
        HEALTH,
        ARMOR,
        GOMME_HEALTH;

        private final String name;

        SelectionMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
