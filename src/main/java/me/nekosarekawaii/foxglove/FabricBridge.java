package me.nekosarekawaii.foxglove;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FabricBridge implements ClientModInitializer {

    private final static Item dummyItem = new Item(new FabricItemSettings());

    @Override
    public void onInitializeClient() {
        Registry.register(Registries.ITEM, new Identifier(Foxglove.getInstance().getLowerCaseName(), "dummy"), dummyItem);
        Foxglove.getInstance().getCreativeTabRegistry().register(dummyItem);
    }

}
