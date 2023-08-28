package de.nekosarekawaii.foxglove.util.rotation;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.util.render.RenderUtils;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;

public class RotationListener implements PacketListener, RenderListener {

    private Rotation fixedRotation, rotation, lastRotation;
    private double partialIterations;

    public RotationListener() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onPacketRead(PacketEvent event) {
        if (event.packet instanceof final PlayerPositionLookS2CPacket packet) { // Set the lastRotation to the rotation we send to the server.
            this.lastRotation = new Rotation(packet.getYaw(), packet.getPitch(), RotationPriority.NORMAL);
        }
    }

    @Override
    public void onPacketWrite(PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet && this.rotation != null && this.lastRotation != null &&
                (this.rotation.getYaw() != this.lastRotation.getYaw() || this.rotation.getPitch() != this.lastRotation.getPitch())) {
            packet.yaw = fixedRotation.getYaw();
            packet.pitch = fixedRotation.getPitch();
        }
    }

    @Override
    public void onRender2DInGame(DrawContext context, float delta, Window window) {
        if (this.rotation != null) {
            this.fixedRotation = bruteforceGCD(this.rotation, delta);
        }
    }

    public void setRotation(final Rotation rotation) {
        if (rotation.getPriority().getPriority() >= this.rotation.getPriority().getPriority()) {
            this.rotation = rotation;
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

}
