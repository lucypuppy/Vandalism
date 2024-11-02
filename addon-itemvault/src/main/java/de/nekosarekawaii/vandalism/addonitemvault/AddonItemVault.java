/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import com.itemvault.fabric_platform_api.Messages;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonitemvault.command.ItemVaultCommand;
import de.nekosarekawaii.vandalism.addonitemvault.creativetab.ItemVaultCreativeTab;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import lombok.Getter;

import java.io.File;
import java.util.logging.Logger;

@Getter
public class AddonItemVault implements VandalismAddonLauncher {

    private ItemVaultFabricBase vaultHolder;
    private ItemVaultCreativeTab itemVaultCreativeTab;

    @Override
    public void onPreLaunch(final Vandalism vandalism) {
        this.vaultHolder = new ItemVaultFabricBase(Logger.getLogger("Vandalism-ItemVault"), new MessagesImpl(), new File(FabricBootstrap.MOD_ID, "data"));
        this.vaultHolder.init();

        this.itemVaultCreativeTab = new ItemVaultCreativeTab(this.vaultHolder);
        this.itemVaultCreativeTab.publish();
    }

    @Override
    public void onLaunch(final Vandalism vandalism) {
        vandalism.getCommandManager().add(new ItemVaultCommand(this.vaultHolder));
    }

    private static class MessagesImpl implements Messages {

        @Override
        public void success(final String s) {
            ChatUtil.infoChatMessage(s);
        }

        @Override
        public void warning(final String s) {
            ChatUtil.warningChatMessage(s);
        }

        @Override
        public void error(final String s) {
            ChatUtil.errorChatMessage(s);
        }

    }

}
