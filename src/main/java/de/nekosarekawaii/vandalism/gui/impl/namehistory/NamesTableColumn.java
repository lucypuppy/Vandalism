package de.nekosarekawaii.vandalism.gui.impl.namehistory;

import de.florianmichael.rclasses.common.StringUtils;

public enum NamesTableColumn {

    USERNAME, DATE, ACCURATE, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}
