package de.vandalismdevelopment.vandalism.integration.creativetab;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.integration.creativetab.impl.CrashItemsCreativeTab;
import de.vandalismdevelopment.vandalism.integration.creativetab.impl.GriefItemsCreativeTab;
import de.vandalismdevelopment.vandalism.integration.creativetab.impl.KickItemsCreativeTab;
import de.vandalismdevelopment.vandalism.integration.creativetab.impl.TrollItemsCreativeTab;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.UUID;

public class CreativeTabManager extends Storage<AbstractCreativeTab> implements PacketListener {

    public static final String CLIENTSIDE_NAME = UUID.randomUUID().toString();
    public static final String CLIENTSIDE_GLINT = UUID.randomUUID().toString();

    public CreativeTabManager() {
        this.setAddConsumer(AbstractCreativeTab::publish);
    }
    
    @Override
    public void init() {
        this.add(
                new CrashItemsCreativeTab(),
                new KickItemsCreativeTab(),
                new GriefItemsCreativeTab(),
                new TrollItemsCreativeTab()
        );
        
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            final var itemStack = creativeInventoryActionC2SPacket.getStack();
            final var nbt = itemStack.getNbt();

            if (nbt != null) {
                if (nbt.contains(CLIENTSIDE_NAME)) {
                    final var display = itemStack.getSubNbt(ItemStack.DISPLAY_KEY);
                    if (display != null) {
                        display.remove(ItemStack.NAME_KEY);
                        display.remove(ItemStack.LORE_KEY);
                        if (display.isEmpty()) {
                            itemStack.removeSubNbt(ItemStack.DISPLAY_KEY);
                        }
                    }
                    nbt.remove(CLIENTSIDE_NAME);
                }
                if (nbt.contains(CLIENTSIDE_GLINT)) {
                    nbt.remove(CLIENTSIDE_GLINT);
                }
            }
        }
    }
    
}
