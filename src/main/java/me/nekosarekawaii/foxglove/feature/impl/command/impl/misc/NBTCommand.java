package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.feature.impl.command.arguments.NbtCompoundArgumentType;
import me.nekosarekawaii.foxglove.gui.imgui.impl.menu.NBTEditMenu;
import me.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

@CommandInfo(name = "NBT", description = "Modifies NBT data for an item.", aliases = {"nbt", "changenbt", "nbtchange"}, category = FeatureCategory.MISC)
public class NBTCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("nbt", NbtCompoundArgumentType.create()).executes(s -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (validBasic(stack)) {
                    final NbtCompound tag = NbtCompoundArgumentType.get(s);
                    final NbtCompound source = stack.getOrCreateNbt();
                    if (tag != null) {
                        source.copyFrom(tag);
                        this.setStack(stack);
                    } else
                        ChatUtils.errorChatMessage("Some of the NBT data could not be found, try using: " + Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue() + "nbt set {nbt}");
                }
            }
            return singleSuccess;
        })));
        builder.then(literal("set").then(argument("nbt", NbtCompoundArgumentType.create()).executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    stack.setNbt(NbtCompoundArgumentType.get(context));
                    this.setStack(stack);
                }
            }
            return singleSuccess;
        })));
        builder.then(literal("remove").then(argument("nbt_path", NbtPathArgumentType.nbtPath()).executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    context.getArgument("nbt_path", NbtPathArgumentType.NbtPath.class).remove(stack.getNbt());
                }
            }
            return singleSuccess;
        })));
        builder.then(literal("view").executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (stack == null) {
                    ChatUtils.errorChatMessage("You must hold an item in your main hand.");
                } else {
                    final NbtCompound tag = stack.getNbt();
                    final MutableText copyButton = Text.literal("NBT");
                    copyButton.setStyle(copyButton.getStyle()
                            .withFormatting(Formatting.UNDERLINE)
                            .withClickEvent(new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue() + "nbt copy")
                            ).withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Text.literal("Copy the NBT data to your clipboard.")
                            )));
                    final MutableText text = Text.literal("");
                    text.append(copyButton);
                    if (tag == null) text.append("{}");
                    else text.append(" ").append(NbtHelper.toPrettyPrintedText(tag));
                    ChatUtils.infoChatMessage(text);
                }
            }
            return singleSuccess;
        }));
        builder.then(literal("copy").executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (stack == null) {
                    ChatUtils.errorChatMessage("You must hold an item in your main hand.");
                } else {
                    final NbtCompound tag = stack.getOrCreateNbt();
                    mc.keyboard.setClipboard(tag.toString());
                    final MutableText nbt = Text.literal("NBT");
                    nbt.setStyle(nbt.getStyle()
                            .withFormatting(Formatting.UNDERLINE)
                            .withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    NbtHelper.toPrettyPrintedText(tag)
                            )));
                    final MutableText text = Text.literal("");
                    text.append(nbt);
                    text.append(Text.literal(" data copied!"));
                    ChatUtils.infoChatMessage(text);
                }
            }
            return singleSuccess;
        }));
        builder.then(literal("paste").executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    stack.setNbt(new NbtCompoundArgumentType().parse(new StringReader(mc.keyboard.getClipboard())));
                    this.setStack(stack);
                }
            }
            return singleSuccess;
        }));
        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final int count = IntegerArgumentType.getInteger(context, "count");
                    stack.setCount(count);
                    this.setStack(stack);
                    ChatUtils.infoChatMessage("Set main hand stack count to " + count + ".");
                }
            }
            return singleSuccess;
        })));
        builder.then(literal("gui").executes(context -> {
            final ClientPlayerEntity player = mc.player;
            if (player != null) {
                final ItemStack stack = player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    final DataOutputStream out = new DataOutputStream(stream);
                    try {
                        NbtIo.write(stack.getOrCreateNbt(), out);
                        final File file = new File(stack.getName().getString());
                        final NBTEditMenu nbtEditMenu = Foxglove.getInstance().getImGuiHandler().getNbtEditMenu();
                        nbtEditMenu.getMainWindow().dragAndDrop(file, stream.toByteArray());
                        nbtEditMenu.show();
                    } catch (final IOException io) {
                        io.printStackTrace();
                    }
                }
            }
            return singleSuccess;
        }));
    }

    private void setStack(final ItemStack stack) {
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + player.getInventory().selectedSlot, stack));
    }

    private boolean validBasic(final ItemStack stack) {
        final ClientPlayerEntity player = mc.player;
        if (player == null) return false;
        if (!player.getAbilities().creativeMode) {
            ChatUtils.errorChatMessage("Creative mode only.");
            return false;
        }
        if (stack == null || stack.isEmpty()) {
            ChatUtils.errorChatMessage("You must hold an item in your main hand.");
            return false;
        }
        return true;
    }

}
