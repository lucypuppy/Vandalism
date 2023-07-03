package me.nekosarekawaii.foxglove.mixin.net.minecraft.item;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ItemGroup.class)
public abstract class MixinItemGroup {

    @Inject(method = "getDisplayStacks", at = @At(value = "RETURN"), cancellable = true)
    private void injectGetDisplayStacks(final CallbackInfoReturnable<Collection<ItemStack>> cir) {
        final ItemGroup thisItemGroup = (ItemGroup) (Object) this;
        for (final CreativeTab creativeTab : Foxglove.getInstance().getCreativeTabRegistry().getCreativeTabs()) {
            if (creativeTab.getItemGroup().equals(thisItemGroup)) {
                cir.setReturnValue(creativeTab.entries());
                break;
            }
        }
    }

    @Inject(method = "getSearchTabStacks", at = @At(value = "RETURN"), cancellable = true)
    private void injectGetSearchTabStacks(final CallbackInfoReturnable<Collection<ItemStack>> cir) {
        final ItemGroup thisItemGroup = (ItemGroup) (Object) this;
        for (final CreativeTab creativeTab : Foxglove.getInstance().getCreativeTabRegistry().getCreativeTabs()) {
            if (creativeTab.getItemGroup().equals(thisItemGroup)) {
                cir.setReturnValue(creativeTab.entries());
                break;
            }
        }
    }

}
