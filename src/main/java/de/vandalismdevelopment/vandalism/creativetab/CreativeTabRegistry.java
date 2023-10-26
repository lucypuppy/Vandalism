package de.vandalismdevelopment.vandalism.creativetab;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.creativetab.impl.CrashItemsCreativeTab;
import de.vandalismdevelopment.vandalism.creativetab.impl.GriefItemsCreativeTab;
import de.vandalismdevelopment.vandalism.creativetab.impl.KickItemsCreativeTab;
import de.vandalismdevelopment.vandalism.creativetab.impl.TrollItemsCreativeTab;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreativeTabRegistry implements PacketListener, MinecraftWrapper {

    public final static String CLIENTSIDE_NAME = UUID.randomUUID().toString(), CLIENTSIDE_GLINT = UUID.randomUUID().toString();

    private final List<CreativeTab> creativeTabs;
    private final List<ItemGroup> itemGroups;

    public CreativeTabRegistry() {
        this.itemGroups = new ArrayList<>();
        this.creativeTabs = new ArrayList<>();
        this.registerCreativeTabs(
                new CrashItemsCreativeTab(),
                new KickItemsCreativeTab(),
                new GriefItemsCreativeTab(),
                new TrollItemsCreativeTab()
        );
    }

    private void registerCreativeTabs(final CreativeTab... creativeTabs) {
        for (int i = 0; i < creativeTabs.length; i++) {
            final CreativeTab creativeTab = creativeTabs[i];
            if (!this.creativeTabs.contains(creativeTab)) {
                this.creativeTabs.add(creativeTab);
                final ItemGroup itemGroup = FabricItemGroup.builder()
                        .icon(creativeTab::getIcon)
                        .displayName(creativeTab.getDisplayName())
                        .build();
                creativeTab.setItemGroup(itemGroup);
                this.itemGroups.add(itemGroup);
                Registry.register(Registries.ITEM_GROUP, new Identifier(
                                Vandalism.getInstance().getId(),
                                Integer.toString(i)),
                        itemGroup
                );
            }
        }
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            if (currentScreen() instanceof CreativeInventoryScreen) {
                if (this.itemGroups.contains(CreativeInventoryScreen.selectedTab)) {
                    final ItemStack itemStack = creativeInventoryActionC2SPacket.getStack();
                    final NbtCompound nbt = itemStack.getNbt();
                    if (nbt != null) {
                        if (nbt.contains(CLIENTSIDE_NAME)) {
                            final NbtCompound display = itemStack.getSubNbt(ItemStack.DISPLAY_KEY);
                            if (display != null) {
                                display.remove(ItemStack.NAME_KEY);
                                display.remove(ItemStack.LORE_KEY);
                                if (display.isEmpty()) itemStack.removeSubNbt(ItemStack.DISPLAY_KEY);
                            }
                            nbt.remove(CLIENTSIDE_NAME);
                        }
                        if (nbt.contains(CLIENTSIDE_GLINT)) nbt.remove(CLIENTSIDE_GLINT);
                    }
                }
            }
        }
    }

    public List<CreativeTab> getCreativeTabs() {
        return this.creativeTabs;
    }

}