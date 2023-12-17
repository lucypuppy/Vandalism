package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;
import de.nekosarekawaii.vandalism.util.minecraft.TimerHack;
import net.minecraft.util.math.Vec3d;

public class CubeCraftModuleMode extends ModuleMulti<FlightModule> implements MotionListener {

    private int waitTicks, moveTicks;
    private double lastPosY;
    private boolean canLongJump;
    private double moveSpeed;

    public CubeCraftModuleMode(final FlightModule parent) {
        super("Cubecraft", parent);
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(MotionListener.MotionEvent.ID, this);
        if (this.mc.getNetworkHandler() != null) {
            MovementUtil.clip(3.5, 0);
            MovementUtil.setSpeed(0.01);
            this.moveSpeed = MovementUtil.getBaseSpeed() * 3.5;
            this.lastPosY = this.mc.player.getY();
        }
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(MotionListener.MotionEvent.ID, this);
        this.waitTicks = 0;
        this.moveTicks = 0;
        this.canLongJump = false;
        TimerHack.reset();
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
                    this.mc.player.setVelocity(this.mc.player.getVelocity().add(0, 0.01, 0));
                    this.moveTicks = 5;
                    TimerHack.setSpeed(0.85f);
                    return;
                } else {
                    TimerHack.setSpeed(1.7f);
                }


                if (Math.abs(this.mc.player.getY() - this.lastPosY) > 1) {
                    MovementUtil.setSpeed(-0.01);
                    this.mc.player.fallDistance = 0;
                    this.mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.mc.player.setPos(this.mc.player.getX(), this.lastPosY, this.mc.player.getZ());
                    if (this.mc.options.jumpKey.isPressed()) {
                        this.mc.player.setPos(this.mc.player.getX(), this.mc.player.getY() + 0.8 + Math.random() * 0.04, this.mc.player.getZ());
                    } else if (this.mc.options.sneakKey.isPressed()) {
                        this.mc.player.setPos(this.mc.player.getX(), this.mc.player.getY() - 0.8 + Math.random() * 0.04, this.mc.player.getZ());
                    }
                    this.lastPosY = this.mc.player.getY();
                    return;
                }
                if (this.mc.options.jumpKey.isPressed() || this.mc.options.sneakKey.isPressed()) {
                    MovementUtil.setSpeed(0);
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
            if (this.mc.player.hurtTime >= 9) {
                MovementUtil.setSpeed(8);
            }
        }
    }
}
