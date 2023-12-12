package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.util.minecraft.MovementUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.TimerHack;
import net.minecraft.util.math.Vec3d;

public class LongJumpModule extends AbstractModule implements MovementListener {

    private int waitTicks, moveTicks;
    private double lastPosY;
    private boolean canLongJump;
    private double moveSpeed;

    public LongJumpModule() {
        super("Long Jump", "Let's you jump further from normal.", Category.MOVEMENT);

        setExperimental(true);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(MotionEvent.ID, this);
        if (this.mc.getNetworkHandler() != null) {
            MovementUtil.clip(3.5, 0);
            MovementUtil.setSpeed(0.01);
            this.lastPosY = mc.player.getY();
            this.moveSpeed = MovementUtil.getBaseSpeed() * 4;
        }
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(MotionEvent.ID, this);
        this.waitTicks = 0;
        this.moveTicks = 0;
        this.canLongJump = false;
        TimerHack.setSpeed(1);

    }

    @Override
    public void onPostMotion(final MotionEvent event) {
        if (this.mc.player.hurtTime > 0) {
            this.waitTicks++;
            if (this.waitTicks >= 4) {
                this.canLongJump = true;
            }
        }
        if (this.canLongJump) {
            if (this.mc.player.isOnGround()) {
                this.mc.player.setVelocity(this.mc.player.getVelocity().add(0, 1, 0));
            } else {
                final Vec3d moveVelocity = this.mc.player.getVelocity();

                if (this.mc.player.fallDistance > 0.2f && this.moveTicks <= 2) {
                    this.mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.mc.player.setVelocity(mc.player.getVelocity().add(0, 0.01, 0));
                    this.moveTicks = 5;
                    TimerHack.setSpeed(0.8f);
                    return;
                } else {
                    TimerHack.setSpeed(1.3f);
                }

                if (Math.abs(this.mc.player.getY() - this.lastPosY) > 1) {
                    MovementUtil.setSpeed(-0.01);
                    this.mc.player.fallDistance = 0;
                    this.mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.mc.player.setPos(this.mc.player.getX(), this.lastPosY, this.mc.player.getZ());
                    this.lastPosY = this.mc.player.getY();
                    return;
                }
                final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, 0.0026f);
                if (MovementUtil.getSpeed() <= 0.27) {
                    this.moveSpeed = 0.27f;
                    return;
                }
                if (this.mc.player.hurtTime == 0) {
                    this.moveSpeed -= 0.1f;
                }
                final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, 40);
                this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
                this.moveTicks--;
            }
        }
    }
}
