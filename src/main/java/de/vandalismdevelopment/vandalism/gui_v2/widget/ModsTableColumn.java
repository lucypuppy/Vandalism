package de.vandalismdevelopment.vandalism.gui_v2.widget;

import de.florianmichael.rclasses.common.StringUtils;

public enum ModsTableColumn {

    MOD_ID, MOD_VERSION, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
