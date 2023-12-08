package re.catgirls.irc.packet.impl.shared;

import re.catgirls.irc.ChatClient;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class SharedKeyExchangePacket extends Packet {

    private PublicKey key;

    // @formatter:off
    public SharedKeyExchangePacket() {}
    public SharedKeyExchangePacket(final PublicKey key) { this.key = key; }
    // @formatter:on

    @Override
    public void read(PacketBuffer buffer) {
        int keyBytesLength = buffer.readInt();
        if (keyBytesLength > 0) {
            byte[] keyBytes = new byte[keyBytesLength];
            buffer.readBytes(keyBytes);

            key = reconstructPublicKey(keyBytes);
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
        byte[] keyBytes = key.getEncoded();

        buffer.writeInt(keyBytes.length);

        if (keyBytes.length > 0)
            buffer.writeBytes(keyBytes);
    }

    public PublicKey getKey() {
        return key;
    }

    private PublicKey reconstructPublicKey(byte[] keyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }
    }
}
