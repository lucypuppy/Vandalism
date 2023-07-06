package me.nekosarekawaii.foxglove.mixin.net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Redirect(method = "hasGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasGlint(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean injectGetDisplayStacks(final Item instance, final ItemStack stack) {
        final NbtCompound nbt = stack.getNbt();
        return instance.hasGlint(stack) || (nbt != null && nbt.contains("clientsideGlint"));
    }

}
