package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.BlockListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidFillable;
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
        final Block block = event.state.getBlock();
        if (
                event.pos.getY() < player().getY() &&
                        (
                                block instanceof FluidBlock ||
                                        block instanceof FluidFillable
                        )
        ) {
            event.shape = VoxelShapes.fullCube();
            event.shouldUpdate = true;
        }
    }

}
