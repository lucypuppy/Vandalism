package de.nekosarekawaii.foxglove.util.rotation;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import de.nekosarekawaii.foxglove.util.render.RenderUtils;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RotationListener implements PacketListener, RenderListener, MinecraftWrapper {

    private Rotation rotation, targetRotation, lastRotation;
    private double partialIterations;
    private float rotateSpeed;
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
                    rotationDistribution(this.targetRotation, this.lastRotation, this.rotateSpeed),
                    delta
            );
            return;
        }
        if (this.rotation == null) return;
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
                rotationDistribution(new Rotation(yaw, pitch), this.lastRotation,
                        this.rotateSpeed
                ),
                delta
        );
    }

    public void setRotation(final @Nullable Rotation rotation, final float rotateSpeed, final RotationPriority priority) {
        if (this.currentPriority == null || priority.getPriority() >= this.currentPriority.getPriority()) {
            this.targetRotation = rotation;
            this.rotateSpeed = rotateSpeed;
            this.currentPriority = priority;
        }
    }

    public void setRotation(final @NotNull Vec3d to, final float rotateSpeed, final RotationPriority priority) {
        if (player() == null) return;
        final Vec3d eyePos = player().getEyePos(), diff = to.subtract(eyePos);
        final double hypot = Math.hypot(diff.getX(), diff.getZ());
        final float
                yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0F / Math.PI)) - 90.0F,
                pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0F / Math.PI));
        this.setRotation(new Rotation(yaw, pitch), rotateSpeed, priority);
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

    public Rotation rotationDistribution(final Rotation rotation, final Rotation lastRotation, final float speed) {
        if (speed > 0) {
            final float
                    lastYaw = lastRotation.getYaw(),
                    lastPitch = lastRotation.getPitch(),
                    deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw),
                    deltaPitch = rotation.getPitch() - lastPitch;
            final double
                    distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch),
                    distributionYaw = Math.abs(deltaYaw / distance),
                    distributionPitch = Math.abs(deltaPitch / distance),
                    maxYaw = speed * distributionYaw,
                    maxPitch = speed * distributionPitch;
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
