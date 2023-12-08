package re.catgirls.packets.connection;

import io.netty.channel.*;
import re.catgirls.irc.ChatClient;
import re.catgirls.irc.connection.ChatChannelInitializer;
import re.catgirls.packets.Packet;
import re.catgirls.packets.event.EventRegistry;

import java.net.SocketAddress;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    private final Channel channel;
    private final ChatChannelInitializer initializer;
    private final EventRegistry eventRegistry;

    public PacketHandler(ChatChannelInitializer initializer, Channel channel, EventRegistry eventRegistry) {
        this.initializer = initializer;
        this.channel = channel;
        this.eventRegistry = eventRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        eventRegistry.invoke(packet, this);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ChatClient.getInstance().setSession(null);
        if (ChatClient.getInstance().getListeners().getDisconnectListener() != null)
            ChatClient.getInstance().getListeners().getDisconnectListener().onDisconnect();
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChatClient.getInstance().setSession(null);
        initializer.exceptionCaught(ctx, cause);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ChannelFuture send(Packet packet) {
        return channel.writeAndFlush(packet);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ChannelFuture close() {
        return channel.disconnect();
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    public SocketAddress getAddress() {
        return channel.remoteAddress() == null ? channel.localAddress() : channel.remoteAddress();
    }

    public Channel getChannel() {
        return channel;
    }

    public ChannelId getId() {
        return channel.id();
    }
}