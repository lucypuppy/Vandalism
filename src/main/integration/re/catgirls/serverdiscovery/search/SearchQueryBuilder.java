package re.catgirls.serverdiscovery.search;

import java.util.HashMap;
import java.util.Map;

public class SearchQueryBuilder {

    private final Map<String, String> queries = new HashMap<>();

    /**
     * Search for servers that are in offline mode.
     *
     * @return The builder.
     */
    public SearchQueryBuilder offlineOnly() {
        queries.put("offline", "true");
        return this;
    }

    /**
     * Search for servers that are in online mode.
     *
     * @return The builder.
     */
    public SearchQueryBuilder onlineOnly() {
        queries.put("online", "true");
        return this;
    }

    /**
     * Search for servers with no whitelist.
     *
     * @return The builder.
     */
    public SearchQueryBuilder noWhitelist() {
        queries.put("whitelisted", "true");
        return this;
    }

    /**
     * Search for a specific server brand.
     *
     * @param brand The brand to search for.
     * @return The builder.
     */
    public SearchQueryBuilder containsInBrand(final String brand) {
        queries.put("brand", brand);
        return this;
    }

    /**
     * Search for a player. This will return all servers that could possibly contain the player.
     *
     * @param player The player to search for.
     * @return The builder.
     */
    public SearchQueryBuilder containsPlayer(final String player) {
        queries.put("players", player);
        return this;
    }

    /**
     * Search for a string in the motd.
     *
     * @param motd The string to search for.
     * @return The builder.
     */
    public SearchQueryBuilder containsMotd(final String motd) {
        queries.put("motd", motd);
        return this;
    }

    /**
     * Create the query string.
     *
     * @return The query string.
     */
    public String create() {
        if (queries.isEmpty()) return "";
        final StringBuilder query = new StringBuilder();
        queries.forEach((key, value) -> query.append(key).append("=").append(value).append("&"));
        return query.deleteCharAt(query.length() - 1).toString();
    }
}