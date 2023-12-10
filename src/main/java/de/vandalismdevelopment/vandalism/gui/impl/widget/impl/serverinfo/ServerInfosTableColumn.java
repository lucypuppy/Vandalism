package de.vandalismdevelopment.vandalism.gui.impl.widget.impl.serverinfo;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum ServerInfosTableColumn implements EnumNameNormalizer {

    SERVER_ADDRESS, PORT, RESOLVED_SERVER_ADDRESS, PROTOCOL, VERSION, ONLINE_PLAYERS, MAX_PLAYERS, MOTD, ACTIONS;

    private final String normalName;

    ServerInfosTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}