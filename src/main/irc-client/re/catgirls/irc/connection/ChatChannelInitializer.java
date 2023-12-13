package re.catgirls.irc.connection;

import re.catgirls.irc.packet.impl.shared.SharedKeyExchangePacket;
import re.catgirls.irc.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import re.catgirls.irc.ChatClient;
import re.catgirls.packets.coder.RsaPacketDecoder;
import re.catgirls.packets.coder.RsaPacketEncoder;
import re.catgirls.packets.connection.PacketHandler;

public class ChatChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) {
        final PacketHandler chatPacketHandler = new PacketHandler(
                this,
                channel,
                ChatClient.getInstance().getPacketRegistry().getEventRegistry()
        );

        final Session chatChannelSession = new Session(chatPacketHandler);
        ChatClient.getInstance().setSession(chatChannelSession);

        // in
        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
        channel.pipeline().addLast(new RsaPacketDecoder(ChatClient.getInstance().getPacketRegistry(), chatChannelSession));

        // out
        channel.pipeline().addLast(new LengthFieldPrepender(4));
        channel.pipeline().addLast(new RsaPacketEncoder(ChatClient.getInstance().getPacketRegistry(), chatChannelSession));

        channel.pipeline().addLast(chatPacketHandler);

        chatPacketHandler.send(new SharedKeyExchangePacket(chatChannelSession.getClientKeyPair().getPublic()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChatClient.getInstance().setSession(null);
        super.exceptionCaught(ctx, cause);
    }
}
