package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.EntityListener;
import de.nekosarekawaii.foxglove.event.FluidListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderDoubleValue;

@ModuleInfo(name = "Custom Push Velocity", description = "Allows you to customize the entity/block and liquid push velocity which applies to you.", category = FeatureCategory.MOVEMENT)
public class CustomVelocityModule extends Module implements EntityListener, FluidListener {

    private final Value<Boolean> modifyEntityPush = new BooleanValue(
            "Modify Entity Push",
            "If enabled you can modify the entity push velocity.",
            this,
            true
    );

    private final Value<Double> entityPushMultiplier = new SliderDoubleValue(
            "Entity Push Multiplier",
            "Which multiplier of velocity should a entity push apply to you.",
            this,
            0.0d,
            -2.0d,
            2.0d
    ).visibleConsumer(this.modifyEntityPush::getValue);

    private final Value<Boolean> modifyFluidPush = new BooleanValue(
            "Modify Fluid Push",
            "If enabled you can modify the fluid push velocity.",
            this,
            true
    );

    private final Value<Double> fluidPushSpeed = new SliderDoubleValue(
            "Fluid Push Value",
            "Which value of speed should a fluid push apply to you.",
            this,
            0.0d,
            -2.0d,
            2.0d
    ).visibleConsumer(this.modifyFluidPush::getValue);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(EntityPushEvent.ID, this);
        DietrichEvents2.global().subscribe(FluidPushEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(EntityPushEvent.ID, this);
        DietrichEvents2.global().unsubscribe(FluidPushEvent.ID, this);
    }

    @Override
    public void onEntityPush(final EntityPushEvent entityPushEvent) {
        if (!this.modifyEntityPush.getValue()) return;
        final double value = this.entityPushMultiplier.getValue();
        if (value == 0.0d) entityPushEvent.cancel();
        else entityPushEvent.value = value;
    }

    @Override
    public void onFluidPush(final FluidPushEvent fluidPushEvent) {
        if (!this.modifyFluidPush.getValue()) return;
        final double speed = this.fluidPushSpeed.getValue();
        if (speed == 0.0d) fluidPushEvent.cancel();
        else fluidPushEvent.speed = speed;
    }

}
