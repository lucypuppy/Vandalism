package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc.NbtCommand;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.ServerUtil;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ItemStackLoggerModule extends Module implements TickListener {

    private final Value<Boolean> notifyInChat = new BooleanValue(
            "Notify in Chat",
            "If enabled this module sends a notification into the chat to inform you about a newly found item.",
            this,
            true
    );

    private final static File LOGGED_ITEMS_DIR = new File(Vandalism.getInstance().getDir(), "logged-items");

    private final DateFormat formatter;
    private final Gson gson;

    public ItemStackLoggerModule() {
        super(
                "Item Stack Logger",
                "Logs item stacks to '" + LOGGED_ITEMS_DIR.getAbsolutePath() + "'",
                FeatureCategory.MISC,
                false,
                false
        );
        this.formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");
        this.gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
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
        if (world() == null) return;
        for (final Entity entity : world().getEntities()) {
            if (entity == player()) continue;
            if (entity instanceof final ItemEntity itemEntity) {
                this.logStack(entity, itemEntity.getStack());
            } else {
                for (final ItemStack stack : entity.getItemsEquipped()) {
                    this.logStack(entity, stack);
                }
            }
        }
    }

    private void logStack(final Entity entity, final ItemStack stack) {
        try {
            final Item item = stack.getItem();
            if (item.equals(Items.AIR)) return;
            final String rawItemName = item.toString().replace("_", " ");
            final StringBuilder itemName = new StringBuilder(rawItemName);
            final boolean isItem = Block.getBlockFromItem(item) == Blocks.AIR;
            if (!rawItemName.contains("block") && !rawItemName.contains("item")) {
                if (isItem) itemName.append(" item");
                else itemName.append(" block");
            }
            final NbtCompound base = stack.getNbt();
            final int damage = stack.getDamage(), nbtCount = (base != null) ? base.getKeys().size() : 0, count = stack.getCount();
            if ((nbtCount == 1 && base.contains("Damage")) || (damage == 0 && nbtCount == 0)) {
                return;
            }
            final String nbt,
                    displayNbt,
                    itemNameString = itemName.toString(),
                    entityType = (entity instanceof PlayerEntity) ? "Player" : "Entity",
                    entityName = entityType.equals("Player") ? ((PlayerEntity) entity).getGameProfile().getName() : entity.getName().getString(),
                    itemFromText = itemNameString + " from " + entityType + " " + entityName;
            if (base == null) {
                nbt = "{}";
                displayNbt = "{}";
            } else {
                nbt = NbtHelper.toPrettyPrintedText(base).getString();
                final NbtCompound copy = base.copy();
                copy.putString(NbtCommand.DISPLAY_TITLE_NBT_KEY, itemFromText);
                displayNbt = NbtHelper.toPrettyPrintedText(copy).getString();
            }
            final String giveCommand = Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.commandPrefix.getValue() + "give " + item + nbt + " " + count,
                    server = "Server: " + (ServerUtil.lastServerExists() ? ServerUtil.getLastServerInfo().address : "single player"),
                    position = "Position: " + entity.getBlockPos().toShortString(),
                    damageString = "Damage: " + damage,
                    countString = "Count: " + count,
                    nbtCountString = "NBT Count: " + nbtCount,
                    logStart = String.format("%s%n%n%s: %s%n%n%s%n%n%s%n%n%s%n%n%s%n", server, entityType, entityName, position, damageString, countString, nbtCountString),
                    data = logStart + System.lineSeparator();
            LOGGED_ITEMS_DIR.mkdirs();
            final File itemOrBlockDir = new File(LOGGED_ITEMS_DIR, isItem ? "item" : "block");
            itemOrBlockDir.mkdirs();
            final File itemNameDir = new File(itemOrBlockDir, itemNameString.replace(" item", "").replace(" block", ""));
            itemNameDir.mkdirs();
            final File itemZipFile = new File(itemNameDir, nbtCount + "-[" + damageString.hashCode() + nbt.hashCode() + "].zip");
            if (itemZipFile.exists()) return;
            itemZipFile.createNewFile();
            try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(itemZipFile, true))) {
                final ZipEntry zipEntry = new ZipEntry("data." + Vandalism.getInstance().getId() + "-log");
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write((
                        "Date: " + this.formatter.format(new Date()) + System.lineSeparator() +
                                System.lineSeparator() +
                                data + "[Give Command]" + System.lineSeparator() +
                                giveCommand + System.lineSeparator() +
                                System.lineSeparator() +
                                "[NBT]" + System.lineSeparator() +
                                this.gson.toJson(JsonParser.parseString(nbt))
                ).getBytes());
                zipOutputStream.closeEntry();
            }
            final String normalWithoutNBT = position + " | " + damageString + " | " + countString + " | " + nbtCountString;
            if (this.notifyInChat.getValue()) {
                final MutableText text = Text.literal("Options:");
                final MutableText copyButton = Text.literal(" (Copy Data)");
                copyButton.setStyle(
                        copyButton.getStyle()
                                .withFormatting(Formatting.GREEN)
                                .withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                                normalWithoutNBT + " | [NBT] " + nbt
                                        )
                                )
                );
                final MutableText copyGiveCommandButton = Text.literal(" (Copy Give Command)");
                copyGiveCommandButton.setStyle(
                        copyGiveCommandButton.getStyle()
                                .withFormatting(Formatting.YELLOW)
                                .withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                                giveCommand
                                        )
                                )
                );
                final MutableText openDirectoryButton = Text.literal(" (Open Directory)");
                openDirectoryButton.setStyle(
                        openDirectoryButton.getStyle()
                                .withFormatting(Formatting.GOLD)
                                .withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.OPEN_FILE,
                                                itemZipFile.getParent()
                                        )
                                )
                );
                final MutableText openFileButton = Text.literal(" (Open File)");
                openFileButton.setStyle(
                        openFileButton.getStyle()
                                .withFormatting(Formatting.RED)
                                .withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.OPEN_FILE,
                                                itemZipFile.getAbsolutePath()
                                        )
                                )
                );
                text.append(copyButton).append(copyGiveCommandButton).append(openDirectoryButton).append(openFileButton);
                if (base != null) {
                    final MutableText displayNBTButton = Text.literal(" (Display NBT)");
                    displayNBTButton.setStyle(
                            displayNBTButton.getStyle()
                                    .withFormatting(Formatting.DARK_RED)
                                    .withClickEvent(
                                            Vandalism.getInstance().getCommandRegistry().generateClickEvent("nbt displaynbt " + displayNbt)
                                    )
                    );
                    text.append(displayNBTButton);
                }
                ChatUtil.infoChatMessage(Text.literal("Item Stack Logger").formatted(Formatting.AQUA));
                ChatUtil.chatMessage(Text.literal("Found a " + itemFromText + ".").formatted(Formatting.DARK_AQUA), false);
                ChatUtil.chatMessage(Text.literal(normalWithoutNBT).formatted(Formatting.LIGHT_PURPLE), false);
                ChatUtil.chatMessage(text.formatted(Formatting.DARK_GREEN), false);
            }
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Failed to log stack!", throwable);
        }
    }

}
