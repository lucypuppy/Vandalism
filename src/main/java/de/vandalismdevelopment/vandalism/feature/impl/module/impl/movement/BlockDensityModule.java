package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.WorldListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;

public class BlockDensityModule extends Module implements WorldListener {

    private final static List<Block> BLOCKS = List.of(
            Blocks.CACTUS,
            Blocks.SWEET_BERRY_BUSH,
            Blocks.POWDER_SNOW,
            Blocks.MAGMA_BLOCK,
            Blocks.COBWEB
    );

    public BlockDensityModule() {
        super(
                "Block Density",
                "Prevents you and your vehicles from clipping into blocks that can change your movement or damage you.",
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
        if (this.player() == null) return;
        final BlockState state = event.state;
        final Block block = state.getBlock();
        final boolean isFluidBlock = event.pos.getY() < this.player().getY() && (block instanceof FluidBlock || !block.getFluidState(state).isEmpty());
        if (isFluidBlock) {
            if (this.options().useKey.isPressed()) {
                final ItemStack mainHandStack = this.player().getMainHandStack();
                if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0) {
                    return;
                }
            }
            if (this.player().isUsingRiptide()) {
                return;
            }
        }
        if (isFluidBlock || BLOCKS.contains(block)) {
            event.shape = VoxelShapes.fullCube();
            event.shouldUpdate = true;
        }
    }

}
