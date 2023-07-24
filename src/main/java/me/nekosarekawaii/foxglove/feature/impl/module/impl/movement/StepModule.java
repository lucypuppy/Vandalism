package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.impl.StepListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;

@ModuleInfo(name = "Step", description = "Makes you step up blocks.", category = FeatureCategory.MOVEMENT)
public class StepModule extends Module implements StepListener {

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
        event.stepHeight = 10.0f;
    }

}
