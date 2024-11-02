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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.event.render.TextDrawListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.client.session.Session;

import java.util.UUID;

public class ProtectorModule extends Module implements TextDrawListener {

    private final BooleanValue protectUsername = new BooleanValue(
            this,
            "Protect Username",
            "Protects your username from being leaked.",
            true
    );

    private final StringValue username = new StringValue(
            this,
            "Username",
            "The replacement for your username.",
            "<censored-username>"
    ).visibleCondition(this.protectUsername::getValue);

    private final BooleanValue protectUUID = new BooleanValue(
            this,
            "Protect UUID",
            "Protects your uuid from being leaked.",
            true
    );

    private final StringValue uuid = new StringValue(
            this,
            "UUID",
            "The replacement for your uuid.",
            "<censored-uuid>"
    ).visibleCondition(this.protectUUID::getValue);

    public ProtectorModule() {
        super("Protector", "Protects information from being leaked.", Category.RENDER);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void onTextDraw(final TextDrawEvent event) {
        final Session session = mc.session;
        if (this.protectUsername.getValue()) {
            event.text = StringUtils.replaceAll(event.text, session.getUsername(), this.username.getValue());
        }
        if (this.protectUUID.getValue()) {
            final UUID uuid = session.getUuidOrNull();
            if (uuid != null) {
                event.text = StringUtils.replaceAll(event.text, uuid.toString(), this.uuid.getValue());
                event.text = StringUtils.replaceAll(event.text, uuid.toString().replace("-", ""), this.uuid.getValue());
            }
        }
    }

}
