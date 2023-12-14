package de.nekosarekawaii.vandalism.gui.widget;

import de.florianmichael.rclasses.common.StringUtils;

public enum ServerInfosTableColumn {

    SERVER_ADDRESS, PORT, RESOLVED_SERVER_ADDRESS, PROTOCOL, VERSION, ONLINE_PLAYERS, MAX_PLAYERS, MOTD, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
