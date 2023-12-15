package de.nekosarekawaii.vandalism.clientmenu.impl.namehistory;

import de.florianmichael.rclasses.common.StringUtils;

public enum NamesTableColumn {

    USERNAME, DATE, ACCURATE, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
