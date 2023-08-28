package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.impl.misc.NBTCommand;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import de.nekosarekawaii.foxglove.util.minecraft.ServerUtils;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ModuleInfo(name = "Item Stack Logger", description = "Logs incoming player item stacks into the chat and writes a complete log into a log file.", category = FeatureCategory.MISC)
public class ItemStackLoggerModule extends Module implements TickListener {

    private final Value<Boolean> notifyInChat = new BooleanValue("Notify in Chat", "If enabled this module sends a notification into the chat to inform you about a newly found item.", this, true);

    private final File loggedItemsDir;
    private final DateFormat formatter;
    private final Gson gson;

    public ItemStackLoggerModule() {
        super();
        this.loggedItemsDir = new File(Foxglove.getInstance().getDir(), "logged-items");
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
        final ClientWorld world = mc.world;
        if (world == null) return;
        for (final Entity entity : world.getEntities()) {
            if (entity == mc.player) continue;
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
            if (!rawItemName.contains("block") && !rawItemName.contains("item")) {
                if (Block.getBlockFromItem(item) == Blocks.AIR) itemName.append(" item");
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
                copy.putString(NBTCommand.displayTitleNbtKey, itemFromText);
                displayNbt = NbtHelper.toPrettyPrintedText(copy).getString();
            }
            final String giveCommand = Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue() + "give " + item + nbt + " " + count,
                    server = "[Server] " + (ServerUtils.lastServerExists() ? ServerUtils.getLastServerInfo().address : "single player"),
                    position = "[Position] " + entity.getBlockPos().toShortString(),
                    damageString = "[Damage] " + damage,
                    countString = "[Count] " + count,
                    nbtCountString = "[NBT Count] " + nbtCount,
                    logStart = String.format("%s%n%n[%s] %s%n%n%s%n%n%s%n%n%s%n%n%s%n", server, entityType, entityName, position, damageString, countString, nbtCountString),
                    data = logStart + System.lineSeparator() + "[NBT]" + System.lineSeparator();
            if (!this.loggedItemsDir.exists()) {
                if (!this.loggedItemsDir.mkdirs()) {
                    Foxglove.getInstance().getLogger().error("Failed to create '" + this.loggedItemsDir.getAbsolutePath() + "' directory!");
                    this.setState(false);
                    return;
                }
            }
            final File itemNameDir = new File(this.loggedItemsDir, itemName.toString());
            if (!itemNameDir.exists()) {
                if (!itemNameDir.mkdirs()) {
                    Foxglove.getInstance().getLogger().error("Failed to create '" + itemNameDir.getAbsolutePath() + "' directory!");
                    return;
                }
            }
            final File itemNbtFile = new File(itemNameDir, nbtCount + " NBT's  " + damageString.hashCode() + "  " + nbt.hashCode());
            if (itemNbtFile.exists()) return;
            final String normalWithoutNBT = position + " | " + damageString + " | " + countString + " | " + nbtCountString;
            String prettyNBT;
            try {
                prettyNBT = this.gson.toJson(JsonParser.parseString(nbt));
            } catch (final Throwable ignored) {
                prettyNBT = nbt;
            }
            Files.write(
                    Path.of(itemNbtFile.getAbsolutePath()),
                    ("[Date] " + this.formatter.format(new Date()) + System.lineSeparator() + System.lineSeparator() + data + prettyNBT + System.lineSeparator() + System.lineSeparator() + "[Give Command]" + System.lineSeparator() + giveCommand + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
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
                                                itemNbtFile.getParent()
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
                                                itemNbtFile.getAbsolutePath()
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
                                            Foxglove.getInstance().getCommandRegistry().generateClickEvent("nbt displaynbt " + displayNbt)
                                    )
                    );
                    text.append(displayNBTButton);
                }
                ChatUtils.infoChatMessage(Text.literal("Item Stack Logger").formatted(Formatting.AQUA));
                ChatUtils.chatMessage(Text.literal("Found a " + itemFromText + ".").formatted(Formatting.DARK_AQUA), false);
                ChatUtils.chatMessage(Text.literal(normalWithoutNBT).formatted(Formatting.LIGHT_PURPLE), false);
                ChatUtils.chatMessage(text.formatted(Formatting.DARK_GREEN), false);
            }
        } catch (final Throwable throwable) {
            Foxglove.getInstance().getLogger().error("Failed to log stack!", throwable);
        }
    }

}
