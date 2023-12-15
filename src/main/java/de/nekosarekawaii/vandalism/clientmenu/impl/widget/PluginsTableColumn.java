package de.nekosarekawaii.vandalism.clientmenu.impl.widget;

import de.florianmichael.rclasses.common.StringUtils;

public enum PluginsTableColumn {

    NAME, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
