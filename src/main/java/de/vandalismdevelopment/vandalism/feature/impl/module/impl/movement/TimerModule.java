package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.TimerUtil;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderFloatValue;

public class TimerModule extends Module implements TickListener {

    public TimerModule() {
        super(
                "Timer",
                "Modifies the timer speed.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    private final Value<Float> timerModifier = new SliderFloatValue(
            "Timer Modifier",
            "Allows you to customize the timer speed.",
            this,
            2.f,
            .1f,
            20.f
    );

    private final Value<Boolean> screen = new BooleanValue(
            "Screen",
            "Allows you to use the timer in screen(s).",
            this,
            false
    );

    @Override
    public void onTick() {
        if (!this.screen.getValue() && currentScreen() != null) {
            TimerUtil.setSpeed(1.f);
            return;
        }

        TimerUtil.setSpeed(this.timerModifier.getValue());
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        TimerUtil.setSpeed(1f);
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

}
