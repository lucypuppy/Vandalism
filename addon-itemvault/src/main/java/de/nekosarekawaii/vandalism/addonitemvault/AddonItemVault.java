/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.addonitemvault;

import com.itemvault.fabric_platform_api.ItemVaultFabricBase;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonitemvault.creativetab.ItemVaultCreativeTab;
import de.nekosarekawaii.vandalism.base.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;

import java.io.File;

public class AddonItemVault implements VandalismAddonLauncher {

    public static final File TEMP_DIR = new File("C:\\Users\\UwU\\IdeaProjects\\ItemVault\\data");

    ItemVaultFabricBase vaultHolder;

    @Override
    public void onPreLaunch(Vandalism vandalism) {
        vaultHolder = new ItemVaultFabricBase(TEMP_DIR);
        vaultHolder.loadFiles();

        CreativeTabManager.getInstance().add(new ItemVaultCreativeTab(vaultHolder));
    }

    @Override
    public void onLaunch(Vandalism vandalism) {
    }
}
