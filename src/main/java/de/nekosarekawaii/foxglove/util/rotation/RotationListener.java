package de.nekosarekawaii.foxglove.util.rotation;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;

public class RotationListener implements PacketListener {

    private Rotation rotation, lastRotation;

    public RotationListener() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
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
            final Rotation fixedRotation = bruteforceGCD(this.rotation); // Fix the GCD Before we send it to the server.

            packet.yaw = fixedRotation.getYaw();
            packet.pitch = fixedRotation.getPitch();
        }
    }

    public void setRotation(final Rotation rotation) {
        if (rotation.getPriority().getPriority() >= this.rotation.getPriority().getPriority()) {
            this.rotation = rotation;
        }
    }

    // Bruteforce GCD Method best for hvh tested it really often.
    private Rotation bruteforceGCD(final Rotation rotation) {
        float yaw = rotation.getYaw();
        float pitch = rotation.getPitch();

        final float mouseSensitivity = (float) ((108 / 200f) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;

        for (int i = 0; i <= 4; i++) {
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
