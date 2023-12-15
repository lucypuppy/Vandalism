package de.nekosarekawaii.vandalism.clientmenu.impl.port;

import de.florianmichael.rclasses.common.StringUtils;

public enum PortsTableColumn {

    PORT, STATE, QUERY_STATE, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
