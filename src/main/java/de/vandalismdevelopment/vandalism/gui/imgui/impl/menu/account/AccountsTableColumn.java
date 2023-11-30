package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.account;

import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;

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