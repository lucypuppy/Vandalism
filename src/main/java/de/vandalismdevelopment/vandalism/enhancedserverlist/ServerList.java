package de.vandalismdevelopment.vandalism.enhancedserverlist;

public class ServerList {

    public final static String DEFAULT_SERVER_LIST_NAME = "Default";
    private final static String DEFAULT_SERVER_LIST_FILE_NAME = "servers";

    private final String name;
    private int size;

    public ServerList() {
        this(DEFAULT_SERVER_LIST_FILE_NAME);
    }

    public ServerList(final String name) {
        this.name = name;
        this.size = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public ServerList setSize(final int size) {
        this.size = size;
        return this;
    }

    public boolean isDefault() {
        return this.name.equals(DEFAULT_SERVER_LIST_FILE_NAME);
    }

}
