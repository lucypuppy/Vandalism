package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import me.nekosarekawaii.foxglove.util.minecraft.FormattingUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SoundPlayerItemsCreativeTab extends CreativeTab {

    private final Collection<ItemStack> soundItems;

    private final List<Item> spawnEggs;

    public SoundPlayerItemsCreativeTab() {
        super(new ItemStack(Items.JUKEBOX).setCustomName(Text.literal("Sound Player Items")));
        this.soundItems = new ArrayList<>();
        this.spawnEggs = new ArrayList<>();
        for (final Item item : Registries.ITEM) {
            if (item.toString().endsWith("_spawn_egg")) {
                this.spawnEggs.add(item);
            }
        }
    }

    private ItemStack generateItem(final Identifier soundIdentifier) {
        final ItemStack item = new ItemStack(this.spawnEggs.get(ThreadLocalRandom.current().nextInt(0, this.spawnEggs.size() - 1)));
        final NbtCompound nbt = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("SoundEvent", soundIdentifier.getPath());
        entityTag.putString("id", "minecraft:arrow");
        nbt.put("EntityTag", entityTag);
        item.setNbt(nbt);
        this.putClientsideName(
                item,
                Text.literal(
                        FormattingUtils.getRandomColor() + soundIdentifier.toString()
                )
        );
        return item;
    }

    @Override
    public Collection<ItemStack> entries() {
        if (this.soundItems.isEmpty()) {
            for (final SoundEvent soundEvent : Registries.SOUND_EVENT) {
                System.out.println(soundEvent.getId().toString());
                this.soundItems.add(this.generateItem(soundEvent.getId()));
            }
        }
        return this.soundItems;
    }

}
