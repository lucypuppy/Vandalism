package de.nekosarekawaii.vandalism.feature.script.gui;

import de.florianmichael.rclasses.common.StringUtils;

public enum ScriptsTableColumn {

    NAME, VERSION, AUTHOR, DESCRIPTION, CATEGORY, EXPERIMENTAL, MODIFICATION_DATE, ACTIONS;

    public String getName() {
        return StringUtils.normalizeEnumName(this.name());
    }

}