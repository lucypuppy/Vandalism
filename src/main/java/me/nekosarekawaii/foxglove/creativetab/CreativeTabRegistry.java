package me.nekosarekawaii.foxglove.creativetab;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.creativetab.impl.*;
import me.nekosarekawaii.foxglove.event.PacketListener;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.MinecraftClient;
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

import java.util.UUID;

public class CreativeTabRegistry implements PacketListener {

    private final UUID clientsideName, clientsideGlint;

    private final ObjectArrayList<CreativeTab> creativeTabs;
    private final ObjectArrayList<ItemGroup> itemGroups;

    public CreativeTabRegistry() {
        this.creativeTabs = new ObjectArrayList<>();
        this.itemGroups = new ObjectArrayList<>();
        this.clientsideName = UUID.randomUUID();
        this.clientsideGlint = UUID.randomUUID();
        this.registerCreativeTabs(
                new TestItemsCreativeTab(),
                new CrashItemsCreativeTab(),
                new KickItemsCreativeTab(),
                new GriefItemsCreativeTab(),
                new TrollItemsCreativeTab(),
                new SoundPlayerItemsCreativeTab()
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
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.HIGH);
    }

    public ObjectArrayList<CreativeTab> getCreativeTabs() {
        return this.creativeTabs;
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            if (MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) {
                if (this.itemGroups.contains(CreativeInventoryScreen.selectedTab)) {
                    final ItemStack itemStack = creativeInventoryActionC2SPacket.getItemStack();
                    final NbtCompound nbt = itemStack.getNbt();
                    if (nbt != null) {
                        if (nbt.contains(this.getClientsideName())) {
                            final NbtCompound display = itemStack.getSubNbt(ItemStack.DISPLAY_KEY);
                            if (display != null) {
                                display.remove(ItemStack.NAME_KEY);
                                display.remove(ItemStack.LORE_KEY);
                                if (display.isEmpty()) itemStack.removeSubNbt(ItemStack.DISPLAY_KEY);
                            }
                            nbt.remove(this.getClientsideName());
                        }
                        if (nbt.contains(this.getClientsideGlint())) nbt.remove(this.getClientsideGlint());
                    }
                }
            }
        }
    }

    public String getClientsideName() {
        return this.clientsideName.toString();
    }

    public String getClientsideGlint() {
        return this.clientsideGlint.toString();
    }

}
