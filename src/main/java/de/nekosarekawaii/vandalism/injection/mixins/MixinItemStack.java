package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Redirect(method = "hasGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasGlint(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean applyClientsideGlint(final Item instance, final ItemStack stack) {
        final var nbt = stack.getNbt();
        return instance.hasGlint(stack) || (nbt != null && nbt.contains(CreativeTabManager.CLIENTSIDE_GLINT));
    }

}