package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.StepListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderFloatValue;

public class StepModule extends Module implements StepListener {

    private final Value<Float> stepHeight = new SliderFloatValue(
            "Step Height",
            "Allows you to customize the step height.",
            this,
            10.0f,
            0.7f,
            10.0f
    );

    public StepModule() {
        super(
                "Step",
                "Changes your step height or step speed.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(StepEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(StepEvent.ID, this);
    }

    @Override
    public void onStep(final StepEvent event) {
        event.stepHeight = this.stepHeight.getValue();
    }

}
