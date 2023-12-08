package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.NbtCompoundArgumentType;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.nbteditor.NbtEditortImGuiMenu;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ItemStackUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class NbtCommand extends Command {

    public final static String DISPLAY_TITLE_NBT_KEY = UUID.randomUUID().toString();

    public NbtCommand() {
        super("Nbt", "Allows you to view and modify the nbt data from an item stack.", FeatureCategory.MISC, false, "nbt");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("nbt", NbtCompoundArgumentType.create()).executes(s -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final NbtCompound tag = NbtCompoundArgumentType.get(s);
                final NbtCompound source = stack.getOrCreateNbt();
                if (tag != null) {
                    source.copyFrom(tag);
                    ItemStackUtil.giveItemStack(stack);
                } else {
                    ChatUtil.errorChatMessage("Some of the NBT data could not be found, try using: " + Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.commandPrefix.getValue() + "nbt set {nbt}");
                }
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("set").then(argument("nbt", NbtCompoundArgumentType.create()).executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                stack.setNbt(NbtCompoundArgumentType.get(context));
                ItemStackUtil.giveItemStack(stack);
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("remove").then(argument("nbt_path", NbtPathArgumentType.nbtPath()).executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                context.getArgument("nbt_path", NbtPathArgumentType.NbtPath.class).remove(stack.getNbt());
                ItemStackUtil.giveItemStack(stack);
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("view").executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final NbtCompound tag = stack.getNbt();
                final MutableText copyButton = Text.literal("NBT");
                copyButton.setStyle(copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.commandPrefix.getValue() + "nbt copy")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the NBT data to your clipboard."))));
                final MutableText text = Text.literal("");
                text.append(copyButton);
                if (tag == null) text.append("{}");
                else text.append(" ").append(NbtHelper.toPrettyPrintedText(tag));
                ChatUtil.infoChatMessage(text);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final NbtCompound tag = stack.getOrCreateNbt();
                this.keyboard().setClipboard(tag.toString());
                ChatUtil.infoChatMessage("NBT copied into the Clipboard.");
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("paste").executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                stack.setNbt(new NbtCompoundArgumentType().parse(new StringReader(this.keyboard().getClipboard())));
                ItemStackUtil.giveItemStack(stack);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final int count = IntegerArgumentType.getInteger(context, "count");
                stack.setCount(count);
                ItemStackUtil.giveItemStack(stack);
                ChatUtil.infoChatMessage("Set main hand stack count to " + count + ".");
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("gui").executes(context -> {
            final ItemStack stack = this.player().getInventory().getMainHandStack();
            if (this.validBasic(stack))
                Vandalism.getInstance().getImGuiHandler().getImGuiMenuCategoryRegistry().getImGuiMenuByClass(NbtEditortImGuiMenu.class).displayNbt(stack.getName().getString(), stack.getNbt());
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("displaynbt").then(argument("nbt", NbtCompoundArgumentType.create()).executes(context -> {
            final NbtCompound nbt = NbtCompoundArgumentType.get(context);
            final String displayTitle;
            if (nbt.contains(DISPLAY_TITLE_NBT_KEY)) {
                displayTitle = nbt.getString(DISPLAY_TITLE_NBT_KEY);
                nbt.remove(DISPLAY_TITLE_NBT_KEY);
            } else displayTitle = "Nbt";
            Vandalism.getInstance().getImGuiHandler().getImGuiMenuCategoryRegistry().getImGuiMenuByClass(NbtEditortImGuiMenu.class).displayNbt(displayTitle, nbt);
            return SINGLE_SUCCESS;
        })));
    }

    private boolean validBasic(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            ChatUtil.errorChatMessage("You must hold an item in your main hand.");
            return false;
        }
        return true;
    }

}
