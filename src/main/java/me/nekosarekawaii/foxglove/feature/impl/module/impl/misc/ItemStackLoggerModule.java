package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.NBTCommand;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.ValidatorUtils;
import me.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import me.nekosarekawaii.foxglove.util.minecraft.ServerUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@ModuleInfo(name = "Item Stack Logger", description = "Logs incoming player item stacks into the chat and writes a complete log into a log file.", category = FeatureCategory.MISC)
public class ItemStackLoggerModule extends Module implements TickListener {

    private final File itemsLog;
    private final List<Integer> containedStacks;
    private final static String lineIdSplitter = ":ID;";
    private final static DateFormat formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");

    public ItemStackLoggerModule() {
        super();
        this.itemsLog = new File(Foxglove.getInstance().getDir(), "items.log");
        this.containedStacks = new ArrayList<>();
    }

    @Override
    protected void onEnable() {
        try {
            if (!this.itemsLog.exists()) {
                if (this.itemsLog.createNewFile()) {
                    Foxglove.getInstance().getLogger().error("Failed to create items log file!");
                }
            } else if (this.containedStacks.isEmpty()) {
                final Scanner scanner = new Scanner(this.itemsLog);
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    if (line.contains(lineIdSplitter)) {
                        final String id = line.split(lineIdSplitter)[0];
                        if (ValidatorUtils.isInteger(id)) {
                            this.containedStacks.add(Integer.parseInt(id));
                        }
                    }
                }
                scanner.close();
            }
        } catch (final IOException ioException) {
            ioException.printStackTrace();
            this.setState(false);
            return;
        }
        DietrichEvents2.global().subscribe(TickEvent.ID, this, Priorities.HIGH);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        this.containedStacks.clear();
    }

    @Override
    public void onTick() {
        final ClientWorld world = mc.world;
        if (world == null) return;
        for (final Entity entity : world.getEntities()) {
            if (entity == mc.player) continue;
            for (final ItemStack stack : entity.getItemsEquipped()) {
                try {
                    this.logStack(entity, stack);
                } catch (final IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private void logStack(final Entity entity, final ItemStack stack) throws IOException {
        final Item item = stack.getItem();
        final NbtCompound tag = stack.getNbt();
        final int damage = stack.getDamage(), nbtCount = tag != null ? tag.getKeys().size() : 0, count = stack.getCount();
        if (item.equals(Items.AIR)) return;
        if (nbtCount == 1 && tag.contains("Damage")) return;
        if (damage == 0 && nbtCount == 0) return;
        final String name = entity instanceof final PlayerEntity player ? "Player: " + player.getGameProfile().getName() : "Entity: " + entity.getName().getString();
        final String logStart = name + " | Position: " + entity.getBlockPos().toShortString() + " | Item: " + item + " | Damage: " + damage + " | Count: " + count + " | NBT Count: " + nbtCount;
        final StringBuilder data = new StringBuilder(logStart + " | NBT: ");
        final String nbt, displayNbt;
        if (tag == null) {
            nbt = "{}";
            displayNbt = "{}";
        } else {
            nbt = NbtHelper.toPrettyPrintedText(tag).getString();
            final NbtCompound copy = tag.copy();
            copy.putString(NBTCommand.displayTitleNbtKey, item + " from " + name.replaceFirst(":", ""));
            displayNbt = NbtHelper.toPrettyPrintedText(copy).getString();
        }
        data.append(nbt).append(" ").append(stack.getCount());
        final String dataString = data.toString();
        final int id = name.hashCode() + item.toString().hashCode() + nbt.hashCode();
        if (this.containedStacks.contains(id)) return;
        final byte[] lineBytes = (id + lineIdSplitter + " [" + formatter.format(new Date()) + "] | Server: " + (ServerUtils.lastServerExists() ? ServerUtils.getLastServerInfo().address : "single player") + " | " + dataString + System.lineSeparator()).getBytes();
        Files.write(Path.of(this.itemsLog.getAbsolutePath()), lineBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        this.containedStacks.add(id);
        final MutableText text = Text.literal("");
        text.append("Found Item Stack from " + logStart);
        final MutableText copyButton = Text.literal(" [Copy]");
        copyButton.setStyle(
                copyButton.getStyle()
                        .withFormatting(Formatting.AQUA)
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
                        .withFormatting(Formatting.LIGHT_PURPLE)
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
                        .withFormatting(Formatting.GREEN)
                        .withClickEvent(
                                new ClickEvent(
                                        ClickEvent.Action.OPEN_FILE,
                                        this.itemsLog.getAbsolutePath()
                                )
                        )
        );
        text.append(copyButton).append(copyGiveCommandButton).append(openFileButton);
        if (tag != null) {
            final MutableText displayNBTButton = Text.literal(" [Display NBT]");
            displayNBTButton.setStyle(
                    displayNBTButton.getStyle()
                            .withFormatting(Formatting.GOLD)
                            .withClickEvent(
                                    Foxglove.getInstance().getCommandRegistry().generateClickEvent("nbt displaynbt " + displayNbt)
                            )
            );
            text.append(displayNBTButton);
        }
        ChatUtils.infoChatMessage(text);
    }

}
