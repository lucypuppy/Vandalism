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
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class RotationListener implements PacketListener, RenderListener {

    private Rotation fixedRotation, rotation, lastRotation;
    private double partialIterations, rotateSpeed;
    private RotationPriority currentPriority;

    public RotationListener() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onPacketRead(PacketEvent event) {
        if (event.packet instanceof final PlayerPositionLookS2CPacket packet) { // Set the lastRotation to the rotation we send to the server.
            this.lastRotation = new Rotation(packet.getYaw(), packet.getPitch());
        }
    }

    @Override
    public void onPacketWrite(PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet && this.fixedRotation != null) {
            packet.yaw = fixedRotation.getYaw();
            packet.pitch = fixedRotation.getPitch();
        }
    }

    @Override
    public void onRender2DInGame(DrawContext context, float delta, Window window) {
        if (this.rotation != null) {
            this.fixedRotation = bruteforceGCD(rotationSmoothing(this.rotation, this.lastRotation, rotateSpeed), delta);
            return;
        }

        final var player = MinecraftClient.getInstance().player;
        if (this.fixedRotation == null || player == null)
            return;

        final var yaw = player.yaw;
        final var pitch = player.pitch;
        final var yawDiff = Math.abs(yaw - this.fixedRotation.getYaw());
        final var pitchDiff = Math.abs(pitch - this.fixedRotation.getYaw());

        if (yawDiff > 0.1 || pitchDiff > 0.1) {
            this.fixedRotation = bruteforceGCD(rotationSmoothing(new Rotation(yaw, pitch), this.lastRotation, rotateSpeed), delta);
            return;
        }


        this.fixedRotation = null;
    }

    public void setRotation(final @Nullable Rotation rotation, final double rotateSpeed, final RotationPriority priority) {
        if (currentPriority == null || priority.getPriority() >= currentPriority.getPriority()) {
            this.rotation = rotation;
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
            yaw = lastRotation.getYaw() + (float) (Math.round((yaw - lastRotation.getYaw()) / multiplier) * multiplier);
            pitch = lastRotation.getPitch() + (float) (Math.round((pitch - lastRotation.getPitch()) / multiplier) * multiplier);
        }

        rotation.setYaw(wrapDegreesFixed(yaw));
        rotation.setPitch(pitch);
        return rotation;
    }

    // This is a fix method for rounding errors in the yaw detectable with a simple check.
    private float wrapDegreesFixed(final float yaw) {
        return lastRotation.getYaw() + MathHelper.wrapDegrees(yaw - lastRotation.getYaw());
    }

    public Rotation rotationSmoothing(final Rotation rotation, final Rotation lastRotation, final double speed) {
        if (speed > 0) {
            var yaw = rotation.getYaw();
            var pitch = rotation.getPitch();
            final var lastYaw = lastRotation.getYaw();
            final var lastPitch = lastRotation.getPitch();

            final double deltaYaw = wrapDegreesFixed(yaw - lastYaw);
            final double deltaPitch = pitch - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final double distributionYaw = Math.abs(deltaYaw / distance);
            final double distributionPitch = Math.abs(deltaPitch / distance);

            final double maxYaw = speed * distributionYaw;
            final double maxPitch = speed * distributionPitch;

            final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            rotation.setYaw(lastYaw + moveYaw);
            rotation.setPitch(lastPitch + movePitch);
        }

        return rotation;
    }

    public Rotation getFixedRotation() {
        return fixedRotation;
    }

}
