package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.world;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.TrueSightModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Shadow
    @Final
    private static Set<Item> BLOCK_MARKER_ITEMS;

    @Inject(method = "randomBlockDisplayTick", at = @At(value = "HEAD", target = "HEAD"))
    private void injectRandomBlockDisplayTick(final int centerX, final int centerY, final int centerZ, final int radius, final Random random, Block block, final BlockPos.Mutable pos, final CallbackInfo ci) {
        final TrueSightModule trueSightModule = Foxglove.getInstance().getModuleRegistry().getTrueSightModule();
        if (trueSightModule.isEnabled() && trueSightModule.blocks.getValue()) {
            block = null;
        }
    }

    @Redirect(method = "randomBlockDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 1))
    private Block redirectRandomBlockDisplayTick(final BlockState instance) {
        final TrueSightModule trueSightModule = Foxglove.getInstance().getModuleRegistry().getTrueSightModule();
        if (trueSightModule.isEnabled() && trueSightModule.blocks.getValue()) {
            if (BLOCK_MARKER_ITEMS.contains(Item.fromBlock(instance.getBlock()))) {
                return null;
            }
        }
        return instance.getBlock();
    }

}
