package me.nekosarekawaii.foxglove.creativetab;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.creativetab.impl.ExploitCreativeTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CreativeTabRegistry {

    private final ObjectArrayList<CreativeTab> creativeTabs;

    public CreativeTabRegistry() {
        this.creativeTabs = new ObjectArrayList<>();
        this.registerCreativeTabs(
                new ExploitCreativeTab()
        );
    }

    private void registerCreativeTabs(final CreativeTab... creativeTabs) {
        for (final CreativeTab creativeTab : creativeTabs) {
            if (!this.creativeTabs.contains(creativeTab)) {
                this.creativeTabs.add(creativeTab);
            }
        }
    }

    public void register(final Item dummyItem) {
        for (int i = 0; i < this.creativeTabs.size(); i++) {
            final CreativeTab creativeTab = this.creativeTabs.get(i);
            final ItemGroup itemGroup = creativeTab.generate(dummyItem);
            creativeTab.setItemGroup(itemGroup);
            Registry.register(Registries.ITEM_GROUP, new Identifier(Foxglove.getInstance().getLowerCaseName(), Integer.toString(i)), itemGroup);
        }
    }

    public ObjectArrayList<CreativeTab> getCreativeTabs() {
        return this.creativeTabs;
    }

}
