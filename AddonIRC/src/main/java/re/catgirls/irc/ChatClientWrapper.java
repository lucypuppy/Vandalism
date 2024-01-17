package re.catgirls.irc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatClientWrapper {

    private final Logger LOGGER = LogManager.getLogger(ChatClient.class);
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ChatClient client;

    public ChatClientWrapper() {
        this.client = new ChatClient();
    }

    public void connect(
            final InetSocketAddress address,
            final long reconnectInterval,
            final String username,
            final String password,
            final String client
    ) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            //check if the session is null, if it isn't then we return because that means we're already connected
            if (this.client.getSession() != null) return;

            try {
                this.client.connect(address, username, password, client, future -> ChatClient.getLogger().info("Successfully connected to server!"));
            } catch (Exception e) {
                ChatClient.getLogger().error("Reconnection attempt failed: " + e.getMessage());
            }
        }, 0, reconnectInterval, TimeUnit.MILLISECONDS);
    }

    public void disconnect() {
        if (scheduler == null) return;
        if (client.getSession() != null) {
            client.getSession().getHandler().close();
            client.setSession(null);
        }

        scheduler.shutdownNow();
        scheduler.shutdown();
        scheduler = null;
    }

    public ChatClient getClient() {
        return client;
    }
}
