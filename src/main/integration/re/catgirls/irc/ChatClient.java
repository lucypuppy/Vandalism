package re.catgirls.irc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import re.catgirls.irc.connection.ChatChannelInitializer;
import re.catgirls.irc.listeners.Listeners;
import re.catgirls.irc.packet.PacketRegistry;
import re.catgirls.irc.packet.impl.c2s.C2SDataUpdatePacket;
import re.catgirls.irc.packet.impl.shared.SharedKeepAlivePacket;
import re.catgirls.irc.session.Session;
import re.catgirls.irc.session.UserProfile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The main of the IRC
 *
 * @author Lucy Luna
 * @version 1.1
 */
public class ChatClient {

    /* logger & chat-client instance */
    private static final Logger LOGGER = LogManager.getLogger(ChatClient.class);
    private static ChatClient instance;

    /* users & listeners */
    private final Map<String, UserProfile> users = new HashMap<>();
    private final Listeners listeners = new Listeners();

    /* netty bootstrap */
    private final Bootstrap bootstrap;

    /* registry with packets */
    private PacketRegistry packetRegistry;

    /* session & user credentials...(I should change this...ffs) */
    private Session session;
    private String username, password, client;

    /**
     * <h2>Creates a new IRC client</h2>
     * <p>
     * This will create a new event loop group and a new packet registry instance
     * and will also create a new bootstrap instance for the IRC client
     * and a new scheduled executor service for the keep-alive handler
     * </p>
     */
    public ChatClient() {
        instance = this;

        // create new event loop groups
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        if (Epoll.isAvailable()) {
            workerGroup = new EpollEventLoopGroup();
            LOGGER.info("Epoll is available, using native epoll implementation.");
        } else LOGGER.warn("Epoll is not available, using NIO implementation.");

        // create new packet registry instance
        try {
            this.packetRegistry = new PacketRegistry();
        } catch (RuntimeException e) {
            LOGGER.fatal("Failed to register packets", e);
        }

        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        // create keep-alive handler
        executorService.scheduleAtFixedRate(() -> {
            if (session == null) return;
            session.getHandler().send(new SharedKeepAlivePacket());
        }, 0, 5, TimeUnit.SECONDS);

        // creates new bootstrap instance for the IRC client
        this.bootstrap = new Bootstrap().
                option(ChannelOption.TCP_NODELAY, true).
                group(workerGroup).
                handler(new ChatChannelInitializer()).
                channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class);
    }

    /**
     * Connects to the server
     *
     * @param address  the address
     * @param username the username
     * @param password the password
     * @param client   the client
     * @param callback the callback when the client successfully connected
     * @throws IOException if an error occurs
     */
    public void connect(
            final InetSocketAddress address,
            final String username,
            final String password,
            final String client,
            final Consumer<Future<? super Void>> callback
    ) throws IOException {
        // clear users
        ChatClient.getInstance().getUsers().clear();

        // set username & password to connect with
        this.username = username;
        this.password = password;
        this.client = client;

        // connect to the server
        try {
            this.bootstrap.connect(address).awaitUninterruptibly().sync().addListener(callback::accept);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    /**
     * Updates the mc username of the client
     *
     * @param username the username
     */
    public void updateMcUsername(final String username) {
        if (getSession() == null)
            throw new NullPointerException("session == null (not connected to the server?)");

        getSession().send(new C2SDataUpdatePacket(null, username));
    }

    /**
     * Updates the mc server of the client
     *
     * @param server the server
     */
    public void updateMcServer(final String server) {
        if (ChatClient.getInstance().getSession() == null)
            throw new NullPointerException("session == null (not connected to the server?)");

        getSession().send(new C2SDataUpdatePacket(server, null));
    }

    /**
     * Get the instance of the IRC client
     *
     * @return the instance
     */
    public static ChatClient getInstance() {
        return instance;
    }

    /**
     * Get the logger
     *
     * @return the logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Get the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the client
     *
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * Get the bootstrap instance
     *
     * @return the bootstrap
     */
    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    /**
     * Get the packet registry
     *
     * @return the packet registry
     */
    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    /**
     * Get the current session
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Get the listeners
     *
     * @return the listeners
     */
    public Listeners getListeners() {
        return listeners;
    }

    /**
     * Get the connected users
     *
     * @return the users
     */
    public Map<String, UserProfile> getUsers() {
        return users;
    }

    /**
     * Set our current session
     *
     * @param session the session
     */
    public void setSession(Session session) {
        this.session = session;
    }
}
