package me.nekosarekawaii.foxglove.creativetab;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.creativetab.impl.ExploitPlayerHeadsCreativeTab;
import me.nekosarekawaii.foxglove.creativetab.impl.GriefSpawnEggsCreativeTab;
import me.nekosarekawaii.foxglove.event.EventPriorities;
import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CreativeTabRegistry implements PacketListener, MinecraftWrapper {

    private final ObjectArrayList<CreativeTab> creativeTabs;
    private final ObjectArrayList<ItemGroup> itemGroups;

    public CreativeTabRegistry() {
        this.creativeTabs = new ObjectArrayList<>();
        this.itemGroups = new ObjectArrayList<>();
        this.registerCreativeTabs(
                new ExploitPlayerHeadsCreativeTab(),
                new GriefSpawnEggsCreativeTab()
        );
    }

    private void registerCreativeTabs(final CreativeTab... creativeTabs) {
        for (final CreativeTab creativeTab : creativeTabs) {
            if (!this.creativeTabs.contains(creativeTab)) {
                this.creativeTabs.add(creativeTab);
            }
        }
    }

    private Text displayNameGen(final CreativeTab creativeTab) {
        final ItemStack icon = creativeTab.getIcon();
        if (icon.hasCustomName()) return icon.getName();
        return Text.literal("Example Displayname");
    }

    public void register(final Item dummyItem) {
        for (int i = 0; i < this.creativeTabs.size(); i++) {
            final CreativeTab creativeTab = this.creativeTabs.get(i);
            final ItemGroup itemGroup = FabricItemGroup.builder()
                    .icon(creativeTab::getIcon)
                    .displayName(this.displayNameGen(creativeTab))
                    .entries((displayContext, entries) -> entries.add(new ItemStack(dummyItem, 1)))
                    .build();
            creativeTab.setItemGroup(itemGroup);
            this.itemGroups.add(itemGroup);
            Registry.register(Registries.ITEM_GROUP, new Identifier(Foxglove.getInstance().getLowerCaseName(), Integer.toString(i)), itemGroup);
        }
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, EventPriorities.HIGH.getPriority());
    }

    public ObjectArrayList<CreativeTab> getCreativeTabs() {
        return this.creativeTabs;
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            if (mc().currentScreen instanceof CreativeInventoryScreen) {
                if (this.itemGroups.contains(CreativeInventoryScreen.selectedTab)) {
                    final ItemStack itemStack = creativeInventoryActionC2SPacket.getItemStack();
                    final NbtCompound nbt = itemStack.getNbt();
                    if (nbt != null) {
                        if (itemStack.hasCustomName() && nbt.contains("clientsideName")) {
                            itemStack.removeCustomName();
                            nbt.remove("clientsideName");
                        }
                        if (nbt.contains("clientsideGlint")) nbt.remove("clientsideGlint");
                    }
                }
            }
        }
    }

}
