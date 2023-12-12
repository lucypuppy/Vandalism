package de.vandalismdevelopment.vandalism.gui_v2.impl.port;

import de.florianmichael.rclasses.common.StringUtils;

public enum PortsTableColumn {

    PORT, STATE, QUERY_STATE, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
