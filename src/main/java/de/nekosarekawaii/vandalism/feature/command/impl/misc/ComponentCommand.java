/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DataResult;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.feature.command.arguments.ComponentMapArgumentType;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.ItemStackUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.component.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ComponentCommand extends Command {

    private static final DynamicCommandExceptionType MALFORMED_ITEM_EXCEPTION = new DynamicCommandExceptionType(
            error -> Text.stringifiedTranslatable("arguments.item.malformed", error)
    );

    public ComponentCommand() {
        super("Allows you to view, copy and modify component data.", Category.MISC, "component", "nbt");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("component", ComponentMapArgumentType.componentMap(REGISTRY_ACCESS)).executes(ctx -> {
            try {
                final ItemStack stack = mc.player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final ComponentMap itemComponents = stack.getComponents();
                    final ComponentMap newComponents = ComponentMapArgumentType.getComponentMap(ctx, "component");
                    final ComponentMap testComponents = ComponentMap.of(itemComponents, newComponents);
                    final DataResult<Unit> dataResult = ItemStack.validateComponents(testComponents);
                    dataResult.getOrThrow(MALFORMED_ITEM_EXCEPTION::create);
                    stack.applyComponentsFrom(testComponents);
                    ItemStackUtil.giveItemStack(stack);
                }
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to add components to the item: " + t);
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("set").then(argument("component", ComponentMapArgumentType.componentMap(REGISTRY_ACCESS)).executes(ctx -> {
            try {
                final ItemStack stack = mc.player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final ComponentMap components = ComponentMapArgumentType.getComponentMap(ctx, "component");
                    final ComponentMapImpl stackComponents = (ComponentMapImpl) stack.getComponents();
                    final DataResult<Unit> dataResult = ItemStack.validateComponents(components);
                    dataResult.getOrThrow(MALFORMED_ITEM_EXCEPTION::create);
                    final ComponentChanges.Builder changesBuilder = ComponentChanges.builder();
                    final Set<ComponentType<?>> types = stackComponents.getTypes();
                    for (final Component<?> entry : components) {
                        changesBuilder.add(entry);
                        types.remove(entry.type());
                    }
                    for (final ComponentType<?> type : types) changesBuilder.remove(type);
                    stackComponents.applyChanges(changesBuilder.build());
                    ItemStackUtil.giveItemStack(stack);
                }
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to set components of the item: " + t);
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("remove").then(argument("component", RegistryKeyArgumentType.registryKey(RegistryKeys.DATA_COMPONENT_TYPE)).executes(ctx -> {
            try {
                final ItemStack stack = mc.player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    @SuppressWarnings("unchecked") final RegistryKey<ComponentType<?>> componentTypeKey = (RegistryKey<ComponentType<?>>) ctx.getArgument("component", RegistryKey.class);
                    final ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(componentTypeKey);
                    final ComponentMapImpl components = (ComponentMapImpl) stack.getComponents();
                    components.applyChanges(ComponentChanges.builder().remove(componentType).build());
                    ItemStackUtil.giveItemStack(stack);
                }
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to remove components from the item: " + t);
            }
            return SINGLE_SUCCESS;
        }).suggests((ctx, suggestionsBuilder) -> {
            final ItemStack stack = mc.player.getInventory().getMainHandStack();
            if (stack != ItemStack.EMPTY) {
                final ComponentMap components = stack.getComponents();
                final String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
                CommandSource.forEachMatching(components.getTypes().stream().map(Registries.DATA_COMPONENT_TYPE::getEntry).toList(), remaining, entry -> {
                    if (entry.getKey().isPresent()) return entry.getKey().get().getValue();
                    return null;
                }, entry -> {
                    final ComponentType<?> dataComponentType = entry.value();
                    if (dataComponentType.getCodec() != null) {
                        if (entry.getKey().isPresent()) {
                            suggestionsBuilder.suggest(entry.getKey().get().getValue().toString());
                        }
                    }
                });
            }
            return suggestionsBuilder.buildFuture();
        })));

        builder.then(literal("view").executes(context -> {
            try {
                final ItemStack stack = mc.player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final DataCommandObject dataCommandObject = new EntityDataObject(mc.player);
                    final NbtPathArgumentType.NbtPath handPath = NbtPathArgumentType.NbtPath.parse("SelectedItem");
                    final MutableText copyButton = Text.literal("Components");
                    copyButton.setStyle(
                            copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(
                                    Vandalism.getInstance().getCommandManager().generateClickEvent(
                                            "component copy"
                                    )
                            ).withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the Component data to your clipboard.")
                            ))
                    );
                    final MutableText text = Text.literal("");
                    text.append(copyButton);
                    try {
                        final List<NbtElement> nbtElement = handPath.get(dataCommandObject.getNbt());
                        if (!nbtElement.isEmpty()) {
                            text.append(" ").append(NbtHelper.toPrettyPrintedText(nbtElement.getFirst()));
                        }
                    } catch (final CommandSyntaxException ignored) {
                        text.append("{}");
                    }
                    ChatUtil.infoChatMessage(text);
                }
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to view components of the item: " + t);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("view-entity").executes(context -> {
            try {
                if (mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
                    final Entity entity = entityHitResult.getEntity();
                    if (entity != null) {
                        final NbtCompound tag = entity.writeNbt(new NbtCompound());
                        final MutableText copyButton = Text.literal("Components");
                        copyButton.setStyle(
                                copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(
                                        Vandalism.getInstance().getCommandManager().generateClickEvent(
                                                "component copy-entity"
                                        )
                                ).withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the Component data to your clipboard.")
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
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to view components from the entity: " + t);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("view-block-entity").executes(context -> {
            try {
                if (mc.crosshairTarget instanceof final BlockHitResult blockHitResult) {
                    final BlockPos pos = blockHitResult.getBlockPos();
                    final BlockEntity blockEntity = mc.world.getBlockEntity(pos);
                    if (blockEntity != null) {
                        final NbtCompound tag = new NbtCompound();
                        blockEntity.writeNbt(tag, mc.player.getRegistryManager());
                        final MutableText copyButton = Text.literal("Components");
                        copyButton.setStyle(
                                copyButton.getStyle().withFormatting(Formatting.UNDERLINE).withClickEvent(
                                        Vandalism.getInstance().getCommandManager().generateClickEvent(
                                                "component copy-block-entity"
                                        )
                                ).withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, Text.literal("Copy the Component data to your clipboard.")
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
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to view components from the block entity: " + t);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            try {
                final ItemStack stack = mc.player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final DataCommandObject dataCommandObject = new EntityDataObject(mc.player);
                    final NbtPathArgumentType.NbtPath handPath = NbtPathArgumentType.NbtPath.parse("SelectedItem");
                    final StringBuilder tag = new StringBuilder();
                    try {
                        final List<NbtElement> nbtElement = handPath.get(dataCommandObject.getNbt());
                        if (!nbtElement.isEmpty()) {
                            tag.append(" ").append(nbtElement.getFirst());
                        }
                    } catch (final CommandSyntaxException ignored) {
                        tag.append("{}");
                    }
                    String tagString = tag.toString();
                    if (tagString.startsWith(" ")) {
                        tagString = tagString.substring(1);
                    }
                    mc.keyboard.setClipboard(tagString);
                    ChatUtil.infoChatMessage("Components copied into the clipboard.");
                }
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to copy components from the item: " + t);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy-entity").executes(context -> {
            try {
                if (mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
                    final Entity entity = entityHitResult.getEntity();
                    if (entity != null) {
                        final NbtCompound tag = entity.writeNbt(new NbtCompound());
                        mc.keyboard.setClipboard(tag.toString());
                        ChatUtil.infoChatMessage("Components copied into the clipboard.");
                        return SINGLE_SUCCESS;
                    }
                }
                ChatUtil.errorChatMessage("You must be looking at an entity to use this command.");
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to copy components from the entity: " + t);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy-block-entity").executes(context -> {
            try {
                if (mc.crosshairTarget instanceof final BlockHitResult blockHitResult) {
                    final BlockPos pos = blockHitResult.getBlockPos();
                    final BlockEntity blockEntity = mc.world.getBlockEntity(pos);
                    if (blockEntity != null) {
                        final NbtCompound tag = new NbtCompound();
                        blockEntity.writeNbt(tag, mc.player.getRegistryManager());
                        mc.keyboard.setClipboard(tag.toString());
                        ChatUtil.infoChatMessage("Components copied into the clipboard.");
                        return SINGLE_SUCCESS;
                    }
                }
                ChatUtil.errorChatMessage("You must be looking at an block entity to use this command.");
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to copy components from the block entity: " + t);
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            try {
                final ItemStack stack = mc.player.getInventory().getMainHandStack();
                if (this.validBasic(stack)) {
                    final int count = IntegerArgumentType.getInteger(context, "count");
                    stack.setCount(count);
                    ItemStackUtil.giveItemStack(stack);
                    ChatUtil.infoChatMessage("Set main hand stack count to " + count + ".");
                }
            } catch (final Throwable t) {
                ChatUtil.errorChatMessage("Failed to set the count of the item: " + t);
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
