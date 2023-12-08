package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.account.AccountManagerImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.*;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.irc.IrcImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.nbteditor.NbtEditortImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.namehistory.NameHistoryImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.port.PortScannerImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script.ScriptsImGuiMenu;

import java.util.ArrayList;
import java.util.List;

public class ImGuiMenuCategoryRegistry {

    private final List<ImGuiMenuCategory> imGuiMenuCategories;

    public ImGuiMenuCategoryRegistry() {
        this.imGuiMenuCategories = new ArrayList<>();
        this.registerImGuiMenuCategories(
                new ImGuiMenuCategory(
                        "Configuration",
                        new ConfigImGuiMenu(),
                        new CustomHUDConfigImGuiMenu(),
                        new ModulesImGuiMenu(),
                        new ScriptsImGuiMenu(),
                        new AccountManagerImGuiMenu(),
                        new IrcImGuiMenu()
                ),
                new ImGuiMenuCategory(
                        "Server Utils",
                        new ServerPingerImGuiMenu(),
                        new PortScannerImGuiMenu(),
                        new ServerAddressResolverImGuiMenu()
                ),
                new ImGuiMenuCategory(
                        "Misc Utils",
                        new NameHistoryImGuiMenu(),
                        new NbtEditortImGuiMenu()
                )
        );
    }

    private void registerImGuiMenuCategories(final ImGuiMenuCategory... imGuiMenuCategories) {
        Vandalism.getInstance().getLogger().info("Registering imgui menu categories...");
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            if (!this.imGuiMenuCategories.contains(imGuiMenuCategory)) {
                this.imGuiMenuCategories.add(imGuiMenuCategory);
                Vandalism.getInstance().getLogger().info("ImGui Menu Category '" + imGuiMenuCategory + "' has been registered.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated imgui menu category found: " + imGuiMenuCategory);
            }
        }
        final int imGuiMenuCategoryListSize = this.imGuiMenuCategories.size();
        if (imGuiMenuCategoryListSize < 1) Vandalism.getInstance().getLogger().info("No imgui menu categories found!");
        else
            Vandalism.getInstance().getLogger().info("Registered " + imGuiMenuCategoryListSize + " imgui menu categories.");
    }

    public List<ImGuiMenuCategory> getImGuiMenuCategories() {
        return this.imGuiMenuCategories;
    }

    public <M extends ImGuiMenu> M getImGuiMenuByClass(final Class<M> clazz) {
        for (final ImGuiMenuCategory imGuiMenuCategory : this.imGuiMenuCategories) {
            for (final ImGuiMenu menu : imGuiMenuCategory.getImGuiMenus()) {
                if (menu.getClass().equals(clazz)) {
                    return (M) menu;
                }
            }
        }
        return null;
    }

}
