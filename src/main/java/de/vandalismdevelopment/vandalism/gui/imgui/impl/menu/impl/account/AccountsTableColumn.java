package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.account;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum AccountsTableColumn implements EnumNameNormalizer {

    USERNAME, UUID, TYPE, ACTIONS;

    private final String normalName;

    AccountsTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}