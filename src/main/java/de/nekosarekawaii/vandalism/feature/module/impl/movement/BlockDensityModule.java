package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.WorldListener;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiBlockValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class BlockDensityModule extends AbstractModule implements WorldListener {

    private static final List<Block> DEFAULT_AFFECTED_BLOCKS = new ArrayList<>();

    static {
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CACTUS);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SWEET_BERRY_BUSH);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.POWDER_SNOW);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.MAGMA_BLOCK);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.COBWEB);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.FIRE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SOUL_FIRE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.HONEY_BLOCK);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SLIME_BLOCK);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SOUL_SAND);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CAMPFIRE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SOUL_CAMPFIRE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CAULDRON);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.COMPOSTER);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.DRIPSTONE_BLOCK);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.POINTED_DRIPSTONE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SUGAR_CANE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.BAMBOO);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CHORUS_FLOWER);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CHORUS_PLANT);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.COCOA);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SEA_PICKLE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.KELP_PLANT);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.KELP);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.TALL_SEAGRASS);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.SEAGRASS);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CAKE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CANDLE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.CANDLE_CAKE);
        DEFAULT_AFFECTED_BLOCKS.add(Blocks.BIG_DRIPLEAF);
    }

    private final MultiBlockValue affectedBlocks = new MultiBlockValue(
            this,
            "Blocks",
            "Change the blocks that are affected by this module.",
            DEFAULT_AFFECTED_BLOCKS,
            DEFAULT_AFFECTED_BLOCKS.toArray(Block[]::new)
    );

    private final BooleanValue fluidBlocks = new BooleanValue(
            this,
            "Fluid Blocks",
            "Whether or not to apply this module to fluid blocks.",
            true
    );

    private boolean wasOneFire = false;

    public BlockDensityModule() {
        super(
                "Block Density",
                "Prevents you and your vehicles from clipping into blocks that can change your movement or damage you.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(BlockEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(BlockEvent.ID, this);
    }

    @Override
    public void onCollisionShapeGet(final BlockEvent event) {
        final BlockState state = event.state;
        final Block block = state.getBlock();
        final ItemStack mainHandStack = this.mc.player.getMainHandStack();
        final boolean eventPosIsBelowPlayer = event.pos.getY() < this.mc.player.getY();
        final FluidState fluidState = state.getFluidState();
        final boolean blockIsFluidBlock = !fluidState.isEmpty();
        final boolean useKeyIsPressed = this.mc.options.useKey.isPressed();
        final boolean isTridentWithRiptide = mainHandStack.getItem() == Items.TRIDENT && EnchantmentHelper.getRiptide(mainHandStack) > 0;
        final boolean isNotUsingRipTide = !(useKeyIsPressed && isTridentWithRiptide || this.mc.player.isUsingRiptide());
        final boolean isFluid = this.fluidBlocks.getValue() && eventPosIsBelowPlayer && blockIsFluidBlock && isNotUsingRipTide;
        if (this.affectedBlocks.isSelected(block) || isFluid) {
            final double minX = 0, minY = 0, minZ = 0, maxX = 1, maxZ = 1;
            double maxY = 1;
            if (isFluid && fluidState.getFluid() instanceof WaterFluid) {
                if (this.wasOneFire) {
                    this.wasOneFire = false;
                    this.mc.options.jumpKey.setPressed(false);
                }
                if (this.mc.player.isOnFire()) {
                    this.wasOneFire = true;
                    this.mc.options.jumpKey.setPressed(true);
                    maxY = 0.59;
                }
            }
            event.shape = VoxelShapes.cuboid(
                    minX,
                    minY,
                    minZ,
                    maxX,
                    maxY,
                    maxZ
            );
        }
    }

}
