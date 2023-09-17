package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.BlockListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

public class BlockDensityModule extends Module implements BlockListener {

    public BlockDensityModule() {
        super(
                "Block Density",
                "Prevents you and your vehicles from clipping into fluid blocks, cactus blocks, sweet berry bush blocks, powder snow blocks, magma blocks and cobweb blocks.",
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
        final BlockState state = event.state;
        final Block block = state.getBlock();
        final boolean isFluidBlock = event.pos.getY() < player().getY() && (block instanceof FluidBlock || !block.getFluidState(state).isEmpty());
        if (isFluidBlock) {
            if (options().useKey.isPressed()) {
                final ItemStack mainHandStack = player().getMainHandStack();
                if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0) {
                    return;
                }
            }
            if (player().isUsingRiptide()) return;
        }
        if (isFluidBlock || block instanceof CactusBlock || block instanceof SweetBerryBushBlock || block instanceof PowderSnowBlock || block instanceof MagmaBlock || block instanceof CobwebBlock) {
            event.shape = VoxelShapes.fullCube();
            event.shouldUpdate = true;
        }
    }

}
