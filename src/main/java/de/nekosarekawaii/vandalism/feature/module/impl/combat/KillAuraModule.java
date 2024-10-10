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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.RaytraceListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import de.nekosarekawaii.vandalism.feature.module.template.target.TargetGroup;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.EntityHitPoint;
import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.hitpoints.entity.IcarusBHV;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.RandomizerModeValue;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer.NoneRandomizer;
import de.nekosarekawaii.vandalism.util.IName;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KillAuraModule extends ClickerModule implements RaytraceListener, RotationListener, Render3DListener {

    private final ModeValue clickMode = new ModeValue(
            this.clickerGroup,
            "Click Mode",
            "Which click method should be used.",
            "Legit",
            "Packet"
    );

    private final BooleanValue onlyClickWhenLooking = new BooleanValue(
            this.clickerGroup,
            "Only Click When Looking",
            "Whether the player should only click when looking at the target.",
            false
    );

    private final BooleanValue hitThroughWalls = new BooleanValue(
            this.clickerGroup,
            "Hit Through Walls",
            "Whether the player should hit through walls.",
            false
    );

    private final ValueGroup targetSelectionGroup = new ValueGroup(
            this,
            "Target Selection",
            "The target selection group of the " + this.getName() + "."
    );

    private final TargetGroup targetGroup = new TargetGroup(
            this.targetSelectionGroup,
            "Targets",
            "The entities to target."
    );

    private final EnumModeValue<SelectionMode> selectionMode = new EnumModeValue<>(
            this.targetSelectionGroup,
            "Target Selection Mode",
            "The mode of the target selection.",
            SelectionMode.RANGE,
            SelectionMode.values()
    );

    private final BooleanValue ignoreCreativePlayers = new BooleanValue(
            this.targetSelectionGroup,
            "Ignore Creative Players",
            "Whether creative players should be ignored.",
            true
    );

    private final ValueGroup rangeGroup = new ValueGroup(
            this.targetSelectionGroup,
            "Range",
            "The range group of the " + this.getName() + "."
    );

    private final DoubleValue rotateRange = new DoubleValue(
            this.rangeGroup,
            "Rotation Range",
            "The range in which the " + this.getName() + " should start to rotate to the target.",
            4.5, 1.0, 6.0
    );

    private final DoubleValue clickRange = new DoubleValue(
            this.rangeGroup,
            "Click Range",
            "The range in which the " + this.getName() + " should start clicking.",
            4.0, 1.0, 6.0
    );

    private final DoubleValue attackRange = new DoubleValue(
            this.rangeGroup,
            "Attack Range",
            "The range in which the " + this.getName() + " should start attacking the target.",
            3.0, 1.0, 6.0
    );

    private final BooleanValue switchTarget = new BooleanValue(
            this.targetSelectionGroup,
            "Switch Target",
            "Whether the target should be switched.",
            false
    );

    private final ValueGroup rotationGroup = new ValueGroup(
            this,
            "Rotation",
            "The rotation group of the " + this.getName() + "."
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
            SmoothingType.NORMAL,
            SmoothingType.values()
    );

    private final FloatValue rotationSpeed = new FloatValue(
            this.rotationSpeedGroup,
            "Rotation Speed",
            "The speed of the rotation.",
            90.0f, 1.0f, 180.0f
    ).visibleCondition(() -> this.rotationSpeedType.getValue() == SmoothingType.NORMAL);

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

    private final DoubleValue minDistToBHV = new DoubleValue(
            this.randomisation,
            "Min Dist to BHV",
            "The minimal distance to the best hit vector.",
            0.2, 0.0, 1.0
    ).visibleCondition(() -> !(this.randomizerMode.getValue() instanceof NoneRandomizer));

    private final DoubleValue maxDistToBHV = new DoubleValue(
            this.randomisation,
            "Max Dist to BHV",
            "The maximum distance to the best hit vector.",
            0.5, 0.0, 1.0
    ).visibleCondition(() -> !(this.randomizerMode.getValue() instanceof NoneRandomizer));

    private final BooleanValue reuseOldPoint = new BooleanValue(
            this.randomisation,
            "Reuse Old Point",
            "Whether the old point should be reused.",
            true
    ).visibleCondition(() -> !(this.randomizerMode.getValue() instanceof NoneRandomizer));

    private final BooleanValue clientRotation = new BooleanValue(
            this.rotationGroup,
            "Client Rotation",
            "Clientside rotation.",
            false
    );

    private final BooleanValue movementFix = new BooleanValue(
            this.rotationGroup,
            "Movement Fix",
            "Whether the movement fix should be applied.",
            true
    );

    private final BooleanValue visualizeHitPoint = new BooleanValue(
            this,
            "Visualize Hit Point",
            "Whether the hit point should be visualized.",
            false
    );

    // Target
    @Getter
    private Entity target;
    private int targetIndex = 0;

    // Rotation
    public final EntityHitPoint points = new IcarusBHV();
    public Vec3d hitPoint;
    public final MSTimer rotationTimer = new MSTimer();
    private int currentDirection = 0;
    private long rotationDelay = 0L;

    public KillAuraModule() {
        super("Kill Aura", "Automatically attacks nearby enemies.", Category.COMBAT);
        this.deactivateAfterSessionDefault();
    }

    @Override
    protected void onActivate() {
        super.onActivate();
        Vandalism.getInstance().getEventSystem().subscribe(this, RaytraceEvent.ID, RotationEvent.ID, Render3DEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, RaytraceEvent.ID, RotationEvent.ID, Render3DEvent.ID);
        Vandalism.getInstance().getRotationManager().resetRotation(RotationPriority.HIGH);
        this.target = null;
        this.hitPoint = null;
        super.onDeactivate();
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (mc.player == null || mc.world == null) return;
        this.updateTarget();

        if (this.target == null) {
            Vandalism.getInstance().getRotationManager().resetRotation(RotationPriority.HIGH);
            this.hitPoint = null;
            return;
        }

//        Vec3d actualVelocity = new Vec3d(mc.player.prevX - mc.player.getX(), mc.player.prevY - mc.player.getY(), mc.player.prevZ - mc.player.getZ()).multiply(-1);
//        Vec3d diff = actualVelocity.subtract(target.prevX - target.getX(), target.prevY - target.getY(), target.prevZ - target.getZ());

        // This code generates a hitpoint on a target entity hitbox
        final Vec3d newPoint = this.points.generateHitPoint(this.target);
        if (this.hitPoint == null || !this.reuseOldPoint.getValue()) {
            this.hitPoint = newPoint;
        }

        { // Randomisation
            this.hitPoint = this.randomizerMode.getValue().randomiseRotationVec3d(this.hitPoint);

            // This code clamps the distance between the hitpoint and the new point to a maximum distance
            final double maxDistance = this.maxDistToBHV.getValue();
            if (this.hitPoint.distanceTo(newPoint) > maxDistance) {
                final Vec3d direction = this.hitPoint.subtract(newPoint).normalize();
                this.hitPoint = newPoint.add(direction.multiply(maxDistance));
            }

            final double minDistance = this.minDistToBHV.getValue();
            if (this.hitPoint.distanceTo(newPoint) < minDistance) {
                final Vec3d direction = this.hitPoint.subtract(newPoint).normalize();
                this.hitPoint = newPoint.add(direction.multiply(minDistance));
            }

            final double distance = this.hitPoint.distanceTo(newPoint);
            //ChatUtil.infoChatMessage("Distance: " + distance);
        }


//        this.hitPoint.subtract(diff.multiply(random.nextGaussian()));

        // This code clamps the hitpoint to the bounding box of the target entity and generates the rotation
        this.hitPoint = RotationUtil.clampHitpointsToBoundingBox(this.hitPoint, this.target.getBoundingBox(), 0.01);
        final PrioritizedRotation rotation = RotationUtil.rotationToVec(this.hitPoint, RotationPriority.HIGH);
        final PrioritizedRotation preRotation = Vandalism.getInstance().getRotationManager().getClientRotation();

//        if (preRotation != null) {
//            final float yawDifference = MathHelper.wrapDegrees(rotation.getYaw()) - MathHelper.wrapDegrees(preRotation.getYaw());
//            final float threshold = 5.0f;
//
//            int direction = 0;
//            if (yawDifference > threshold) {
//                direction = 1;
//            } else if (yawDifference < -threshold) {
//                direction = -1;
//            }
//
//            if (direction != 0) {
//                final boolean changedDiection = direction != this.currentDirection;
//                this.currentDirection = direction;
//
//                if (changedDiection && this.rotationDelay == 0L) {
//                    this.rotationDelay = RandomUtils.randomLong(20L, 100L);
//                    this.rotationTimer.reset();
//                    ChatUtil.infoChatMessage("Direction changed! (Rotation timer reset! " + this.rotationDelay + ")");
//                }
//            }
//        }

        // Update the current reaction time
//        updateReactionTime(rotation);


        // This code sets the rotation
        if (this.rotationDelay == 0L || this.rotationTimer.hasReached(this.rotationDelay)) {
            Vandalism.getInstance().getRotationManager().setRotation(rotation, this.movementFix.getValue(), (targetRotation, serverRotation, deltaTime, hasClientRotation) ->
                    RotationUtil.rotateMouse(targetRotation, serverRotation, this.getRotationSpeed(), deltaTime, hasClientRotation));
            this.rotationDelay = 0L;
        }

        final PrioritizedRotation currentRotation = Vandalism.getInstance().getRotationManager().getClientRotation();
        if (currentRotation != null && this.clientRotation.getValue()) {
            mc.player.setYaw(currentRotation.getYaw());
            mc.player.setPitch(currentRotation.getPitch());
        }
    }

    @Override
    public void onRaytrace(final RaytraceEvent event) {
        if (this.target != null && Vandalism.getInstance().getRotationManager().getClientRotation() != null) {
            event.range = getAttackRange();
        }
    }

    @Override
    public void onRender3D(float tickDelta, MatrixStack matrixStack) {
        if (this.visualizeHitPoint.getValue() && mc.player != null && mc.world != null && this.hitPoint != null) {
            final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
            final int color = Color.RED.getRGB();
            final float red = ((color >> 16) & 0xff) / 255f;
            final float green = ((color >> 8) & 0xff) / 255f;
            final float blue = ((color) & 0xff) / 255f;
            final float alpha = ((color >> 24) & 0xff) / 255f;
            final Vec3d vec = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().negate();
            matrixStack.push();
            matrixStack.translate(vec.x, vec.y, vec.z);

            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    hitPoint.getX() - 0.05,
                    hitPoint.getY() - 0.05,
                    hitPoint.getZ() - 0.05,
                    hitPoint.getX() + 0.05,
                    hitPoint.getY() + 0.05,
                    hitPoint.getZ() + 0.05,
                    red,
                    green,
                    blue,
                    alpha
            );

            immediate.draw();
            matrixStack.pop();
        }
    }

    public double getAttackRange() {
        return this.attackRange.getValue();
    }

    public double getClickRange() {
        return this.clickRange.getValue();
    }

    public double getRotateRange() {
        return this.rotateRange.getValue();
    }

    private void updateTarget() {
        final List<Entity> entities = new ArrayList<>();

        for (final Entity entity : this.mc.world.getEntities()) {
            if (entity == this.mc.player || entity == this.mc.player.getVehicle()) {
                continue;
            }
            if (
                    this.targetGroup.isTarget(entity) &&
                            this.mc.player.distanceTo(entity) <= getRotateRange() + 1.0 &&
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

            case SCOREBOARD_HEALTH -> entities.sort((entity1, entity2) -> {
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

    private double getAngleToPlayer(final Entity entity) {
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

    private double getHealthFromScoreboard(final Entity entity) {
        final Scoreboard scoreboard = mc.player.getScoreboard();

        for (final ScoreboardEntry entry : scoreboard.getScoreboardEntries(scoreboard.getNullableObjective("health"))) {
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

    private float getRotationSpeed() {
        if (this.rotationSpeedType.getValue() == SmoothingType.NORMAL) {
            return this.rotationSpeed.getValue();
        }

        return 360.0f; // Instant Rotations
    }

    @Override
    public void onClick() {
        if (mc.player == null || mc.world == null || this.hitPoint == null) {
            return;
        }

        final PrioritizedRotation pseudoRotation = RotationUtil.rotationToVec(this.hitPoint, RotationPriority.NORMAL);
        final HitResult hitResult = WorldUtil.raytrace(pseudoRotation, this.getClickRange());
        final double raytraceDistance = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY ? mc.player.getEyePos().distanceTo(hitResult.getPos()) : -1.0;

        if (!hitThroughWalls.getValue() && (raytraceDistance > this.getClickRange() || raytraceDistance < 0.0)) {
            return;
        }

        if (!hitThroughWalls.getValue() && onlyClickWhenLooking.getValue() && (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY)) {
            return;
        }

        if (mc.player.getEyePos().distanceTo(this.hitPoint) > this.getClickRange()) {
            return;
        }

        switch (clickMode.getValue()) {
            case "Legit" -> {
                mc.doAttack();
            }
            case "Packet" -> {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(mc.player.getActiveHand());
            }
        }

    }

    @Override
    public void onFailClick() {
        if (mc.player == null || mc.world == null || this.hitPoint == null) {
            return;
        }

        final PrioritizedRotation pseudoRotation = RotationUtil.rotationToVec(this.hitPoint, RotationPriority.NORMAL);
        final HitResult hitResult = WorldUtil.raytrace(pseudoRotation, this.getClickRange());
        final double raytraceDistance = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY ? mc.player.getEyePos().distanceTo(hitResult.getPos()) : -1.0;

        if (!hitThroughWalls.getValue() && (raytraceDistance > this.getClickRange() || raytraceDistance < 0.0)) {
            return;
        }

        if (!hitThroughWalls.getValue() && onlyClickWhenLooking.getValue() && (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY)) {
            return;
        }

        if (mc.player.getEyePos().distanceTo(this.hitPoint) > this.getClickRange()) {
            return;
        }

        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private enum SelectionMode implements IName {

        RANGE,
        HEALTH,
        FOV,
        ARMOR,
        SCOREBOARD_HEALTH;

        private final String name;

        SelectionMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private enum SmoothingType implements IName {

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

}
