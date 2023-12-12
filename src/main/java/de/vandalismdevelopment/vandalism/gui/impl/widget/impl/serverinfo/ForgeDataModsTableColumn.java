package de.vandalismdevelopment.vandalism.gui.impl.widget.impl.serverinfo;

import de.florianmichael.rclasses.common.StringUtils;

public enum ForgeDataModsTableColumn {

    MOD_ID, MOD_MARKER, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
