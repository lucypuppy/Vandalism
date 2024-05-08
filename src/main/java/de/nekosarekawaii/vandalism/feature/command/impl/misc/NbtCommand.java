/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.ChatSettings;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.NbtCompoundArgumentType;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.ItemStackUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class NbtCommand extends AbstractCommand {

    public static final String DISPLAY_TITLE_NBT_KEY = UUID.randomUUID().toString();

    public NbtCommand() {
        super("Allows you to view and modify the nbt data from an item stack.", Category.MISC, "nbt");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        builder.then(literal("add").then(argument("nbt", NbtCompoundArgumentType.create()).executes(s -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final NbtCompound tag = NbtCompoundArgumentType.get(s);
                final NbtCompound source = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                if (tag != null) {
                    source.copyFrom(tag);
                    ItemStackUtil.giveItemStack(stack);
                } else {
                    ChatUtil.errorChatMessage(
                            "Some of the NBT data could not be found, try using: " +
                                    chatSettings.commandPrefix.getValue() + "nbt set {nbt}"
                    );
                }
            }
            return SINGLE_SUCCESS;
        })));

        // TODO: Fix
       /* builder.then(literal("set").then(argument("nbt", NbtCompoundArgumentType.create()).executes(context -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                stack.setNbt(NbtCompoundArgumentType.get(context));
                ItemStackUtil.giveItemStack(stack);
            }
            return SINGLE_SUCCESS;
        })));     */

        builder.then(literal("remove").then(argument("nbt_path", NbtPathArgumentType.nbtPath()).executes(context -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                context.getArgument("nbt_path", NbtPathArgumentType.NbtPath.class).remove(stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt());
                ItemStackUtil.giveItemStack(stack);
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("view").executes(context -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final NbtCompound tag = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                final MutableText copyButton = Text.literal("NBT");
                copyButton.setStyle(
                        copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, chatSettings.commandPrefix.getValue() + "nbt copy")
                        ).withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the NBT data to your clipboard.")
                        ))
                );
                final MutableText text = Text.literal("");
                text.append(copyButton);
                if (tag == null) text.append("{}");
                else text.append(" ").append(NbtHelper.toPrettyPrintedText(tag));
                ChatUtil.infoChatMessage(text);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("view-entity").executes(context -> {
            if (this.mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
                final Entity entity = entityHitResult.getEntity();
                if (entity != null) {
                    final NbtCompound tag = entity.writeNbt(new NbtCompound());
                    final MutableText copyButton = Text.literal("NBT");
                    copyButton.setStyle(
                            copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, chatSettings.commandPrefix.getValue() + "nbt copy-entity")
                            ).withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the NBT data to your clipboard.")
                            ))
                    );
                    final MutableText text = Text.literal("");
                    text.append(copyButton);
                    if (tag == null) text.append("{}");
                    else text.append(" ").append(NbtHelper.toPrettyPrintedText(tag));
                    ChatUtil.infoChatMessage(text);
                    return SINGLE_SUCCESS;
                }
            }
            ChatUtil.errorChatMessage("You must be looking at an entity to use this command.");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("view-block-entity").executes(context -> {
            if (this.mc.crosshairTarget instanceof final BlockHitResult blockHitResult) {
                final BlockPos pos = blockHitResult.getBlockPos();
                final BlockEntity blockEntity = mc.world.getBlockEntity(pos);
                if (blockEntity != null) {
                    final NbtCompound tag = new NbtCompound();
                    // TODO: Fix
                    //blockEntity.writeNbt(tag);
                    final MutableText copyButton = Text.literal("NBT");
                    copyButton.setStyle(
                            copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, chatSettings.commandPrefix.getValue() + "nbt copy-block-entity")
                            ).withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the NBT data to your clipboard.")
                            ))
                    );
                    final MutableText text = Text.literal("");
                    text.append(copyButton);
                    text.append(" ").append(NbtHelper.toPrettyPrintedText(tag));
                    ChatUtil.infoChatMessage(text);
                    return SINGLE_SUCCESS;
                }
            }
            ChatUtil.errorChatMessage("You must be looking at an block entity to use this command.");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final NbtCompound tag = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                this.mc.keyboard.setClipboard(tag.toString());
                ChatUtil.infoChatMessage("NBT copied into the clipboard.");
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy-entity").executes(context -> {
            if (this.mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
                final Entity entity = entityHitResult.getEntity();
                if (entity != null) {
                    final NbtCompound tag = entity.writeNbt(new NbtCompound());
                    this.mc.keyboard.setClipboard(tag.toString());
                    ChatUtil.infoChatMessage("NBT copied into the clipboard.");
                    return SINGLE_SUCCESS;
                }
            }
            ChatUtil.errorChatMessage("You must be looking at an entity to use this command.");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy-block-entity").executes(context -> {
            if (this.mc.crosshairTarget instanceof final BlockHitResult blockHitResult) {
                final BlockPos pos = blockHitResult.getBlockPos();
                final BlockEntity blockEntity = mc.world.getBlockEntity(pos);
                if (blockEntity != null) {
                    final NbtCompound tag = new NbtCompound();
                    // TODO: Fix
                    //blockEntity.writeNbt(tag);
                    this.mc.keyboard.setClipboard(tag.toString());
                    ChatUtil.infoChatMessage("NBT copied into the clipboard.");
                    return SINGLE_SUCCESS;
                }
            }
            ChatUtil.errorChatMessage("You must be looking at an block entity to use this command.");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("paste").executes(context -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(new NbtCompoundArgumentType().parse(new StringReader(this.mc.keyboard.getClipboard()))));
                ItemStackUtil.giveItemStack(stack);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
            if (this.validBasic(stack)) {
                final int count = IntegerArgumentType.getInteger(context, "count");
                stack.setCount(count);
                ItemStackUtil.giveItemStack(stack);
                ChatUtil.infoChatMessage("Set main hand stack count to " + count + ".");
            }
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
