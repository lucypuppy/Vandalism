package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.WorldListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;

public class BlockDensityModule extends AbstractModule implements WorldListener {

    private static final List<Block> BLOCKS = List.of(
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
                Category.MOVEMENT
        );
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(BlockEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(BlockEvent.ID, this);
    }

    @Override
    public void onCollisionShapeGet(final BlockEvent event) {
        if (this.mc.player == null) return;

        final BlockState state = event.state;
        final Block block = state.getBlock();

        final boolean isFluidBlock = event.pos.getY() < this.mc.player.getY() && (block instanceof FluidBlock || !block.getFluidState(state).isEmpty());
        if (isFluidBlock) {
            if (this.mc.options.useKey.isPressed()) {
                final ItemStack mainHandStack = this.mc.player.getMainHandStack();
                if (mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0)
                    return;
            }

            if (this.mc.player.isUsingRiptide())
                return;
        }

        if (isFluidBlock || BLOCKS.contains(block)) {
            event.shape = VoxelShapes.fullCube();
            event.shouldUpdate = true;
        }
    }

}
