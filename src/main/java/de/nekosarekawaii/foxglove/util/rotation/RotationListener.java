package de.nekosarekawaii.foxglove.util.rotation;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import de.nekosarekawaii.foxglove.util.RaytraceUtil;
import de.nekosarekawaii.foxglove.util.render.RenderUtils;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RotationListener implements PacketListener, RenderListener, MinecraftWrapper {

    private Rotation rotation, targetRotation, lastRotation;
    private double partialIterations;
    private float rotateSpeed;
    private Vec2f rotateSpeedMinMax;
    private RotationPriority currentPriority;

    public RotationListener() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) {
            if (this.rotation != null) {
                packet.yaw = this.rotation.getYaw();
                packet.pitch = this.rotation.getPitch();
                packet.changeLook = true;
            }
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        this.lastRotation = new Rotation(player().lastYaw, player().lastPitch);

        if (this.targetRotation != null) {
            this.rotation = this.bruteforceGCD(
                    rotationDistribution(this.targetRotation, this.lastRotation),
                    delta
            );
            return;
        }

        if (this.rotation == null)
            return;

        final float
                yaw = wrapDegreesFixed(player().yaw),
                pitch = player().pitch,
                yawDiff = Math.abs(yaw - this.rotation.getYaw()),
                pitchDiff = Math.abs(pitch - this.rotation.getPitch());

        if (yawDiff <= 0.5 && pitchDiff <= 0.5) {
            this.rotation = null;
            return;
        }

        this.rotation = this.bruteforceGCD(
                rotationDistribution(new Rotation(yaw, pitch), this.lastRotation),
                delta
        );
    }

    public void setRotation(final @Nullable Rotation rotation, final Vec2f rotateSpeedMinMax, final RotationPriority priority) {
        if (this.currentPriority == null || priority.getPriority() >= this.currentPriority.getPriority()) {
            this.targetRotation = rotation;
            this.rotateSpeedMinMax = rotateSpeedMinMax;
            this.currentPriority = priority;
        }
    }

    public void setRotation(final @NotNull Vec3d to, final Vec2f rotateSpeedMinMax, final RotationPriority priority) {
        if (player() == null) return;

        this.setRotation(vecToRotation(to, player().getEyePos()), rotateSpeedMinMax, priority);
    }

    public void setRotation(final Entity entity, final boolean bestHitVec, final double range, final Vec2f rotateSpeedMinMax, final RotationPriority priority) {
        if (player() == null) return;

        final Vec3d eyePos = player().getEyePos();
        final Box box = entity.getBoundingBox();
        final Vec3d getEntityVector = bestHitVec ? getNearestPoint(entity) : new Vec3d(entity.getX(), entity.getY(), entity.getZ());

        Rotation normalRotations = vecToRotation(getEntityVector, eyePos);
        if (RaytraceUtil.rayTraceBlock(normalRotations.getVector(), range))
            setRotation(normalRotations, rotateSpeedMinMax, priority);

        Vec3d currentVector = null;
        for (double x = 0.00D; x < 1.00D; x += 0.1D) {
            for (double y = 0.00D; y < 1.00D; y += 0.1D) {
                for (double z = 0.00D; z < 1.00D; z += 0.1D) {
                    final Vec3d vector = new Vec3d(
                            box.minX + (box.maxX - box.minX) * x,
                            box.minY + (box.maxY - box.minY) * y,
                            box.minZ + (box.maxZ - box.minZ) * z);

                    if (eyePos.distanceTo(vector) > range)
                        continue;

                    final Rotation parsedRotation = vecToRotation(vector, eyePos);

                    if (!RaytraceUtil.rayTraceBlock(parsedRotation.getVector(), range))
                        continue;

                    if (!bestHitVec) {
                        setRotation(parsedRotation, rotateSpeedMinMax, priority);
                    } else if (currentVector == null || eyePos.distanceTo(vector) <= eyePos.distanceTo(currentVector)) {
                        currentVector = vector;
                        normalRotations = parsedRotation;
                    }
                }
            }
        }

        setRotation(normalRotations, rotateSpeedMinMax, priority);
    }

    public Rotation vecToRotation(final Vec3d to, final Vec3d eyePos) {
        final Vec3d diff = to.subtract(eyePos);
        final double hypot = Math.hypot(diff.getX(), diff.getZ());
        final float
                yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0F / Math.PI)) - 90.0F,
                pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0F / Math.PI));

        return new Rotation(yaw, pitch);
    }

    public Vec3d getNearestPoint(final Entity entity) {
        final Box boundingBox = entity.getBoundingBox();

        final double nearestX = Math.max(boundingBox.minX, Math.min(entity.getX(), boundingBox.maxX));
        final double nearestZ = Math.max(boundingBox.minZ, Math.min(entity.getZ(), boundingBox.maxZ));

        //Todo recode this.
        final double nearestY = entity.getY() + Math.max(0,
                Math.min(player().getY() - entity.getY() + player().getEyeHeight(player().getPose()),
                        (boundingBox.maxY - boundingBox.minY) * 0.9)
        );

        return new Vec3d(nearestX, nearestY, nearestZ);
    }

    // Bruteforce GCD Method best for hvh tested it really often.
    private Rotation bruteforceGCD(final Rotation rotation, final float partialTicks) {
        float yaw = rotation.getYaw(), pitch = rotation.getPitch();

        final double multiplier = getMouseMultiplier();

        double iterationsNeeded = RenderUtils.getFps() / 20.0;
        iterationsNeeded *= partialTicks;
        final int iterations = MathHelper.floor(iterationsNeeded + this.partialIterations);
        this.partialIterations += iterationsNeeded - iterations;

        for (int i = 0; i <= iterations; i++) {
            yaw = this.lastRotation.getYaw() + (float) (
                    Math.round((yaw - this.lastRotation.getYaw()) / multiplier) * multiplier
            );

            pitch = this.lastRotation.getPitch() + (float) (
                    Math.round((pitch - this.lastRotation.getPitch()) / multiplier) * multiplier
            );
        }

        return new Rotation(wrapDegreesFixed(yaw), pitch);
    }

    private double getMouseMultiplier() {
        final double f = options().getMouseSensitivity().getValue() * 0.6F + 0.2F;
        return f * f * f * 8.0;
    }

    // This is a fix method for rounding errors in the yaw detectable with a simple check.
    private float wrapDegreesFixed(final float yaw) {
        return this.lastRotation.getYaw() + MathHelper.wrapDegrees(yaw - this.lastRotation.getYaw());
    }

    public Rotation rotationDistribution(final Rotation rotation, final Rotation lastRotation) {
        if (rotateSpeedMinMax.x > 0 && rotateSpeedMinMax.y > 0) { //Todo code a better calculation for the rotate speed.
            rotateSpeed = RandomUtils.nextFloat(rotateSpeedMinMax.x, rotateSpeedMinMax.y);
        }

        if (rotateSpeed > 0) {
            final float
                    lastYaw = lastRotation.getYaw(),
                    lastPitch = lastRotation.getPitch(),
                    deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw),
                    deltaPitch = rotation.getPitch() - lastPitch;

            final double
                    distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch),
                    distributionYaw = Math.abs(deltaYaw / distance),
                    distributionPitch = Math.abs(deltaPitch / distance),
                    maxYaw = rotateSpeed * distributionYaw,
                    maxPitch = rotateSpeed * distributionPitch;

            final float
                    moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw),
                    movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            return new Rotation(lastYaw + moveYaw, lastPitch + movePitch);
        }
        return rotation;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

}
