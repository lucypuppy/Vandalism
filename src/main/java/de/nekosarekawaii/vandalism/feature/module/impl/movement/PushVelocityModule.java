package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.entity.EntityPushListener;
import de.nekosarekawaii.vandalism.base.event.entity.FluidPushListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PushVelocityModule extends AbstractModule implements EntityPushListener, FluidPushListener {

    private final BooleanValue modifyEntityPush = new BooleanValue(
            this,
            "Modify Entity Push",
            "If enabled you can modify the entity push velocity.",
            true
    );

    private final Value<Double> entityPushMultiplier = new DoubleValue(
            this,
            "Entity Push Multiplier",
            "Which multiplier of velocity should a entity push apply to you.",
            0.0d,
            -2.0d,
            2.0d
    ).visibleCondition(this.modifyEntityPush::getValue);

    private final BooleanValue modifyFluidPush = new BooleanValue(
            this,
            "Modify Fluid Push",
            "If enabled you can modify the fluid push velocity.",
            true
    );

    private final Value<Double> fluidPushSpeed = new DoubleValue(
            this,
            "Fluid Push Value",
            "Which value of speed should a fluid push apply to you.",
            0.0d,
            -2.0d,
            2.0d
    ).visibleCondition(this.modifyFluidPush::getValue);

    public PushVelocityModule() {
        super("Push Velocity", "Modifies the entity and the fluid push velocity you take.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(EntityPushEvent.ID, this);
        DietrichEvents2.global().subscribe(FluidPushEvent.ID, this);
    }

    @Override
    public void onDisable() {
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

        if (this.mc.options.useKey.isPressed()) {
            final ItemStack mainHandStack = this.mc.player.getMainHandStack();
            if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0)
                return;
        }

        if (this.mc.player.isUsingRiptide()) return;

        final double speed = this.fluidPushSpeed.getValue();
        if (speed == 0.0d) fluidPushEvent.cancel();
        else fluidPushEvent.speed = speed;
    }

}
