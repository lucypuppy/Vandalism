package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.StringValue;
import net.minecraft.client.session.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class ProtectorModule extends Module implements RenderListener {

    private final Value<Boolean> protectUsername = new BooleanValue(
            "Protect Username",
            "Protects your username from being leaked.",
            this,
            true
    );

    private final Value<String> username = new StringValue(
            "Username",
            "The replacement for your username.",
            this,
            "<censored-username>"
    ).visibleConsumer(this.protectUsername::getValue);

    private final Value<Boolean> protectUUID = new BooleanValue(
            "Protect UUID",
            "Protects your uuid from being leaked.",
            this,
            true
    );

    private final Value<String> uuid = new StringValue(
            "UUID",
            "The replacement for your uuid.",
            this,
            "<censored-uuid>"
    ).visibleConsumer(this.protectUUID::getValue);

    public ProtectorModule() {
        super(
                "Protector",
                "Protects information from being leaked.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TextDrawEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TextDrawEvent.ID, this);
    }

    //TODO: Add protection for custom rank prefixes.
    //TODO: Add protection for skins.
    //TODO: Add protection for coords.
    //TODO: Maybe use a chat event instead of a text draw event.

    @Override
    public void onTextDraw(final TextDrawEvent event) {
        final Session session = this.mc().session;
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
