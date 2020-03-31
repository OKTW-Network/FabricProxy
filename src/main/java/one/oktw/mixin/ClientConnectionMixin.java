package one.oktw.mixin;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import one.oktw.interfaces.IClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;
import java.util.UUID;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements IClientConnection {
    @Shadow
    private SocketAddress address;
    private UUID spoofedUUID;
    private Property[] spoofedProfile;

    @Override
    public void setRemoteAddress(SocketAddress socketAddress) {
        this.address = socketAddress;
    }

    @Override
    public UUID getSpoofedUUID() {
        return this.spoofedUUID;
    }

    @Override
    public void setSpoofedUUID(UUID uuid) {
        this.spoofedUUID = uuid;
    }

    @Override
    public Property[] getSpoofedProfile() {
        return this.spoofedProfile;
    }

    @Override
    public void setSpoofedProfile(Property[] profile) {
        this.spoofedProfile = profile;
    }
}
