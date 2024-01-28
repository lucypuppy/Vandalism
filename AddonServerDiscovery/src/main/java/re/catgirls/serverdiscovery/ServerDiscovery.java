package re.catgirls.serverdiscovery;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import re.catgirls.serverdiscovery.entry.ServerEntry;
import re.catgirls.serverdiscovery.search.NumberRangeType;
import re.catgirls.serverdiscovery.search.SearchQueryBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main API class for the server discovery API.
 * This class is used to get information about servers.
 *
 * @author Lucy Luna
 */
public class ServerDiscovery {

    private static final HttpClient REQUESTER = HttpClient.newHttpClient();

    private final String host;
    private final String key;

    /**
     * Create a new server discovery api instance with the given API key.
     *
     * @param host The host to use.
     * @param key The API key to use.
     */
    public ServerDiscovery(final String host, final String key) {
        this.host = host;
        this.key = key;
    }

    /**
     * Get a list of servers that match the given search query.
     *
     * @param builder The search query builder to use.
     * @return A list of servers that match the given search query.
     * @throws IOException If an error occurs while sending the request.
     */
    public List<ServerEntry> getServers(
            final SearchQueryBuilder builder,
            final int limit,
            final NumberRangeType playerRange,
            final int minPlayers,
            final int maxPlayers
    ) throws Exception {
        final List<ServerEntry> entries = new ArrayList<>();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s/v1/list_servers?key=%s%s".formatted(this.host, this.key, builder.create().isBlank() ? "" : "&" + builder.create())))
                .GET()
                .build();
        final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
        final JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        jsonObject.getAsJsonArray("data").forEach(jsonElement -> {
            final JsonObject object = jsonElement.getAsJsonObject();

            switch (playerRange) {
                case GREATER_THAN -> {
                    if (object.get("player_count").getAsInt() < minPlayers) return;
                }
                case LESS_THAN -> {
                    if (object.get("player_count").getAsInt() > maxPlayers) return;
                }
                case BETWEEN -> {
                    if (object.get("player_count").getAsInt() < minPlayers || object.get("player_count").getAsInt() > maxPlayers) return;
                }
                case EQUALS -> {
                    if (object.get("player_count").getAsInt() != minPlayers) return;
                }
            }
            entries.add(new ServerEntry(
                    object.get("address").getAsString(),
                    object.get("motd").getAsString(),
                    object.get("players").getAsString(),
                    object.get("brand").getAsString(),
                    object.get("whitelisted").getAsBoolean(),
                    object.get("online").getAsBoolean(),
                    object.get("player_count").getAsInt()
            ));
        });
        Collections.shuffle(entries);
        return entries.subList(0, Math.min(entries.size(), limit));
    }

    public List<ServerEntry> getServers(final SearchQueryBuilder builder, final int limit) throws Exception {
        return getServers(builder, limit, NumberRangeType.ANY, -1, -1);
    }

}
