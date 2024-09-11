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

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.BezierValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.player.RaytraceListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.AutoSoupModule;
import de.nekosarekawaii.vandalism.feature.module.template.clicking.Clicker;
import de.nekosarekawaii.vandalism.feature.module.template.clicking.ClickerModeValue;
import de.nekosarekawaii.vandalism.feature.module.template.clicking.impl.BezierClicker;
import de.nekosarekawaii.vandalism.feature.module.template.clicking.impl.BoxMuellerClicker;
import de.nekosarekawaii.vandalism.feature.module.template.clicking.impl.CooldownClicker;
import de.nekosarekawaii.vandalism.feature.module.template.target.TargetGroup;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationManager;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.EntityHitPoint;
import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.hitpoints.entity.IcarusBHV;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.Randomizer;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.RandomizerModeValue;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer.SimplexRandomizer;
import de.nekosarekawaii.vandalism.util.*;
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
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KillAuraModule extends Module implements PlayerUpdateListener, Render2DListener, Render3DListener, RotationListener, RaytraceListener {

    private final ValueGroup targetSelectionGroup = new ValueGroup(
            this,
            "Target Selection",
            "Settings for the target selection."
    );

    private final TargetGroup targetGroup = new TargetGroup(this.targetSelectionGroup, "Targets", "The entities to target.");

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

    private final ClickerModeValue clickType = new ClickerModeValue(
            this.clicking,
            "Click Type",
            "The type of clicking."
    ).onValueChange((oldValue, newValue) -> {
        oldValue.setClickAction(aBoolean -> {
        });
        this.updateClicker(newValue);
    });

    private final FloatValue std = new FloatValue(
            this.clicking,
            "Standard Deviation",
            "The standard deviation for the Box-Mueller clicker.",
            5.0f,
            1.0f,
            10.0f
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue()))
            .visibleCondition(() -> this.clickType.getValue() instanceof BoxMuellerClicker);

    private final FloatValue mean = new FloatValue(
            this.clicking, "Mean",
            "The mean for the Box-Mueller clicker.",
            15.0f, 1.0f, 30.0f
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue()))
            .visibleCondition(() -> this.clickType.getValue() instanceof BoxMuellerClicker);

    private final IntegerValue minCps = new IntegerValue(
            this.clicking,
            "Minimum CPS",
            "The minimum CPS for the clicker.",
            10,
            1,
            20
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue()))
            .visibleCondition(() -> this.clickType.getValue() instanceof BoxMuellerClicker);

    private final IntegerValue maxCps = new IntegerValue(
            this.clicking,
            "Maximum CPS",
            "The maximum CPS for the clicker.",
            20,
            1,
            30
    )
            .onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue()))
            .visibleCondition(() -> this.clickType.getValue() instanceof BoxMuellerClicker);

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
    ).visibleCondition(() -> this.clickType.getValue() instanceof BezierClicker);

    private final IntegerValue updatePossibility = new IntegerValue(
            this.clicking,
            "Update Possibility",
            "The possibility of the CPS update.",
            80,
            0,
            100
    ).visibleCondition(() -> !(this.clickType.getValue() instanceof CooldownClicker));

    private final BooleanValue preHit = new BooleanValue(
            this.clicking,
            "Pre Hit",
            "Whether you want to pre hit in the extended range (doesnt work in cooldown mode).",
            false
    ).visibleCondition(() -> !(this.clickType.getValue() instanceof CooldownClicker));

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

    private final BooleanValue clientRotations = new BooleanValue(
            this.rotationGroup,
            "Client Rotations",
            "Whenever the rotations are clientside or nor.",
            false
    );

    private final ValueGroup randomisation = new ValueGroup(
            this.rotationGroup,
            "Randomisation",
            "Settings for the randomisation."
    );

    private final RandomizerModeValue randomizerMode = new RandomizerModeValue(
            this.randomisation,
            "Randomizer Mode",
            "The mode of the randomizer."
    );

    private final DoubleValue maxRadius = new DoubleValue(
            this.randomisation,
            "Max Radius",
            "The maximum radius for the randomiser.",
            0.15,
            0.0,
            1.0
    ).visibleCondition(() -> this.randomizerMode.getValue() instanceof SimplexRandomizer);

    private final DoubleValue maxRadiusY = new DoubleValue(
            this.randomisation,
            "Max Radius Y",
            "The maximum radius Y for the randomiser.",
            0.25,
            0.0,
            1.0
    ).visibleCondition(() -> this.randomizerMode.getValue() instanceof SimplexRandomizer);

    private final DoubleValue mindDistanceToBHV = new DoubleValue(
            this.randomisation,
            "Mind Distance To BHV",
            "The minimum distance to the Best Hit Vector.",
            0.1,
            0.0,
            1.0
    ).visibleCondition(() -> this.randomizerMode.getValue() instanceof SimplexRandomizer);

    private final ValueGroup humanityGroup = new ValueGroup(
            this.rotationGroup,
            "Humanity",
            "These settings make the Killaura more human"
    );

    private final BooleanValue reactionDelay = new BooleanValue(
            this.humanityGroup,
            "Reaction Delay",
            "Whether the reaction delay should be used.",
            true
    );

    private final IntegerValue reactionDelayMin = new IntegerValue(
            this.humanityGroup,
            "Reaction Delay Min",
            "The minimum reaction delay.",
            50,
            0,
            1000
    ).visibleCondition(this.reactionDelay::getValue);

    private final IntegerValue reactionDelayMax = new IntegerValue(
            this.humanityGroup,
            "Reaction Delay Max",
            "The maximum reaction delay.",
            100,
            0,
            1000
    ).visibleCondition(this.reactionDelay::getValue);

    private final BooleanValue outerHitbox = new BooleanValue(
            this.humanityGroup,
            "Outer Hitbox",
            "Only aims if the rotations arent on the hitbox anymore",
            true
    );

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
    public boolean shouldRotate;

    public final MSTimer aimTimer = new MSTimer();

    public final EntityHitPoint points = new IcarusBHV();

    private AutoSoupModule autoSoupModule;

    public KillAuraModule() {
        super(
                "Kill Aura",
                "Automatically attacks nearby enemies.",
                Category.COMBAT
        );

        this.rotationManager = Vandalism.getInstance().getRotationManager();
        this.updateClicker(this.clickType.getValue());

        this.deactivateAfterSessionDefault();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID, Render2DEvent.ID, RotationEvent.ID, RaytraceEvent.ID, Render3DEvent.ID
        );

        this.updateClicker(this.clickType.getValue());

        if (this.autoSoupModule == null)
            this.autoSoupModule = Vandalism.getInstance().getModuleManager().getByClass(AutoSoupModule.class);
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
        if (this.target == null || this.rotationManager.getClientRotation() == null) {
            stopBlocking(BlockState.ERROR);
            return;
        }

        // Check if the target is looking at us
        final Rotation pseudoRotation = RotationUtil.rotationToVec(this.target.getEyePos(), RotationPriority.NORMAL);
        this.isLooking = Math.abs(MathHelper.wrapDegrees(pseudoRotation.getYaw()) - MathHelper.wrapDegrees(this.target.getYaw())) <= 80.0 &&
                this.mc.player.getPos().distanceTo(this.target.getPos()) <= 6.0;

        final double raytraceReach = this.preHit.getValue() && !(this.clickType.getValue() instanceof CooldownClicker) ? this.getAimRange() : this.getRange();
        final Vec3d eyePos = mc.player.getEyePos();
        final HitResult raytrace = WorldUtil.raytrace(this.rotationManager.getClientRotation() != null ? this.rotationManager.getClientRotation() : new Rotation(mc.player.getYaw(), mc.player.getPitch()), raytraceReach);
        this.raytraceDistance = raytrace != null && (preHit.getValue() || raytrace.getType() != HitResult.Type.MISS) ? eyePos.distanceTo(raytrace.getPos()) : -1.0;

        if (this.raytraceDistance > raytraceReach || this.raytraceDistance < 0) {
            stopBlocking(BlockState.ERROR);
            this.targetIndex = 0;
            return;
        }

        stopBlocking(BlockState.PRE_CLICKING);
        boolean shouldUpdate = true;
        final Clicker clicker = this.clickType.getValue();

        if (autoSoupModule != null && autoSoupModule.isActive() && autoSoupModule.getState() != AutoSoupModule.State.WAITING) {
            clicker.clickAction.accept(false);
            shouldUpdate = false;
        }

        if (clicker instanceof final CooldownClicker cooldownClicker) {
            if (this.mc.crosshairTarget == null || this.mc.crosshairTarget.getType() != HitResult.Type.ENTITY) {
                cooldownClicker.clickAction.accept(false);
                shouldUpdate = false;
            }
            if (shouldUpdate) {
                if (
                        this.mc.crosshairTarget instanceof final EntityHitResult entityHitResult &&
                                entityHitResult.getEntity().distanceTo(this.mc.player) >
                                        (this.getPreHit().getValue() ? this.getAimRange() : this.getRange())
                ) {
                    cooldownClicker.clickAction.accept(false);
                    shouldUpdate = false;
                }
            }
        } else if (clicker instanceof final BoxMuellerClicker boxMuellerClicker) {
            if (!this.getPreHit().getValue()) {
                if (this.mc.crosshairTarget == null || this.mc.crosshairTarget.getType() != HitResult.Type.ENTITY) {
                    clicker.clickAction.accept(false);
                    boxMuellerClicker.setClicks(0);
                    shouldUpdate = false;
                }
                if (shouldUpdate) {
                    if (this.mc.crosshairTarget instanceof final EntityHitResult entityHitResult && entityHitResult.getEntity().distanceTo(this.mc.player) > this.getAimRange()) {
                        clicker.clickAction.accept(false);
                        boxMuellerClicker.setClicks(0);
                        shouldUpdate = false;
                    }
                }
            } else {
                if (this.getTarget() == null) {
                    clicker.clickAction.accept(false);
                    boxMuellerClicker.setClicks(0);
                    shouldUpdate = false;
                }
            }
        }
        if (shouldUpdate) clicker.onUpdate();
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
                this.clickType.getValue().onRotate();
            }

            Vec3d aimPos = this.points.generateHitPoint(this.target);
            final Randomizer randomizer = randomizerMode.getValue();

            if (randomizer instanceof final SimplexRandomizer simplexRandomizer) {
                simplexRandomizer.setMaxRadius(maxRadius.getValue());
                simplexRandomizer.setMaxRadiusY(maxRadiusY.getValue());
                simplexRandomizer.setMindDistance(mindDistanceToBHV.getValue());
            }

            //final Vec3d oldVec = aimPos;
            aimPos = randomizerMode.getValue().randomiseRotationVec3d(aimPos);

            //final double mindDist = oldVec.distanceTo(aimPos);
            //if (mindDist < 0.1) {
            //    ChatUtil.infoChatMessage("Shit client gets detected" + mindDist);
            //}

            aimPos = RotationUtil.clampHitpointsToBoundingBox(aimPos, this.target.getBoundingBox());
            final PrioritizedRotation rotation = RotationUtil.rotationToVec(aimPos, RotationPriority.HIGH);

            if (rotation == null) { // Sanity check, crashes if you sneak and have your reach set to 3.0
                this.rotationManager.resetRotation(RotationPriority.HIGH);
                return;
            }

            if (this.rotationManager.getClientRotation() == null || (!this.outerHitbox.getValue() && !this.reactionDelay.getValue())) {
                this.shouldRotate = true;
            } else {
                if (this.outerHitbox.getValue() &&
                        !WorldUtil.canHitEntity(mc.player, target, this.rotationManager.getClientRotation(), getAimRange())) {
                    if (!this.reactionDelay.getValue() ||
                            this.aimTimer.hasReached(RandomUtils.randomInt(this.reactionDelayMin.getMinValue(), this.reactionDelayMax.getValue()), true)) {
                        this.shouldRotate = true;
                    }
                } else {
                    final float yaw = MathHelper.wrapDegrees(rotation.getYaw());
                    final float pitch = rotation.getPitch();
                    final float yawDiff = Math.abs(yaw - MathHelper.wrapDegrees(this.rotationManager.getClientRotation().getYaw()));
                    final float pitchDiff = Math.abs(pitch - this.rotationManager.getClientRotation().getPitch());

                    if (yawDiff <= 2 && pitchDiff <= 2) {
                        this.shouldRotate = false;
                    } else if (!this.outerHitbox.getValue() &&
                            (!this.reactionDelay.getValue() ||
                                    this.aimTimer.hasReached(RandomUtils.randomInt(this.reactionDelayMin.getMinValue(), this.reactionDelayMax.getValue()), true))) {
                        this.shouldRotate = true;
                    }

                    this.aimTimer.reset();
                }
            }

            if (this.shouldRotate) {
                float rotateSpeed = 360.0f;

                if (rotationSpeedType.getValue() == SmoothingType.NORMAL) {
                    rotateSpeed = this.rotateSpeed.getValue();
                }

                this.rotationManager.setRotation(
                        rotation,
                        rotateSpeed,
                        this.correlationStrength.getValue(),
                        this.movementFix.getValue()
                );
            }

            if (this.rotationManager.getClientRotation() != null && this.clientRotations.getValue()) {
                final Rotation clientRotation = this.rotationManager.getClientRotation();
                mc.player.setYaw(clientRotation.getYaw());
                mc.player.setPitch(clientRotation.getPitch());
            }
        } else {
            this.rotationManager.resetRotation(RotationPriority.HIGH);
        }
    }

    @Override
    public void onRaytrace(final RaytraceEvent event) {
        if (this.target != null && this.rotationManager.getClientRotation() != null) {
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
                    this.targetGroup.isTarget(entity) &&
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
                final double distance1 = eyePos.distanceTo(new IcarusBHV().generateHitPoint(entity1));
                final double distance2 = eyePos.distanceTo(new IcarusBHV().generateHitPoint(entity2));
                return Double.compare(distance1, distance2);
            });

            case HEALTH -> entities.sort((entity1, entity2) -> {
                final double health1 = entity1 instanceof LivingEntity living1 ? living1.getHealth() : 9999;
                final double health2 = entity2 instanceof LivingEntity living2 ? living2.getHealth() : 9999;
                return Double.compare(health1, health2);
            });

            case FOV -> entities.sort((entity1, entity2) -> {
                final double distance1 = getAngleToPlayer(entity1);
                final double distance2 = getAngleToPlayer(entity2);
                return Double.compare(distance1, distance2);
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
                    final double distance1 = eyePos.distanceTo(new IcarusBHV().generateHitPoint(entity1));
                    final double distance2 = eyePos.distanceTo(new IcarusBHV().generateHitPoint(entity2));
                    return Double.compare(distance1, distance2);
                }
                if (health1 > 0 && health2 <= 0)
                    return -1;
                if (health1 <= 0 && health2 > 0)
                    return 1;
                return Double.compare(health1, health2);
            });
        }

        if (!this.switchTarget.getValue() || this.targetIndex >= entities.size()) {
            this.targetIndex = 0;
        }

        this.target = entities.get(this.targetIndex);
    }

    private double getAngleToPlayer(Entity entity) {
        // Calculate direction vector from player to entity
        double dx = entity.getX() - mc.player.getX();
        double dz = entity.getZ() - mc.player.getZ();

        // Normalize the direction vector
        double length = Math.sqrt(dx * dx + dz * dz);
        dx /= length;
        dz /= length;

        // Calculate the player's view direction vector
        double playerYawRad = Math.toRadians(mc.player.getYaw());
        double viewDirX = -Math.sin(playerYawRad);
        double viewDirZ = Math.cos(playerYawRad);

        // Calculate the dot product
        double dotProduct = dx * viewDirX + dz * viewDirZ;

        // Ensure the dot product is within the valid range for acos to avoid NaN results
        dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));

        // Calculate the angle using the arccosine of the dot product
        return Math.acos(dotProduct);
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
            boxMuellerClicker.setStd(this.std.getValue());
            boxMuellerClicker.setMean(this.mean.getValue());
            boxMuellerClicker.setMinCps(this.minCps.getValue());
            boxMuellerClicker.setMaxCps(this.maxCps.getValue());
            boxMuellerClicker.setCpsUpdatePossibility(this.updatePossibility.getValue());
        } else if (clicker instanceof final BezierClicker bezierClicker) {
            bezierClicker.setBezierValue(this.cpsBezier);
            bezierClicker.setCpsUpdatePossibility(this.updatePossibility.getValue());
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

    private void hit() {
        stopBlocking(BlockState.PRE_ATTACK);

        this.mc.doAttack();

        startBlocking(BlockState.POST_ATTACK);

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
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
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
        FOV,
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
