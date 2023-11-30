package de.vandalismdevelopment.vandalism.creativetab;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.creativetab.impl.CrashItemsCreativeTab;
import de.vandalismdevelopment.vandalism.creativetab.impl.GriefItemsCreativeTab;
import de.vandalismdevelopment.vandalism.creativetab.impl.KickItemsCreativeTab;
import de.vandalismdevelopment.vandalism.creativetab.impl.TrollItemsCreativeTab;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class CreativeTabRegistry implements PacketListener {

    public final static String CLIENTSIDE_NAME = UUID.randomUUID().toString();
    public final static String CLIENTSIDE_GLINT = UUID.randomUUID().toString();

    public CreativeTabRegistry() {
        this.registerCreativeTabs(
                new CrashItemsCreativeTab(),
                new KickItemsCreativeTab(),
                new GriefItemsCreativeTab(),
                new TrollItemsCreativeTab()
        );
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.HIGH);
    }

    private void registerCreativeTabs(final CreativeTab... creativeTabs) {
        for (int i = 0; i < creativeTabs.length; i++) {
            Registry.register(
                    Registries.ITEM_GROUP,
                    new Identifier(Vandalism.getInstance().getId(), String.valueOf(i)),
                    creativeTabs[i].getItemGroup()
            );
        }
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
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