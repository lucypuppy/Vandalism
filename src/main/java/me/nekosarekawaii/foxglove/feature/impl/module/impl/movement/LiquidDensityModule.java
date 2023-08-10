package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.BlockListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.shape.VoxelShapes;

@ModuleInfo(name = "Liquid Density", description = "Makes you walk on liquids.", category = FeatureCategory.MOVEMENT)
public class LiquidDensityModule extends Module implements BlockListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(BlockEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(BlockEvent.ID, this);
    }

    @Override
    public void getCollisionShape(BlockEvent event) {
        if (mc.player == null)
            return;

        if (event.pos.getY() < mc.player.getY() && event.state.getBlock() instanceof FluidBlock) {
            event.shape = VoxelShapes.fullCube();
            event.shouldUpdate = true;
        }
    }

}
