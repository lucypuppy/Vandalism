package de.vandalismdevelopment.vandalism.util;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {

    private final static SimpleCommandExceptionType
            NOT_IN_GAME = new SimpleCommandExceptionType(Text.literal("You need to be in-game to get items!")),
            NOT_IN_CREATIVE_MODE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

    public static ItemStack addEnchantment(final ItemStack stack, final Enchantment enchantment, final int level) {
        return addEnchantment(stack, EnchantmentHelper.getEnchantmentId(enchantment), level);
    }

    public static ItemStack addEnchantment(final ItemStack stack, final Identifier enchantmentId, final int level) {
        final NbtCompound tag = stack.getOrCreateNbt();
        if (!tag.contains(ItemStack.ENCHANTMENTS_KEY, 9)) tag.put(ItemStack.ENCHANTMENTS_KEY, new NbtList());
        final NbtList nbtList = tag.getList(ItemStack.ENCHANTMENTS_KEY, 10);
        nbtList.add(EnchantmentHelper.createNbt(enchantmentId, level));
        stack.setNbt(tag);
        return stack;
    }

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

    public static boolean giveItemStack(final ItemStack itemStack) {
        return giveItemStack(itemStack, true);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final ClientPlayerEntity player = mc.player;
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        try {
            if (player == null || networkHandler == null) throw NOT_IN_GAME.create();
            if (!player.getAbilities().creativeMode) throw NOT_IN_CREATIVE_MODE.create();
            networkHandler.sendPacket(
                    new CreativeInventoryActionC2SPacket(
                            player.getInventory().selectedSlot + 36,
                            itemStack
                    )
            );
            if (receiveMessage) {
                ChatUtil.infoChatMessage("You should have received '" + itemStack.getName().getString() + "' item.");
            }
            return true;
        } catch (final Throwable throwable) {
            ChatUtil.errorChatMessage("Failed to give item cause of: " + throwable);
        }
        return false;
    }

}
