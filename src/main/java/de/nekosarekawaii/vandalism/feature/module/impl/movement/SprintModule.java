package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.SprintListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;

public class SprintModule extends AbstractModule implements SprintListener {

    public SprintModule() {
        super("Sprint", "Automatically let's you sprint!", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(SprintEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(SprintEvent.ID, this);
    }

    @Override
    public void onSprint(final SprintEvent event) {
      /*  final RotationListener rotation = Vandalism.getInstance().getRotationListener();
        final boolean useSpoofedRotation = rotation.getRotation() != null;
        if (Math.abs(MathHelper.wrapDegrees((useSpoofedRotation ? rotation.getRotation().getYaw() : mc.player.getYaw()) - MovementUtil.getInputAngle(mc.player.getYaw()))) > 45D) {
            event.sprinting = false;
            event.force = true;
            return;
        }*/
        event.sprinting = MovementUtil.isMoving();
    }

}
