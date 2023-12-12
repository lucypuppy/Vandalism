package de.vandalismdevelopment.vandalism.gui.impl.widget.impl.serverinfo;

import de.florianmichael.rclasses.common.StringUtils;

public enum ModsTableColumn {

    MOD_ID, MOD_VERSION, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
