package me.nekosarekawaii.foxglove.creativetab.impl;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Collection;

public class TrollItemsCreativeTab extends CreativeTab {

    public TrollItemsCreativeTab() {
        super(new ItemStack(Items.END_CRYSTAL).setCustomName(Text.literal("Troll Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        final VersionEnum targetVersion = ProtocolHack.getTargetVersion();

        final NbtCompound killPotionEffectNbt = new NbtCompound();
        killPotionEffectNbt.putInt("Amplifier", 125);
        killPotionEffectNbt.putInt("Duration", 2000);
        killPotionEffectNbt.putInt("Id", 6);
        final NbtList killPotionEffectList = new NbtList();
        killPotionEffectList.add(killPotionEffectNbt);
        final NbtCompound killPotionNbt = new NbtCompound();
        killPotionNbt.put("CustomPotionEffects", killPotionEffectList);

        final ItemStack killPotion = new ItemStack(Items.POTION);
        killPotion.setNbt(killPotionNbt);
        this.putClientsideName(
                killPotion,
                Text.literal(Formatting.RED + "Kill Potion")
        );
        current.add(killPotion);

        final ItemStack killPotionSplash = new ItemStack(Items.SPLASH_POTION);
        killPotionSplash.setNbt(killPotionNbt);
        this.putClientsideName(
                killPotionSplash,
                Text.literal(Formatting.RED + "Kill Potion")
        );
        current.add(killPotionSplash);

        final ItemStack killPotionLingering = new ItemStack(Items.LINGERING_POTION);
        killPotionLingering.setNbt(killPotionNbt);
        this.putClientsideName(
                killPotionLingering,
                Text.literal(Formatting.RED + "Kill Potion")
        );
        current.add(killPotionLingering);

        final ItemStack killArea = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound killAreaNbt = new NbtCompound();
        final NbtCompound killAreaEntityTag = new NbtCompound();
        final NbtList killAreaEffects = new NbtList();
        final NbtCompound killAreaEffect = new NbtCompound();
        killAreaEffect.putInt("Id", 6);
        killAreaEffect.putByte("ShowParticles", (byte) 0);
        killAreaEffect.putInt("Duration", 20);
        killAreaEffect.putByte("Amplifier", (byte) 125);
        killAreaEffects.add(killAreaEffect);
        killAreaEntityTag.put("Effects", killAreaEffects);
        killAreaEntityTag.putFloat("RadiusOnUse", 0.1f);
        killAreaEntityTag.putFloat("RadiusPerTick", 0.01f);
        killAreaEntityTag.putInt("Duration", 20000);
        killAreaEntityTag.putFloat("Radius", 100f);
        killAreaEntityTag.putInt("ReapplicationDelay", 40);
        killAreaEntityTag.putString("Particle", "underwater");
        killAreaEntityTag.putString("id", "minecraft:area_effect_cloud");
        killAreaNbt.put("EntityTag", killAreaEntityTag);
        killArea.setNbt(killAreaNbt);
        this.putClientsideName(killArea,
                Text.literal(
                        Formatting.RED + "Kill Area"
                )
        );
        current.add(killArea);

        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            final ItemStack blackHoleSpawnEgg = new ItemStack(Items.BAT_SPAWN_EGG);
            final NbtCompound blackHoleSpawnEggNBT = new NbtCompound();
            final NbtCompound blackHoleSpawnEggEntityTag = new NbtCompound();
            blackHoleSpawnEggEntityTag.putByte("shadow", (byte) 1);
            blackHoleSpawnEggEntityTag.putFloat("shadow_strength", 10000000f);
            blackHoleSpawnEggEntityTag.putFloat("shadow_radius", 10000000f);
            blackHoleSpawnEggEntityTag.putFloat("view_range", 10000000f);
            blackHoleSpawnEggEntityTag.putString("id", "minecraft:text_display");
            blackHoleSpawnEggNBT.put("EntityTag", blackHoleSpawnEggEntityTag);
            blackHoleSpawnEgg.setNbt(blackHoleSpawnEggNBT);
            this.putClientsideName(blackHoleSpawnEgg,
                    Text.literal(
                            Formatting.RED + Formatting.BOLD.toString() + "Black Hole"
                    )
            );
            current.add(blackHoleSpawnEgg);
        }

        final ItemStack eventHorizonArea = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound eventHorizonAreaNbt = new NbtCompound();
        final NbtCompound eventHorizonAreaEntityTag = new NbtCompound();
        final NbtList eventHorizonEffects = new NbtList();
        final NbtCompound eventHorizonEffect1 = new NbtCompound();
        eventHorizonEffect1.putInt("Id", 2);
        eventHorizonEffect1.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect1.putInt("Duration", 170);
        eventHorizonEffect1.putByte("Amplifier", (byte) 125);
        eventHorizonEffects.add(eventHorizonEffect1);
        final NbtCompound eventHorizonEffect2 = new NbtCompound();
        eventHorizonEffect2.putInt("Id", 4);
        eventHorizonEffect2.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect2.putInt("Duration", 150);
        eventHorizonEffect2.putByte("Amplifier", (byte) 125);
        eventHorizonEffects.add(eventHorizonEffect2);
        final NbtCompound eventHorizonEffect3 = new NbtCompound();
        eventHorizonEffect3.putInt("Id", 11);
        eventHorizonEffect3.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect3.putInt("Duration", 170);
        eventHorizonEffect3.putByte("Amplifier", (byte) 125);
        eventHorizonEffects.add(eventHorizonEffect3);
        final NbtCompound eventHorizonEffect4 = new NbtCompound();
        eventHorizonEffect4.putInt("Id", 14);
        eventHorizonEffect4.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect4.putInt("Duration", 130);
        eventHorizonEffect4.putByte("Amplifier", (byte) 1);
        eventHorizonEffects.add(eventHorizonEffect4);
        final NbtCompound eventHorizonEffect5 = new NbtCompound();
        eventHorizonEffect5.putInt("Id", 18);
        eventHorizonEffect5.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect5.putInt("Duration", 170);
        eventHorizonEffect5.putByte("Amplifier", (byte) 125);
        eventHorizonEffects.add(eventHorizonEffect5);
        final NbtCompound eventHorizonEffect6 = new NbtCompound();
        eventHorizonEffect6.putInt("Id", 20);
        eventHorizonEffect6.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect6.putInt("Duration", 160);
        eventHorizonEffect6.putByte("Amplifier", (byte) 1);
        eventHorizonEffects.add(eventHorizonEffect6);
        final NbtCompound eventHorizonEffect7 = new NbtCompound();
        eventHorizonEffect7.putInt("Id", 25);
        eventHorizonEffect7.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect7.putInt("Duration", 19);
        eventHorizonEffect7.putByte("Amplifier", (byte) 125);
        eventHorizonEffects.add(eventHorizonEffect7);
        final NbtCompound eventHorizonEffect8 = new NbtCompound();
        eventHorizonEffect8.putInt("Id", 33);
        eventHorizonEffect8.putByte("ShowParticles", (byte) 0);
        eventHorizonEffect8.putInt("Duration", 170);
        eventHorizonEffect8.putByte("Amplifier", (byte) 125);
        eventHorizonEffects.add(eventHorizonEffect8);
        eventHorizonAreaEntityTag.put("Effects", eventHorizonEffects);
        eventHorizonAreaEntityTag.putFloat("RadiusOnUse", 0.1f);
        eventHorizonAreaEntityTag.putFloat("RadiusPerTick", 0.01f);
        eventHorizonAreaEntityTag.putInt("Duration", 20000);
        eventHorizonAreaEntityTag.putFloat("Radius", 100f);
        eventHorizonAreaEntityTag.putInt("ReapplicationDelay", 40);
        eventHorizonAreaEntityTag.putString("Particle", "item air");
        eventHorizonAreaEntityTag.putString("id", "minecraft:area_effect_cloud");
        eventHorizonAreaNbt.put("EntityTag", eventHorizonAreaEntityTag);
        eventHorizonArea.setNbt(eventHorizonAreaNbt);
        this.putClientsideName(eventHorizonArea,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area"
                )
        );
        current.add(eventHorizonArea);

        final ItemStack consoleErrorEntity = new ItemStack(Items.HORSE_SPAWN_EGG);
        final NbtCompound consoleErrorEntityNbt = new NbtCompound();
        final NbtCompound consoleErrorEntityTagNbt = new NbtCompound();
        consoleErrorEntityTagNbt.putByte("pickup", (byte) 3);
        consoleErrorEntityTagNbt.putString("id", "minecraft:arrow");
        consoleErrorEntityNbt.put("EntityTag", consoleErrorEntityTagNbt);
        consoleErrorEntity.setNbt(consoleErrorEntityNbt);
        this.putClientsideName(consoleErrorEntity,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Console Error Entity"
                ),
                Text.literal(
                        Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Works on Scissors"
                )
        );
        current.add(consoleErrorEntity);

        final ItemStack consoleErrorHead = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound consoleErrorHeadNbt = new NbtCompound();
        final NbtCompound consoleErrorHeadSkullOwner = new NbtCompound();
        consoleErrorHeadSkullOwner.putIntArray("Id", new int[]{1, 2, 3, 4});
        consoleErrorHeadSkullOwner.putString("Name", " ");
        consoleErrorHeadNbt.put("SkullOwner", consoleErrorHeadSkullOwner);
        consoleErrorHead.setNbt(consoleErrorHeadNbt);
        this.putClientsideName(consoleErrorHead,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Console Error Head"
                ),
                Text.literal(
                        Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Works on Scissors"
                )
        );
        current.add(consoleErrorHead);

        return current;
    }

}
