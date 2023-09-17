package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.StepListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.number.slider.SliderFloatValue;

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
