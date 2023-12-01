package de.vandalismdevelopment.vandalism.util;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class PlayerUtil {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private static ClientPlayerEntity player() {
        return mc().player;
    }

    private static ClientWorld world() {
        return mc().world;
    }

    private static ClientPlayNetworkHandler networkHandler() {
        return mc().getNetworkHandler();
    }

    public enum Dimension {
        OVERWORLD, NETHER, END
    }

    public static Dimension getDimension() {
        if (world() == null) return Dimension.OVERWORLD;
        return switch (world().getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.NETHER;
            case "the_end" -> Dimension.END;
            default -> Dimension.OVERWORLD;
        };
    }

    public static boolean rayTraceBlock(final Vec3d targetPosition, final double maxDistance) {
        if (player() == null || world() == null) {
            return false;
        }
        final Vec3d playerPosition = player().getEyePos();
        final Vec3d lookDirection = targetPosition.subtract(playerPosition).normalize();
        final Vec3d currentPos = playerPosition.add(lookDirection.x * maxDistance, lookDirection.y * maxDistance, lookDirection.z * maxDistance);
        final BlockHitResult rayTraceResult = world().raycast(new RaycastContext(playerPosition, currentPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player()));
        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

    private static GameMenuScreen GAME_MENU_SCREEN = null;
    private static ServerInfo LAST_SERVER_INFO = null;

    public static void connectToLastServer() {
        if (LAST_SERVER_INFO == null) return;
        if (world() != null) {
            disconnect();
        }
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc(), ServerAddress.parse(LAST_SERVER_INFO.address), LAST_SERVER_INFO, false);
    }

    public static boolean lastServerExists() {
        return LAST_SERVER_INFO != null;
    }

    public static ServerInfo getLastServerInfo() {
        return LAST_SERVER_INFO;
    }

    public static void setLastServerInfo(final ServerInfo serverInfo) {
        LAST_SERVER_INFO = serverInfo;
    }

    public static void disconnect() {
        if (GAME_MENU_SCREEN == null) {
            GAME_MENU_SCREEN = new GameMenuScreen(false);
            GAME_MENU_SCREEN.init(mc(), mc().getWindow().getScaledWidth(), mc().getWindow().getScaledHeight());
        }
        GAME_MENU_SCREEN.disconnect();
    }

    public static Int2ObjectMap<ItemStack> createDummyModifiers() {
        final Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        if (player() == null) return int2ObjectMap;
        final DefaultedList<Slot> defaultedList = player().currentScreenHandler.slots;
        final List<ItemStack> list = Lists.newArrayListWithCapacity(defaultedList.size());
        for (final Slot slot : defaultedList) list.add(slot.getStack().copy());
        for (int i = 0; i < defaultedList.size(); i++) {
            final ItemStack original = list.get(i), copy = defaultedList.get(i).getStack();
            if (!ItemStack.areEqual(original, copy)) int2ObjectMap.put(i, copy.copy());
        }
        return int2ObjectMap;
    }

    public static <T extends ScreenHandler> void quickMoveInventory(final HandledScreen<T> screen, final int from, final int to) {
        for (int i = from; i < to; i++) {
            final T handler = screen.getScreenHandler();
            if (handler.slots.size() <= i || mc().currentScreen == null || mc().interactionManager == null) {
                break;
            }
            final Slot slot = handler.slots.get(i);
            if (slot.getStack().isEmpty()) continue;
            mc().interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player());
        }
    }

    private final static SimpleCommandExceptionType NOT_IN_GAME = new SimpleCommandExceptionType(Text.literal("You need to be in-game to get items!"));
    private final static SimpleCommandExceptionType NOT_IN_CREATIVE_MODE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

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

    public static ItemStack createItemStack(final Item item, final String nbt) {
        return createItemStack(item, 1, nbt);
    }

    public static ItemStack createItemStack(final Item item, final int count, final String nbt) {
        final ItemStack stack = new ItemStack(item, count);
        try {
            if (!nbt.isBlank()) {
                stack.setNbt(NbtHelper.fromNbtProviderString(nbt));
            }
        } catch (final CommandSyntaxException e) {
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
        } catch (final CommandSyntaxException e) {
            Vandalism.getInstance().getLogger().error("Failed to create block item stack with nbt: " + nbt, e);
        }
        return stack;
    }

    public static ItemStack appendClientSideInfoToItemStack(final ItemStack stack, final Text name, @Nullable final Text... description) {
        return appendClientSideInfoToItemStack(stack, name, false, description);
    }

    public static ItemStack appendClientSideInfoToItemStack(final ItemStack stack, final Text name, final boolean glint, @Nullable final Text... description) {
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

    public static void giveItemStack(final ItemStack itemStack) {
        giveItemStack(itemStack, true);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage) {
        try {
            if (player() == null || networkHandler() == null) throw NOT_IN_GAME.create();
            if (!player().getAbilities().creativeMode) throw NOT_IN_CREATIVE_MODE.create();
            networkHandler().sendPacket(new CreativeInventoryActionC2SPacket(player().getInventory().selectedSlot + 36, itemStack));
            if (receiveMessage) {
                PlayerUtil.infoChatMessage("You should have received '" + itemStack.getName().getString() + "' item.");
            }
            return true;
        } catch (final Throwable throwable) {
            PlayerUtil.errorChatMessage("Failed to give item cause of: " + throwable);
        }
        return false;
    }

    private enum Type implements EnumNameNormalizer {

        INFO(Color.GREEN), WARNING(Color.ORANGE), ERROR(Color.RED);

        private final MutableText prefix;

        Type(final Color color) {
            this.prefix = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).append("[").append(Text.literal(this.normalizeName(this.name())).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB())))).append("] ");
        }

        public MutableText getPrefix() {
            return this.prefix.copy();
        }

    }

    private final static MutableText CHAT_PREFIX = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).append("(").append(Text.literal(Vandalism.getInstance().getName()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.WHITE.getRGB())))).append(") ");

    public static void infoChatMessage(final String message) {
        infoChatMessage(Text.literal(message));
    }

    public static void infoChatMessage(final Text message) {
        chatMessage(Type.INFO.getPrefix().copy().append(message));
    }

    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    public static void warningChatMessage(final Text message) {
        chatMessage(Type.WARNING.getPrefix().copy().append(message));
    }

    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    public static void errorChatMessage(final Text message) {
        chatMessage(Type.ERROR.getPrefix().copy().append(message));
    }

    public static void emptyChatMessage() {
        chatMessage(Text.literal("\n"));
    }

    public static void chatMessage(final String message) {
        chatMessage(Text.literal(message));
    }

    public static void chatMessage(final Text message) {
        chatMessage(message, true);
    }

    public static void chatMessage(final String message, final boolean prefix) {
        chatMessage(Text.literal(message), prefix);
    }

    public static void chatMessage(final Text message, final boolean prefix) {
        final InGameHud inGameHud = mc().inGameHud;
        if (inGameHud == null) return;
        final ChatHud chatHud = inGameHud.getChatHud();
        if (chatHud == null) return;
        chatHud.addMessage(prefix ? CHAT_PREFIX.copy().append(message) : message);
    }

}
