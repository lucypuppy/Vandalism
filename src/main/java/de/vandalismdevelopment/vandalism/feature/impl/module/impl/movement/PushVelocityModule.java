package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.EntityListener;
import de.vandalismdevelopment.vandalism.event.FluidListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderDoubleValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PushVelocityModule extends Module implements EntityListener, FluidListener {

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

    public PushVelocityModule() {
        super(
                "Push Velocity",
                "Modifies the entity and the fluid push velocity you take.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

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
        if (options().useKey.isPressed()) {
            final ItemStack mainHandStack = player().getMainHandStack();
            if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0) {
                return;
            }
        }
        if (player().isUsingRiptide()) return;
        final double speed = this.fluidPushSpeed.getValue();
        if (speed == 0.0d) fluidPushEvent.cancel();
        else fluidPushEvent.speed = speed;
    }

}
