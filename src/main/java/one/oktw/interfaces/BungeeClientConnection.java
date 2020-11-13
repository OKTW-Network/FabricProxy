package one.oktw.interfaces;

import com.mojang.authlib.properties.Property;

import java.util.UUID;

public interface BungeeClientConnection {
    UUID getSpoofedUUID();

    void setSpoofedUUID(UUID uuid);

    Property[] getSpoofedProfile();

    void setSpoofedProfile(Property[] profile);
}
