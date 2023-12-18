package de.nekosarekawaii.vandalism.util.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemStackUtil implements MinecraftWrapper {

    private static final SimpleCommandExceptionType NOT_IN_GAME = new SimpleCommandExceptionType(Text.literal("You need to be in-game to get items!"));
    private static final SimpleCommandExceptionType NOT_IN_CREATIVE_MODE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

    public static ItemStack appendEnchantmentToItemStack(final ItemStack stack, final Enchantment enchantment, final int level) {
        return appendEnchantmentToItemStack(stack, EnchantmentHelper.getEnchantmentId(enchantment), level);
    }

    public static ItemStack appendEnchantmentToItemStack(final ItemStack stack, final Identifier enchantmentId, final int level) {
        final NbtCompound tag = stack.getOrCreateNbt();
        if (!tag.contains(ItemStack.ENCHANTMENTS_KEY, 9)) tag.put(ItemStack.ENCHANTMENTS_KEY, new NbtList());
        final NbtList nbtList = tag.getList(ItemStack.ENCHANTMENTS_KEY, 10);
        nbtList.add(EnchantmentHelper.createNbt(enchantmentId, level));
        stack.setNbt(tag);
        return stack;
    }

    public static ItemStack createSpawnEggItemStack(final SpawnEggItem originSpawnEgg, final String spawnEggID) {
        final ItemStack item = new ItemStack(originSpawnEgg);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("id", spawnEggID);
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

    public static NbtCompound createOldEffectNBT(final int id, final int duration, final int amplifier, final boolean showParticles) {
        final NbtCompound effect = new NbtCompound();
        effect.putInt("Id", id);
        effect.putByte("ShowParticles", showParticles ? (byte) 1 : (byte) 0);
        effect.putInt("Duration", duration);
        effect.putByte("Amplifier", (byte) amplifier);
        return effect;
    }

    public static ItemStack createItemStack(final Item item, final String nbt) {
        return createItemStack(item, 1, nbt);
    }

    public static ItemStack createItemStack(final Item item, final int count, final String nbt) {
        final ItemStack stack = new ItemStack(item, count);
        try {
            if (!nbt.isBlank()) {
                stack.setNbt(NbtHelper.fromNbtProviderString(nbt));
            }
        } catch (CommandSyntaxException e) {
            Vandalism.getInstance().getLogger().error("Failed to create item stack with nbt: " + nbt, e);
        }
        return stack;
    }

    public static ItemStack createItemStack(final Block block, final String nbt) {
        return createItemStack(block, 1, nbt);
    }

    public static ItemStack createItemStack(final Block block, final int count, final String nbt) {
        final ItemStack stack = new ItemStack(block, count);
        try {
            if (!nbt.isBlank()) {
                stack.setNbt(NbtHelper.fromNbtProviderString(nbt));
            }
        } catch (CommandSyntaxException e) {
            Vandalism.getInstance().getLogger().error("Failed to create block item stack with nbt: " + nbt, e);
        }
        return stack;
    }

    public static ItemStack withClientSide(final ItemStack stack, final Text name, @Nullable final Text... description) {
        return withClientSide(stack, name, false, description);
    }

    public static ItemStack withClientSide(final ItemStack stack, final Text name, final boolean glint, @Nullable final Text... description) {
        final NbtCompound base = stack.getOrCreateNbt();
        base.put(CreativeTabManager.CLIENTSIDE_NAME, new NbtCompound());
        if (glint) base.put(CreativeTabManager.CLIENTSIDE_GLINT, new NbtCompound());
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

    public static void giveItemStack(final ItemStack itemStack) {
        giveItemStack(itemStack, true);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage) {
        try {
            if (mc.player == null || mc.getNetworkHandler() == null) throw NOT_IN_GAME.create();
            if (!mc.player.getAbilities().creativeMode) throw NOT_IN_CREATIVE_MODE.create();
            mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(mc.player.getInventory().selectedSlot + 36, itemStack));
            if (receiveMessage) {
                ChatUtil.infoChatMessage("You should have received '" + itemStack.getName().getString() + "' item.");
            }
            return true;
        } catch (Throwable throwable) {
            ChatUtil.errorChatMessage("Failed to give item cause of: " + throwable);
        }
        return false;
    }

}
