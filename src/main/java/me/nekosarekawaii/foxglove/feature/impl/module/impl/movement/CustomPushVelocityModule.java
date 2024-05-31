package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.EntityPushListener;
import me.nekosarekawaii.foxglove.event.FluidPushListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderDoubleValue;

@ModuleInfo(name = "Custom Push Velocity", description = "Allows you to customize the entity/block and liquid push velocity which applies to you.", category = FeatureCategory.MOVEMENT)
public class CustomPushVelocityModule extends Module implements EntityPushListener, FluidPushListener {

    private final Value<Boolean> cancelEntityPush = new BooleanValue("Cancel Entity Push", "Cancels the entity push velocity.", this, true);

    private final Value<Double> entityPushMultiplier = new SliderDoubleValue(
            "Entity Push Multiplier", "Which multiplier of velocity should a entity push apply to you.", this, 0.0d, -2.0d, 2.0d
    ).visibleConsumer(() -> !this.cancelEntityPush.getValue());

    private final Value<Boolean> cancelFluidPush = new BooleanValue("Cancel Fluid Push", "Cancels the fluid push velocity.", this, true);

    private final Value<Double> fluidPushSpeed = new SliderDoubleValue(
            "Fluid Push Value", "Which value of speed should a fluid push apply to you.", this, 0.0d, -2.0d, 2.0d
    ).visibleConsumer(() -> !this.cancelFluidPush.getValue());

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
        if (this.cancelEntityPush.getValue()) entityPushEvent.cancel();
        else entityPushEvent.value = this.entityPushMultiplier.getValue();
    }

    @Override
    public void onFluidPush(final FluidPushEvent fluidPushEvent) {
        if (this.cancelFluidPush.getValue()) fluidPushEvent.cancel();
        else fluidPushEvent.speed = this.fluidPushSpeed.getValue();
    }

}
