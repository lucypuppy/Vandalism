package de.vandalismdevelopment.vandalism.util.rotation;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.RandomUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.util.render.RenderUtils;
import de.vandalismdevelopment.vandalism.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

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
    public void onRender2DInGame(final DrawContext context, final float delta) {
        this.lastRotation = new Rotation(player().lastYaw, player().lastPitch);

        if (this.targetRotation != null) {
            this.rotation = this.applyGCDFix(rotationDistribution(this.targetRotation, this.lastRotation), delta);
            return;
        }

        if (this.rotation == null)
            return;

        final float
                yaw = MathHelper.wrapDegrees(player().yaw),
                pitch = player().pitch,
                yawDiff = Math.abs(yaw - this.rotation.getYaw()),
                pitchDiff = Math.abs(pitch - this.rotation.getPitch());

        if (yawDiff <= 0.5 && pitchDiff <= 0.5) {
            this.rotation = null;
            return;
        }

        if (!Vandalism.getInstance().getConfigManager().getMainConfig().rotationCategory.rotateBack.getValue()) {
            this.rotation = this.applyGCDFix(new Rotation(yaw, pitch), delta);
            return;
        }

        this.rotation = this.applyGCDFix(rotationDistribution(new Rotation(yaw, pitch), this.lastRotation), delta);
    }

    public void setRotation(final Rotation rotation, final Vec2f rotateSpeedMinMax, final RotationPriority priority) {
        if (this.currentPriority == null || priority.getPriority() >= this.currentPriority.getPriority()) {
            this.targetRotation = rotation;
            this.rotateSpeedMinMax = rotateSpeedMinMax;
            this.currentPriority = priority;
        }
    }

    public void resetRotation() {
        this.targetRotation = null;
    }

    // Bruteforce GCD Method best for hvh tested it really often.
    private Rotation applyGCDFix(final Rotation rotation, final float partialTicks) {
        final double f = mc().options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
        final double g = f * f * f;
        final double gcd = g * 8.0;
        final boolean disallowGCD = mc().options.getPerspective().isFirstPerson() && mc().player.isUsingSpyglass();

        //Calculate needed iterations for the best gcd.
        final double iterationsNeeded = (RenderUtils.getFps() / 20.0) * partialTicks;
        final int iterations = MathHelper.floor(iterationsNeeded + this.partialIterations);
        this.partialIterations += iterationsNeeded - iterations;

        final RotationGCD gcdMode = Vandalism.getInstance().getConfigManager().getMainConfig().rotationCategory.gcdMode.getValue();
        final Rotation fixedRotation = gcdMode.getLambda().apply(rotation, this.lastRotation, disallowGCD ? g : gcd, iterations);

        //Fix for a small check I coded some time in the past idk how it worked but this fixed it.
        fixedRotation.setYaw(this.lastRotation.getYaw() + MathHelper.wrapDegrees(fixedRotation.getYaw() - this.lastRotation.getYaw()));
        fixedRotation.setPitch(MathHelper.clamp(fixedRotation.getPitch(), -90.0F, 90.0F));

        return fixedRotation;
    }

    public Rotation rotationDistribution(final Rotation rotation, final Rotation lastRotation) {
        if (rotateSpeedMinMax.x > 0 && rotateSpeedMinMax.y > 0) { //TODO: Code a better calculation for the rotate speed.
            rotateSpeed = RandomUtils.randomFloat(rotateSpeedMinMax.x, rotateSpeedMinMax.y);
        }

        if (rotateSpeed > 0) {
            final float
                    lastYaw = lastRotation.getYaw(),
                    lastPitch = lastRotation.getPitch(),
                    deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw),
                    deltaPitch = rotation.getPitch() - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);

            if (distance > 0) {
                final double
                        distributionYaw = Math.abs(deltaYaw / distance),
                        distributionPitch = Math.abs(deltaPitch / distance),
                        maxYaw = rotateSpeed * distributionYaw,
                        maxPitch = rotateSpeed * distributionPitch;

                final float
                        moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw),
                        movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

                return new Rotation(lastYaw + moveYaw, lastPitch + movePitch);
            }
        }

        return rotation;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

}
