package de.nekosarekawaii.vandalism.feature.creativetab;

import de.florianmichael.dietrichevents2.Priorities;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.CrashItemsCreativeTab;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.GriefItemsCreativeTab;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.KickItemsCreativeTab;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.TrollItemsCreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;

import java.util.UUID;

public class CreativeTabManager extends Storage<AbstractCreativeTab> implements OutgoingPacketListener {

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
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            final ItemStack itemStack = creativeInventoryActionC2SPacket.getStack();
            final NbtCompound nbt = itemStack.getNbt();
            if (nbt != null) {
                if (nbt.contains(CLIENTSIDE_NAME)) {
                    final NbtCompound display = itemStack.getSubNbt(ItemStack.DISPLAY_KEY);
                    if (display != null) {
                        display.remove(ItemStack.NAME_KEY);
                        display.remove(ItemStack.LORE_KEY);
                        if (display.isEmpty()) {
                            itemStack.removeSubNbt(ItemStack.DISPLAY_KEY);
                        }
                    }
                    itemStack.setCustomName(Text.Serialization.fromJson(nbt.getString(CLIENTSIDE_NAME)));
                    nbt.remove(CLIENTSIDE_NAME);
                }
                if (nbt.contains(CLIENTSIDE_GLINT)) {
                    nbt.remove(CLIENTSIDE_GLINT);
                }
            }
        }
    }
    
}
