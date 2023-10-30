package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.NbtCompoundArgumentType;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.widget.NBTEditWidget;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
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
import java.util.UUID;

public class NBTCommand extends Command {

    public final static String DISPLAY_TITLE_NBT_KEY = UUID.randomUUID().toString();

    public NBTCommand() {
        super(
                "NBT",
                "Allows you to view and modify the nbt data from an item stack.",
                FeatureCategory.MISC,
                false,
                "nbt"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("nbt", NbtCompoundArgumentType.create()).executes(s -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final NbtCompound tag = NbtCompoundArgumentType.get(s);
                    final NbtCompound source = stack.getOrCreateNbt();
                    if (tag != null) {
                        source.copyFrom(tag);
                        this.setStack(stack);
                    } else {
                        ChatUtil.errorChatMessage(
                                "Some of the NBT data could not be found, try using: " +
                                        Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.commandPrefix.getValue() + "nbt set {nbt}"
                        );
                    }
                }
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("set").then(argument("nbt", NbtCompoundArgumentType.create()).executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    stack.setNbt(NbtCompoundArgumentType.get(context));
                    this.setStack(stack);
                }
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("remove").then(argument("nbt_path", NbtPathArgumentType.nbtPath()).executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    context.getArgument("nbt_path", NbtPathArgumentType.NbtPath.class).remove(stack.getNbt());
                }
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("view").executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (stack == null) {
                    ChatUtil.errorChatMessage("You must hold an item in your main hand.");
                } else {
                    final NbtCompound tag = stack.getNbt();
                    final MutableText copyButton = Text.literal("NBT");
                    copyButton.setStyle(copyButton.getStyle()
                            .withFormatting(Formatting.UNDERLINE)
                            .withClickEvent(new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.commandPrefix.getValue() + "nbt copy")
                            ).withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Text.literal("Copy the NBT data to your clipboard.")
                            )));
                    final MutableText text = Text.literal("");
                    text.append(copyButton);
                    if (tag == null) text.append("{}");
                    else text.append(" ").append(NbtHelper.toPrettyPrintedText(tag));
                    ChatUtil.infoChatMessage(text);
                }
            }
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("copy").executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (stack == null) {
                    ChatUtil.errorChatMessage("You must hold an item in your main hand.");
                } else {
                    final NbtCompound tag = stack.getOrCreateNbt();
                    keyboard().setClipboard(tag.toString());
                    ChatUtil.infoChatMessage("NBT copied into the Clipboard.");
                }
            }
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("paste").executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    stack.setNbt(new NbtCompoundArgumentType().parse(new StringReader(keyboard().getClipboard())));
                    this.setStack(stack);
                }
            }
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final int count = IntegerArgumentType.getInteger(context, "count");
                    stack.setCount(count);
                    this.setStack(stack);
                    ChatUtil.infoChatMessage("Set main hand stack count to " + count + ".");
                }
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("gui").executes(context -> {
            if (player() != null) {
                final ItemStack stack = player().getInventory().getMainHandStack();
                if (stack == null) {
                    ChatUtil.errorChatMessage("You must hold an item in your main hand.");
                } else {
                    try {
                        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        final DataOutputStream out = new DataOutputStream(stream);
                        NbtIo.write(stack.getOrCreateNbt(), out);
                        final NBTEditWidget nbtEditWidget = Vandalism.getInstance().getImGuiHandler().getNbtEditWidget();
                        nbtEditWidget.getMainWindow().dragAndDrop(new File(stack.getName().getString()), stream.toByteArray());
                        nbtEditWidget.show();
                    } catch (final IOException io) {
                        Vandalism.getInstance().getLogger().error("Failed to open ImNbt Gui.", io);
                    }
                }
            }
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("displaynbt").then(argument("nbt", NbtCompoundArgumentType.create())
                .executes(context -> {
                            try {
                                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                final DataOutputStream out = new DataOutputStream(stream);
                                final NbtCompound nbt = NbtCompoundArgumentType.get(context);
                                final String displayTitle;
                                if (nbt.contains(DISPLAY_TITLE_NBT_KEY)) {
                                    displayTitle = nbt.getString(DISPLAY_TITLE_NBT_KEY);
                                    nbt.remove(DISPLAY_TITLE_NBT_KEY);
                                } else displayTitle = "NBT";
                                NbtIo.write(nbt, out);
                                final NBTEditWidget nbtEditWidget = Vandalism.getInstance().getImGuiHandler().getNbtEditWidget();
                                nbtEditWidget.getMainWindow().dragAndDrop(new File(displayTitle), stream.toByteArray());
                                nbtEditWidget.show();
                            } catch (final IOException io) {
                                Vandalism.getInstance().getLogger().error("Failed to open ImNbt Gui.", io);
                            }
                    return SINGLE_SUCCESS;
                        }
                )));
    }

    private void setStack(final ItemStack stack) {
        if (player() == null) return;
        networkHandler().sendPacket(new CreativeInventoryActionC2SPacket(36 + player().getInventory().selectedSlot, stack));
    }

    private boolean validBasic(final ItemStack stack) {
        if (player() == null) return false;
        if (!player().getAbilities().creativeMode) {
            ChatUtil.errorChatMessage("Creative mode only.");
            return false;
        }
        if (stack == null || stack.isEmpty()) {
            ChatUtil.errorChatMessage("You must hold an item in your main hand.");
            return false;
        }
        return true;
    }

}
