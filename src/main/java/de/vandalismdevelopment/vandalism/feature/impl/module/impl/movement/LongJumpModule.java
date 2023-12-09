package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.MovementUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.TimerUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class LongJumpModule extends Module implements MovementListener {

    private int waitTicks, moveTicks;
    private double lastPosY;
    private boolean canLongJump;
    private double moveSpeed;

    public LongJumpModule() {
        super("Long Jump", "Let's you jump further from normal.", FeatureCategory.MOVEMENT, true, false);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(MotionEvent.ID, this);
        if (this.networkHandler() != null) {
            MovementUtil.clip(3.5, 0);
            MovementUtil.setSpeed(0.01);
            this.lastPosY = player().getY();
            this.moveSpeed = MovementUtil.getBaseSpeed() * 4;
        }
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(MotionEvent.ID, this);
        this.waitTicks = 0;
        this.moveTicks = 0;
        this.canLongJump = false;
        TimerUtil.setSpeed(1);

    }

    @Override
    public void onPostMotion(final MotionEvent event) {
        if (this.player().hurtTime > 0) {
            this.waitTicks++;
            if (this.waitTicks >= 4) {
                this.canLongJump = true;
            }
        }
        if (this.canLongJump) {
            if (this.player().isOnGround()) {
                this.player().setVelocity(this.player().getVelocity().add(0, 1, 0));
            } else {
                final Vec3d moveVelocity = this.player().getVelocity();

                if (this.player().fallDistance > 0.2f && this.moveTicks <= 2) {
                    this.player().setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.player().setVelocity(player().getVelocity().add(0, 0.01, 0));
                    this.moveTicks = 5;
                    TimerUtil.setSpeed(0.8f);
                    return;
                } else {
                    TimerUtil.setSpeed(1.3f);
                }

                if (Math.abs(this.player().getY() - this.lastPosY) > 1) {
                    MovementUtil.setSpeed(-0.01);
                    this.player().fallDistance = 0;
                    this.player().setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.player().setPos(this.player().getX(), this.lastPosY, this.player().getZ());
                    this.lastPosY = this.player().getY();
                    return;
                }
                final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, 0.0026f);
                if (MovementUtil.getSpeed() <= 0.27) {
                    this.moveSpeed = 0.27f;
                    return;
                }
                if (this.player().hurtTime == 0) {
                    this.moveSpeed -= 0.1f;
                }
                final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, 40);
                this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
                this.moveTicks--;
            }
        }
    }
}
