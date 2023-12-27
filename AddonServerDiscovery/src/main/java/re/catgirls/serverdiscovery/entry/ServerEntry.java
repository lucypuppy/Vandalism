package re.catgirls.serverdiscovery.entry;

public record ServerEntry(
        String address,
        String motd,
        String players,
        String brand,
        boolean whitelisted,
        boolean online,
        int playerCount) {
}
