package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;

public class BoatFlightModule extends AbstractModule implements TickGameListener {

    public BoatFlightModule() {
        super("Boat Flight", "Allows you to fly with a boat.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || !mc.player.hasVehicle()) return;

        float motionY = 0;
        if (mc.options.jumpKey.isPressed())
            motionY += 0.5f;
        else if (mc.options.sprintKey.isPressed())
            motionY -= 0.5f;

        mc.player.getVehicle().setVelocity(mc.player.getVehicle().getVelocity().getX(), motionY, mc.player.getVehicle().getVelocity().getY());

        if (mc.options.forwardKey.isPressed())
            MovementUtil.setSpeed(mc.player.getVehicle(), 1f);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }
}
