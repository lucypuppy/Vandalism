package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.FloatValue;
import de.vandalismdevelopment.vandalism.util.minecraft.TimerHack;

public class TimerModule extends AbstractModule implements TickListener {

    private final Value<Float> timerModifier = new FloatValue(
            this,
            "Timer Modifier",
            "Allows you to customize the timer speed.",
            2.f,
            .1f,
            20.f
    );

    private final Value<Boolean> screen = new BooleanValue(
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
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        TimerHack.reset();
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

}
