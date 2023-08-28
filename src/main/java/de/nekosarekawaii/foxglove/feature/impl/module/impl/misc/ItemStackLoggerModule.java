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
            final String itemName = item.toString().replace("_", " ") + " " + (Block.getBlockFromItem(item) == Blocks.AIR ? "item" : "block");
            final NbtCompound tag = stack.getNbt();
            final int damage = stack.getDamage(), nbtCount = tag != null ? tag.getKeys().size() : 0, count = stack.getCount();
            if (item.equals(Items.AIR)) return;
            if (nbtCount == 1 && tag.contains("Damage")) return;
            if (damage == 0 && nbtCount == 0) return;
            final String name = entity instanceof final PlayerEntity player ? "player " + player.getGameProfile().getName() : "entity " + entity.getName().getString();
            final String logStart = "[Position] " + System.lineSeparator() + entity.getBlockPos().toShortString() + System.lineSeparator() + System.lineSeparator() + "[Damage] " + System.lineSeparator() + damage + System.lineSeparator() + System.lineSeparator() + "[Count] " + System.lineSeparator() + count + System.lineSeparator() + System.lineSeparator() + "[NBT Count] " + System.lineSeparator() + nbtCount;
            final StringBuilder data = new StringBuilder(logStart + System.lineSeparator() + System.lineSeparator() + "[NBT]" + System.lineSeparator());
            String nbt;
            final String displayNbt;
            if (tag == null) {
                nbt = "{}";
                displayNbt = "{}";
            } else {
                nbt = NbtHelper.toPrettyPrintedText(tag).getString();
                final NbtCompound copy = tag.copy();
                copy.putString(NBTCommand.displayTitleNbtKey, itemName + " from " + name);
                displayNbt = NbtHelper.toPrettyPrintedText(copy).getString();
            }
            try {
                nbt = this.gson.toJson(JsonParser.parseString(nbt));
            } catch (final Throwable ignored) {
            }
            if (this.loggedItemsDir.exists()) {
                if (!this.loggedItemsDir.isDirectory()) {
                    this.loggedItemsDir.delete();
                }
            }
            if (!this.loggedItemsDir.exists()) {
                if (!this.loggedItemsDir.mkdirs()) {
                    Foxglove.getInstance().getLogger().error("Failed to create '" + this.loggedItemsDir.getAbsolutePath() + "' directory!");
                    this.setState(false);
                    return;
                }
            }
            final File serverDir = new File(this.loggedItemsDir, ServerUtils.lastServerExists() ? ServerUtils.getLastServerInfo().address : "single player");
            if (serverDir.exists()) {
                if (!serverDir.isDirectory()) {
                    serverDir.delete();
                }
            }
            if (!serverDir.exists()) {
                if (!serverDir.mkdirs()) {
                    Foxglove.getInstance().getLogger().error("Failed to create '" + serverDir.getAbsolutePath() + "' directory!");
                    return;
                }
            }
            final File entityDir = new File(serverDir, name);
            if (entityDir.exists()) {
                if (!entityDir.isDirectory()) {
                    entityDir.delete();
                }
            }
            if (!entityDir.exists()) {
                if (!entityDir.mkdirs()) {
                    Foxglove.getInstance().getLogger().error("Failed to create '" + entityDir.getAbsolutePath() + "' directory!");
                    return;
                }
            }
            final File itemNameDir = new File(entityDir, itemName);
            if (itemNameDir.exists()) {
                if (!itemNameDir.isDirectory()) {
                    itemNameDir.delete();
                }
            }
            if (!itemNameDir.exists()) {
                if (!itemNameDir.mkdirs()) {
                    Foxglove.getInstance().getLogger().error("Failed to create '" + itemNameDir.getAbsolutePath() + "' directory!");
                    return;
                }
            }
            final File itemNbtFile = new File(itemNameDir, String.valueOf(name.hashCode() + itemName.hashCode() + nbt.hashCode()));
            if (itemNbtFile.exists()) return;
            data.append(nbt);
            final String dataString = data.toString();
            Files.write(
                    Path.of(itemNbtFile.getAbsolutePath()),
                    ("[Data from " + formatter.format(new Date()) + "]" + System.lineSeparator() + System.lineSeparator() + dataString).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            final MutableText text = Text.literal("Options:");
            final MutableText copyButton = Text.literal(" [Copy]");
            copyButton.setStyle(
                    copyButton.getStyle()
                            .withFormatting(Formatting.YELLOW)
                            .withClickEvent(
                                    new ClickEvent(
                                            ClickEvent.Action.COPY_TO_CLIPBOARD,
                                            dataString
                                    )
                            )
            );
            final MutableText copyGiveCommandButton = Text.literal(" [Copy Give Command]");
            copyGiveCommandButton.setStyle(
                    copyGiveCommandButton.getStyle()
                            .withFormatting(Formatting.GOLD)
                            .withClickEvent(
                                    new ClickEvent(
                                            ClickEvent.Action.COPY_TO_CLIPBOARD,
                                            Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue() + "give " + item + nbt + " " + count
                                    )
                            )
            );
            final MutableText openFileButton = Text.literal(" [Open File]");
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
            text.append(copyButton).append(copyGiveCommandButton).append(openFileButton);
            if (tag != null) {
                final MutableText displayNBTButton = Text.literal(" [Display NBT]");
                displayNBTButton.setStyle(
                        displayNBTButton.getStyle()
                                .withFormatting(Formatting.DARK_RED)
                                .withClickEvent(
                                        Foxglove.getInstance().getCommandRegistry().generateClickEvent("nbt displaynbt " + displayNbt)
                                )
                );
                text.append(displayNBTButton);
            }
            ChatUtils.infoChatMessage(Text.literal("Item Stack Logger").formatted(Formatting.GREEN));
            ChatUtils.chatMessage(Text.literal("Found a " + itemName + " from " + name + ".").formatted(Formatting.DARK_AQUA), false);
            ChatUtils.chatMessage(Text.literal(logStart.replace(System.lineSeparator(), " | ")).formatted(Formatting.LIGHT_PURPLE), false);
            ChatUtils.chatMessage(text.formatted(Formatting.AQUA), false);
        } catch (final Throwable throwable) {
            Foxglove.getInstance().getLogger().error("Failed to log stack!", throwable);
        }
    }

}
