package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class IllegalBlockPlaceModule extends Module implements TickListener {

    public Value<Boolean> viaVersionBug = new BooleanValue(
            "ViaVersion Bug",
            "Allows you to place blocks inside your-self on versions lower than 1.9.0 on servers that are using the plugin ViaVersion.",
            this,
            true
    );

    public IllegalBlockPlaceModule() {
        super(
                "Illegal Block Place",
                "Lets you place blocks in air, liquids and your-self.",
                FeatureCategory.MISC,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        final Entity cameraEntity = this.mc().getCameraEntity();
        if (this.player() == null || this.interactionManager() == null || cameraEntity == null) {
            return;
        }

        final HitResult hitResult = cameraEntity.raycast(this.interactionManager().getReachDistance(), 0, false);
        if (!(hitResult instanceof final BlockHitResult blockHitResult) || this.player().getMainHandStack().isEmpty()) {
            return;
        }

        final Block block = this.world().getBlockState(blockHitResult.getBlockPos()).getBlock();
        if ((block instanceof AirBlock || block instanceof FluidBlock) && this.options().useKey.isPressed()) {
            this.interactionManager().interactBlock(this.player(), Hand.MAIN_HAND, blockHitResult);
        }
    }

}
