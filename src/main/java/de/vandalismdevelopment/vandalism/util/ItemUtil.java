package de.vandalismdevelopment.vandalism.util;

import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {

    public static ItemStack createSpawnEggItemStack(final Item origin, final String id) {
        final ItemStack item = new ItemStack(origin);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("id", id);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    public static NbtCompound createEffectNBT(final String id, final int duration, final int amplifier, final boolean showParticles) {
        final NbtCompound effect = new NbtCompound();
        effect.putString("id", "minecraft:" + id);
        effect.putByte("show_particles", showParticles ? (byte) 1 : (byte) 0);
        effect.putInt("duration", duration);
        effect.putByte("amplifier", (byte) amplifier);
        return effect;
    }

    public static ItemStack createItemStack(final ItemStack stack, final Text name, @Nullable final Text... description) {
        return createItemStack(stack, name, false, description);
    }

    public static ItemStack createItemStack(final ItemStack stack, final Text name, final boolean glint, @Nullable final Text... description) {
        final NbtCompound base = stack.getOrCreateNbt();
        base.put(CreativeTabRegistry.CLIENTSIDE_NAME, new NbtCompound());
        if (glint) base.put(CreativeTabRegistry.CLIENTSIDE_GLINT, new NbtCompound());
        stack.setCustomName(name);
        if (description != null) {
            final NbtList lore = new NbtList();
            for (final Text text : description) {
                if (text != null) lore.add(NbtString.of(Text.Serializer.toJson(text)));
            }
            stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
        }
        return stack;
    }

}
