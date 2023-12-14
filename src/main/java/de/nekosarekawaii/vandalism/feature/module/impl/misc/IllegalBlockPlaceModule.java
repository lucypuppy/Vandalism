package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class IllegalBlockPlaceModule extends AbstractModule implements TickGameListener {

    public BooleanValue viaVersionBug = new BooleanValue(
            this,
            "ViaVersion Bug",
            "Allows you to place blocks inside your-self on versions lower than 1.9.0 on servers that are using the plugin ViaVersion.",
            true
    );

    public IllegalBlockPlaceModule() {
        super("Illegal Block Place", "Lets you place blocks in air, liquids and your-self.", Category.MISC);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        final Entity cameraEntity = this.mc.getCameraEntity();
        if (this.mc.player == null || this.mc.interactionManager == null || cameraEntity == null) {
            return;
        }

        final HitResult hitResult = cameraEntity.raycast(this.mc.interactionManager.getReachDistance(), 0, false);
        if (!(hitResult instanceof final BlockHitResult blockHitResult) || this.mc.player.getMainHandStack().isEmpty()) {
            return;
        }

        final Block block = this.mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
        if ((block instanceof AirBlock || block instanceof FluidBlock) && this.mc.options.useKey.isPressed()) {
            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, blockHitResult);
        }
    }

}
