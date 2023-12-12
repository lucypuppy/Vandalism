package de.vandalismdevelopment.vandalism.gui.impl.menu.impl.namehistory;

import de.florianmichael.rclasses.common.StringUtils;

public enum NamesTableColumn {

    USERNAME, DATE, ACCURATE, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
