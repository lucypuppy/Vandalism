package re.catgirls.irc.packet.impl.s2c;

import com.google.gson.JsonObject;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

public class S2CChatMessagePacket extends Packet {

    private JsonObject object;
    private JsonObject sender;
    private JsonObject message;

    // @formatter:off

    public S2CChatMessagePacket() { }

    @Override
    public void write(PacketBuffer buffer) {}

    // @formatter:on


    @Override
    public void read(final PacketBuffer buffer) throws Exception {
        object = buffer.readJson();
        if(object.has("sender")) sender = object.getAsJsonObject("sender");
        if(object.has("message")) message = object.getAsJsonObject("message");
    }

    public JsonObject getMessage() {
        if (message == null) throw new NullPointerException("no message in packet (faulty packet)");
        return message;
    }

    public JsonObject getSender() {
        return sender;
    }

    public JsonObject getObject() {
        return object;
    }
}
