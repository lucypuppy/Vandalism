package de.nekosarekawaii.foxglove.util.rotation;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.util.render.RenderUtils;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class RotationListener implements PacketListener, RenderListener {

    private Rotation rotation, targetRotation, lastRotation;
    private double partialIterations;
    private float rotateSpeed;
    private RotationPriority currentPriority;

    public RotationListener() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onPacketWrite(PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) {
            if (this.rotation != null) {
                packet.yaw = this.rotation.getYaw();
                packet.pitch = this.rotation.getPitch();
                packet.changeLook = true;
            }
        }
    }

    @Override
    public void onRender2DInGame(DrawContext context, float delta, Window window) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;

        this.lastRotation = new Rotation(player.lastYaw, player.lastPitch);

        if (this.targetRotation != null) {
            this.rotation = bruteforceGCD(rotationDistribution(this.targetRotation, this.lastRotation,
                    this.rotateSpeed), delta);
            return;
        }

        if (this.rotation == null)
            return;

        final var yaw = wrapDegreesFixed(player.yaw);
        final var pitch = player.pitch;
        final var yawDiff = Math.abs(yaw - this.rotation.getYaw());
        final var pitchDiff = Math.abs(pitch - this.rotation.getPitch());

        if (yawDiff <= 1 && pitchDiff <= 1) {
            this.rotation = null;
            return;
        }

        this.rotation = bruteforceGCD(rotationDistribution(new Rotation(yaw, pitch), this.lastRotation,
                this.rotateSpeed), delta);
    }

    public void setRotation(final @Nullable Rotation rotation, final float rotateSpeed, final RotationPriority priority) {
        if (this.currentPriority == null || priority.getPriority() >= this.currentPriority.getPriority()) {
            this.targetRotation = rotation;
            this.rotateSpeed = rotateSpeed;
            this.currentPriority = priority;
        }
    }

    // Bruteforce GCD Method best for hvh tested it really often.
    private Rotation bruteforceGCD(final Rotation rotation, final float partialTicks) {
        var yaw = rotation.getYaw();
        var pitch = rotation.getPitch();

        // TODO add a system to get the mouse multiplier for every minecraft version without rounding errors.
        final var mouseSensitivity = (float) ((108 / 200f) * 0.6F + 0.2F);
        final var multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;

        var iterationsNeeded = RenderUtils.getFps() / 20.0;
        iterationsNeeded *= partialTicks;
        final var iterations = MathHelper.floor(iterationsNeeded + partialIterations);
        partialIterations += iterationsNeeded - iterations;

        for (int i = 0; i <= iterations; i++) {
            yaw = this.lastRotation.getYaw() + (float) (Math.round((yaw - this.lastRotation.getYaw()) / multiplier) * multiplier);
            pitch = this.lastRotation.getPitch() + (float) (Math.round((pitch - this.lastRotation.getPitch()) / multiplier) * multiplier);
        }

        return new Rotation(wrapDegreesFixed(yaw), pitch);
    }

    // This is a fix method for rounding errors in the yaw detectable with a simple check.
    private float wrapDegreesFixed(final float yaw) {
        return this.lastRotation.getYaw() + MathHelper.wrapDegrees(yaw - this.lastRotation.getYaw());
    }

    public Rotation rotationDistribution(Rotation rotation, Rotation lastRotation, float speed) {
        if (speed > 0) {
            final var lastYaw = lastRotation.getYaw();
            final var lastPitch = lastRotation.getPitch();

            final double deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw);
            final double deltaPitch = rotation.getPitch() - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final double distributionYaw = Math.abs(deltaYaw / distance);
            final double distributionPitch = Math.abs(deltaPitch / distance);

            final double maxYaw = speed * distributionYaw;
            final double maxPitch = speed * distributionPitch;

            final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            return new Rotation(lastYaw + moveYaw, lastPitch + movePitch);
        }

        return rotation;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

}
