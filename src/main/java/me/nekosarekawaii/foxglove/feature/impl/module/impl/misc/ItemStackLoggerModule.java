package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import com.mojang.datafixers.util.Pair;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.EventPriorities;
import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import me.nekosarekawaii.foxglove.util.minecraft.ServerUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@ModuleInfo(name = "Item Stack Logger", description = "Logs incoming player item stacks into the chat and writes a complete log into a log file.", category = FeatureCategory.MISC)
public class ItemStackLoggerModule extends Module implements PacketListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, EventPriorities.HIGH.getPriority());
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    private final static DateFormat formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");

    @Override
    public void onRead(final PacketEvent event) {
        final ClientWorld world = mc.world;
        if (event.isCancelled() || world == null) return;
        if (event.packet instanceof final EntityEquipmentUpdateS2CPacket entityEquipmentUpdateS2CPacket) {
            try {
                final File itemsLog = new File(Foxglove.getInstance().getDir(), "items.log");
                final List<String> log = new ArrayList<>();
                if (!itemsLog.exists()) {
                    if (itemsLog.createNewFile()) {
                        Foxglove.getInstance().getLogger().error("Failed to create items log file!");
                    }
                } else {
                    final Scanner scanner = new Scanner(itemsLog);
                    while (scanner.hasNextLine()) log.add(scanner.nextLine());
                    scanner.close();
                }
                for (final Pair<EquipmentSlot, ItemStack> equipmentSlotItemStackPair : entityEquipmentUpdateS2CPacket.getEquipmentList()) {
                    final Entity entity = world.getEntityById(entityEquipmentUpdateS2CPacket.getId());
                    if (entity instanceof final PlayerEntity player) {
                        final ItemStack stack = equipmentSlotItemStackPair.getSecond();
                        final Item item = stack.getItem();
                        final NbtCompound tag = stack.getNbt();
                        final int damage = stack.getDamage(), nbtCount = tag != null ? tag.getKeys().size() : 0;
                        if (item.equals(Items.AIR)) continue;
                        if (nbtCount == 1 && tag.contains("Damage")) continue;
                        if (damage == 0 && nbtCount == 0) continue;
                        final String logStart = "Player: " + player.getGameProfile().getName() + " | Item: " + item + " | Damage: " + damage + " | Count: " + stack.getCount() + " | NBT Count: " + nbtCount;
                        final StringBuilder data = new StringBuilder(logStart + " | NBT: ");
                        if (tag == null) data.append("{}");
                        else data.append(NbtHelper.toPrettyPrintedText(tag).getString());
                        data.append(" ").append(stack.getCount());
                        final String dataString = data.toString();
                        log.add("[" + formatter.format(new Date()) + "] | Server: " + (ServerUtils.lastServerExists() ? ServerUtils.getLastServerInfo().address : "single player") + " | " + dataString);
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
                        final MutableText openFileButton = Text.literal(" [Open File]");
                        openFileButton.setStyle(
                                openFileButton.getStyle()
                                        .withFormatting(Formatting.GREEN)
                                        .withClickEvent(
                                                new ClickEvent(
                                                        ClickEvent.Action.OPEN_FILE,
                                                        itemsLog.getAbsolutePath()
                                                )
                                        )
                        );
                        final MutableText text = Text.literal("");
                        text.append("Found Item Stack from " + logStart);
                        text.append(copyButton);
                        text.append(openFileButton);
                        ChatUtils.infoChatMessage(text);
                    }
                }
                final PrintWriter printWriter = new PrintWriter(itemsLog);
                for (final String line : log) printWriter.println(line);
                printWriter.close();
            } catch (final IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
