package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.TrueSightModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Shadow
    @Final
    private static Set<Item> BLOCK_MARKER_ITEMS;

    @Inject(method = "getBlockParticle", at = @At(value = "HEAD"), cancellable = true)
    private void hookTrueSight(final CallbackInfoReturnable<Block> cir) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        if (trueSightModule.isActive() && trueSightModule.blocks.getValue()) {
            cir.setReturnValue(null);
        }
    }

    @Redirect(method = "randomBlockDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 1))
    private Block hookTrueSight(final BlockState instance) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        if (trueSightModule.isActive() && trueSightModule.blocks.getValue()) {
            if (BLOCK_MARKER_ITEMS.contains(Item.fromBlock(instance.getBlock()))) {
                return null;
            }
        }
        return instance.getBlock();
    }

}
