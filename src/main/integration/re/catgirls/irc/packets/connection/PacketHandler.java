package re.catgirls.irc.packets.connection;

import re.catgirls.irc.connection.ChatChannelInitializer;
import re.catgirls.irc.listeners.impl.DisconnectListener;
import re.catgirls.irc.packets.Packet;
import re.catgirls.irc.packets.event.EventRegistry;
import io.netty.channel.*;
import re.catgirls.irc.ChatClient;

import java.net.SocketAddress;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    private final Channel channel;
    private final ChatChannelInitializer initializer;
    private final EventRegistry eventRegistry;

    /**
     * Create a new packet handler
     *
     * @param initializer   channel initializer
     * @param channel       channel
     * @param eventRegistry event registry
     */
    public PacketHandler(final ChatChannelInitializer initializer, final Channel channel, final EventRegistry eventRegistry) {
        this.initializer = initializer;
        this.channel = channel;
        this.eventRegistry = eventRegistry;
    }

    /**
     * Handle a packet
     *
     * @param channelHandlerContext channel handler context
     * @param packet                packet
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        eventRegistry.invoke(packet, this);
    }

    /**
     * Handle an active handler is being removed
     *
     * @param ctx channel handler context
     * @throws Exception if there was an error while removing the handler
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ChatClient.getInstance().setSession(null);

        final DisconnectListener listener = ChatClient.getInstance().getListeners().getDisconnectListener();
        if (listener != null) listener.onDisconnect();

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