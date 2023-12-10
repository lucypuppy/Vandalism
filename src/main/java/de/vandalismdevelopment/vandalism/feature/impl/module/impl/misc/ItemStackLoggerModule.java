package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc.NbtCommand;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.ServerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemStackLoggerModule extends Module implements TickListener {

    private final Value<Boolean> notifyInChat = new BooleanValue("Notify in Chat", "If enabled this module sends a notification into the chat to inform you about a newly found item.", this, true);

    private static final File LOGGED_ITEMS_DIR = new File(Vandalism.getInstance().getRunDirectory(), "logged-items");

    private final DateFormat formatter;

    private final ExecutorService executorService;

    public ItemStackLoggerModule() {
        super("Item Stack Logger", "Logs item stacks to '" + LOGGED_ITEMS_DIR.getAbsolutePath() + "'", FeatureCategory.MISC, false, false);
        this.formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");
        this.executorService = Executors.newFixedThreadPool(4);
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this, Priorities.HIGH);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.world == null) return;
        for (final Entity entity : this.mc.world.getEntities()) {
            if (entity == this.mc.player) continue;
            if (entity instanceof final ItemEntity itemEntity) {
                this.logStack(entity, itemEntity.getStack());
            } else {
                for (final ItemStack stack : entity.getItemsEquipped())
                    this.logStack(entity, stack);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void logStack(final Entity entity, final ItemStack stack) {
        final Item item = stack.getItem();
        if (item.equals(Items.AIR)) {
            return;
        }

        final String rawItemName = item.toString().replace("_", " ");
        final StringBuilder itemName = new StringBuilder(rawItemName);

        final boolean isItem = Block.getBlockFromItem(item) == Blocks.AIR;

        if (!rawItemName.contains("block") && !rawItemName.contains("item")) {
            if (isItem) itemName.append(" item");
            else itemName.append(" block");
        }

        final NbtCompound nbt = stack.getOrCreateNbt();
        final int damage = stack.getDamage(), nbtCount = nbt.getKeys().size(), count = stack.getCount();
        if ((nbtCount == 1 && nbt.contains("Damage")) || (damage == 0 && nbtCount == 0)) {
            return;
        }

        final String itemNameString = itemName.toString();
        final boolean entityIsPlayer = entity instanceof PlayerEntity;
        final String entityName = (entityIsPlayer ? ((PlayerEntity) entity).getGameProfile().getName() : entity.getName().getString());
        final String entityType = (entityIsPlayer ? "Player" : entity.getType().getName().getString());
        final String itemFromText = itemNameString + " from " + entityType + " " + entityName;
        final String position = entity.getBlockPos().toShortString();

        LOGGED_ITEMS_DIR.mkdirs();
        if (!LOGGED_ITEMS_DIR.exists()) return;

        final File serverDir = new File(LOGGED_ITEMS_DIR, ServerUtil.lastServerExists() ? ServerUtil.getLastServerInfo().address : "single player");
        serverDir.mkdirs();
        if (!serverDir.exists()) return;

        final File playerOrEntityDir = new File(serverDir, entityType);
        playerOrEntityDir.mkdirs();
        if (!playerOrEntityDir.exists()) return;

        final File playerOrEntityNameDir = new File(playerOrEntityDir, entityName);
        playerOrEntityNameDir.mkdirs();
        if (!playerOrEntityNameDir.exists()) return;

        final File itemOrBlockDir = new File(playerOrEntityNameDir, isItem ? "item" : "block");
        itemOrBlockDir.mkdirs();

        if (!itemOrBlockDir.exists()) return;
        final File itemNameDir = new File(itemOrBlockDir, itemNameString.replace(" item", "").replace(" block", ""));
        itemNameDir.mkdirs();

        if (!itemNameDir.exists()) return;
        final File itemNbtFile = new File(itemNameDir, nbtCount + "-[" + String.valueOf(damage).hashCode() + nbt.hashCode() + "].nbt");

        if (itemNbtFile.exists()) return;

        this.executorService.submit(() -> {
            try {
                itemNbtFile.createNewFile();

                final NbtCompound itemNbt = new NbtCompound();
                itemNbt.putString("At Date", this.formatter.format(new Date()));
                itemNbt.putString("At Position", position);
                itemNbt.putInt("Damage", damage);
                itemNbt.putInt("Count", count);
                itemNbt.putInt("NBT Count", nbtCount);
                itemNbt.put("NBT", nbt);
                NbtIo.write(itemNbt, itemNbtFile);

                final String normalWithoutNBT = "Position: " + position + " | Damage: " + damage + " | Count: " + count + " | NBT Count: " + nbtCount;
                if (this.notifyInChat.getValue()) {
                    final MutableText text = Text.literal("Options:");
                    final MutableText copyButton = Text.literal(" (Copy Data)");
                    copyButton.setStyle(copyButton.getStyle().withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, normalWithoutNBT + " | [NBT] " + nbt)));
                    final MutableText copyGiveCommandButton = Text.literal(" (Copy Give Command)");
                    final String giveCommand = Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue() + "give " + item + nbt + " " + count;
                    copyGiveCommandButton.setStyle(copyGiveCommandButton.getStyle().withFormatting(Formatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, giveCommand)));
                    final MutableText openDirectoryButton = Text.literal(" (Open Directory)");
                    openDirectoryButton.setStyle(openDirectoryButton.getStyle().withFormatting(Formatting.GOLD).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, itemNbtFile.getParent())));
                    final MutableText openFileButton = Text.literal(" (Open File)");
                    openFileButton.setStyle(openFileButton.getStyle().withFormatting(Formatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, itemNbtFile.getAbsolutePath())));
                    text.append(copyButton).append(copyGiveCommandButton).append(openDirectoryButton).append(openFileButton);
                    final MutableText displayNBTButton = Text.literal(" (Display NBT)");
                    Style style = displayNBTButton.getStyle();
                    style = style.withFormatting(Formatting.DARK_RED);
                    final NbtCompound copy = nbt.copy();
                    copy.putString(NbtCommand.DISPLAY_TITLE_NBT_KEY, itemFromText);
                    final String command = "nbt displaynbt " + NbtHelper.toPrettyPrintedText(copy).getString();
                    final ClickEvent clickEvent = Vandalism.getInstance().getCommandRegistry().generateClickEvent(command);
                    style = style.withClickEvent(clickEvent);
                    displayNBTButton.setStyle(style);
                    text.append(displayNBTButton);

                    ChatUtil.infoChatMessage(Text.literal("Item Stack Logger").formatted(Formatting.AQUA));
                    ChatUtil.chatMessage(Text.literal("Found a " + itemFromText + ".").formatted(Formatting.DARK_AQUA), false);
                    ChatUtil.chatMessage(Text.literal(normalWithoutNBT).formatted(Formatting.LIGHT_PURPLE), false);
                    ChatUtil.chatMessage(text.formatted(Formatting.DARK_GREEN), false);
                }

            } catch (final Throwable throwable) {
                Vandalism.getInstance().getLogger().error("Failed to log stack!", throwable);
            }
        });
    }

}
