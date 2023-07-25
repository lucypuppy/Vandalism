package me.nekosarekawaii.foxglove.util.minecraft.via;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.raphimc.vialoader.util.VersionEnum;

public class ViaPackets {

    private final static MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendCustomPayload(final Identifier identifier, final PacketByteBuf packetByteBuf) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            final var pluginMessage = PacketWrapper.create(ServerboundPackets1_12.PLUGIN_MESSAGE, ProtocolHack.getMainUserConnection());
            pluginMessage.write(Type.STRING, identifier.toString());
            pluginMessage.write(Type.REMAINING_BYTES, packetByteBuf.array());

            try {
                pluginMessage.sendToServerRaw();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else if (mc.player != null) {
            mc.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(identifier, packetByteBuf));
        }
    }

}
