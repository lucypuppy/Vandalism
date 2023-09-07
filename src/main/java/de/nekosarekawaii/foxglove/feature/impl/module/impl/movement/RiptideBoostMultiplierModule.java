package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderFloatValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RiptideBoostMultiplierModule extends Module {

    public final Value<Float> multiplier = new SliderFloatValue("Multiplier", "Multiplier of the boost", this, 1.0f, .0f, 2.0f);

    public RiptideBoostMultiplierModule() {
        super(
                "Riptide Boost Multiplier",
                "Allows you to go higher with a trident using the riptide enchantment.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    public boolean isExempted() {
        final ItemStack itemStack = player().getMainHandStack();
        return itemStack.getItem() == Items.TRIDENT && EnchantmentHelper.getLevel(Enchantments.RIPTIDE, itemStack) > 0;
    }

}
