package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.render.TextDrawListener;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.client.session.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class ProtectorModule extends AbstractModule implements TextDrawListener {

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
    public void onEnable() {
        DietrichEvents2.global().subscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void onTextDraw(final TextDrawEvent event) {
        final Session session = this.mc.session;
        if (this.protectUsername.getValue()) event.text = StringUtils.replace(event.text, session.getUsername(), this.username.getValue());
        if (this.protectUUID.getValue()) {
            final UUID uuid = session.getUuidOrNull();
            if (uuid != null) {
                event.text = StringUtils.replace(event.text, uuid.toString(), this.uuid.getValue());
                event.text = StringUtils.replace(event.text, uuid.toString().replace("-", ""), this.uuid.getValue());
            }
        }
    }

}
