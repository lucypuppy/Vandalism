package re.catgirls.irc.session;

import re.catgirls.irc.interfaces.IdentifiableEnum;

public class UserProfile {

    /* user data */
    private final String name;
    private Rank rank;
    private String mcUsername, mcServer;

    /* json web token */
    private String jwt;

    /**
     * Create a new user profile
     *
     * @param name username
     * @param rank user rank
     */
    public UserProfile(final String name, final Rank rank) {
        this.name = name;
        this.rank = rank;
    }

    /**
     * Get the username
     *
     * @return the username
     */
    public String getName() {
        return name;
    }

    /**
     * Get the json web token (Self-client only)
     *
     * @return the json web token
     */
    public String getJwt() {
        return jwt;
    }

    /**
     * Get the user rank
     *
     * @return the user rank
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Get the minecraft username
     *
     * @return the minecraft username
     */
    public String getMcUsername() {
        return mcUsername;
    }

    /**
     * Get the minecraft server
     *
     * @return the minecraft server
     */
    public String getMcServer() {
        return mcServer;
    }

    /**
     * Set the users minecraft username
     *
     * @param mcUsername the minecraft username
     */
    public void setMcUsername(String mcUsername) {
        this.mcUsername = mcUsername;
    }

    /**
     * Set the users minecraft server
     *
     * @param mcServer the minecraft server
     */
    public void setMcServer(String mcServer) {
        this.mcServer = mcServer;
    }

    /**
     * Set the json web token
     *
     * @param jwt the json web token
     */
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    /**
     * All identifiable ranks
     */
    public enum Rank implements IdentifiableEnum {
        USER(0),
        MODERATOR(1),
        ADMINISTRATOR(2);

        private final int id;

        Rank(final int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

}
