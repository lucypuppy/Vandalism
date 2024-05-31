package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.StepListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderFloatValue;

@ModuleInfo(name = "Step", description = "Makes you step up blocks.", category = FeatureCategory.MOVEMENT)
public class StepModule extends Module implements StepListener {

    private final Value<Float> stepHeight = new SliderFloatValue(
            "Step Height",
            "Allows you to customize the step height.",
            this,
            10.0f,
            0.7f,
            10.0f
    );

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
