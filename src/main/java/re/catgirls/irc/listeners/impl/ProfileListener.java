package re.catgirls.irc.listeners.impl;

import re.catgirls.irc.session.UserProfile;

public interface ProfileListener {
    void onMinecraftServerUpdate(final UserProfile profile, final String newServer, final String oldServer);

    void onMinecraftUsernameUpdate(final UserProfile profile, final String newUsername, final String oldUpdate);

    void onUserJoin(final UserProfile profile);

    void onUserLeave(final UserProfile profile);
}