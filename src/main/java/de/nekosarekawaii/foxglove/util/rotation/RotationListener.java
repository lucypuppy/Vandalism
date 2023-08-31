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
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RotationListener implements PacketListener, RenderListener {

    private final MinecraftClient mc = MinecraftClient.getInstance();

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
        var player = mc.player;
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

        if (yawDiff <= 0.5 && pitchDiff <= 0.5) {
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

    public void setRotation(final @NotNull Vec3d to, final float rotateSpeed, final RotationPriority priority) {
        if (mc.player == null)
            return;

        final var eyePos = mc.player.getEyePos();
        final var diff = to.subtract(eyePos);
        final var hypot = Math.hypot(diff.getX(), diff.getZ());
        final var yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0F / Math.PI)) - 90.0F;
        final var pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0F / Math.PI));

        setRotation(new Rotation(yaw, pitch), rotateSpeed, priority);
    }

    // Bruteforce GCD Method best for hvh tested it really often.
    private Rotation bruteforceGCD(final Rotation rotation, final float partialTicks) {
        var yaw = rotation.getYaw();
        var pitch = rotation.getPitch();
        final double multiplier = getMouseMultiplier();

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

    private double getMouseMultiplier() {
        final double f = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
        return f * f * f * 8.0;
    }

    // This is a fix method for rounding errors in the yaw detectable with a simple check.
    private float wrapDegreesFixed(final float yaw) {
        return this.lastRotation.getYaw() + MathHelper.wrapDegrees(yaw - this.lastRotation.getYaw());
    }

    public Rotation rotationDistribution(Rotation rotation, Rotation lastRotation, float speed) {
        if (speed > 0) {
            final var lastYaw = lastRotation.getYaw();
            final var lastPitch = lastRotation.getPitch();

            final var deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw);
            final var deltaPitch = rotation.getPitch() - lastPitch;

            final var distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final var distributionYaw = Math.abs(deltaYaw / distance);
            final var distributionPitch = Math.abs(deltaPitch / distance);

            final var maxYaw = speed * distributionYaw;
            final var maxPitch = speed * distributionPitch;

            final var moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final var movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            return new Rotation(lastYaw + moveYaw, lastPitch + movePitch);
        }

        return rotation;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

}
