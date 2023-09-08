package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.BlockListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

public class LiquidDensityModule extends Module implements BlockListener {

    public LiquidDensityModule() {
        super(
                "Liquid Density",
                "Lets you and vehicles be just like jesus.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(BlockEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(BlockEvent.ID, this);
    }

    @Override
    public void onCollisionShapeGet(final BlockEvent event) {
        if (player() == null) return;
        if (options().useKey.isPressed()) {
            final ItemStack mainHandStack = player().getMainHandStack();
            if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0) {
                return;
            }
        }
        if (player().isUsingRiptide()) return;
        final BlockState state = event.state;
        final Block block = state.getBlock();
        if (event.pos.getY() < player().getY() && (block instanceof FluidBlock || !block.getFluidState(state).isEmpty())) {
            event.shape = VoxelShapes.fullCube();
            event.shouldUpdate = true;
        }
    }

}
