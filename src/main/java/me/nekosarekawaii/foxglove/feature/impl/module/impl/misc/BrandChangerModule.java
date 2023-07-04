package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import io.netty.buffer.Unpooled;
import me.nekosarekawaii.foxglove.event.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.StringValue;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

@ModuleInfo(name = "Brand Changer", description = "Changes the brand that the Client sends to the Server.", category = FeatureCategory.MISC, isDefaultEnabled = true)
public class BrandChangerModule extends Module implements PacketListener {

    private final Value<String> brand = new StringValue("Brand", "The Brand that will used.", this, ClientBrandRetriever.VANILLA);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.packet instanceof final CustomPayloadC2SPacket customPayloadC2SPacket) {
            final Identifier channel = customPayloadC2SPacket.getChannel();
            if (channel.equals(CustomPayloadC2SPacket.BRAND)) {
                event.packet = new CustomPayloadC2SPacket(channel, new PacketByteBuf(Unpooled.buffer()).writeString(this.brand.getValue()));
                //ChatUtils.infoChatMessage("Channel: " + customPayloadC2SPacket.getChannel() + " | Data: " + new String(((CustomPayloadC2SPacket) event.packet).getData().array()));
            }
        }
    }

}
