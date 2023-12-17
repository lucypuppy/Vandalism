package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.TimerHack;

public class TimerModule extends AbstractModule implements TickGameListener {

    private final Value<Float> timerModifier = new FloatValue(
            this,
            "Timer Modifier",
            "Allows you to customize the timer speed.",
            2.f,
            .1f,
            20.f
    );

    private final BooleanValue screen = new BooleanValue(
            this,
            "Screen",
            "Allows you to use the timer in screen(s).",
            false
    );


    public TimerModule() {
        super("Timer", "Modifies the timer speed.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!this.screen.getValue() && mc.currentScreen != null) {
            TimerHack.reset();
        } else {
            TimerHack.setSpeed(this.timerModifier.getValue());
        }
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        TimerHack.reset();
        Vandalism.getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

}
