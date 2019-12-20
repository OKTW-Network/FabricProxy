package one.oktw.mixin.bungee;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.interfaces.IClientConnection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow
    @Final
    public ClientConnection client;
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    private GameProfile profile;

    @Inject(method = "onHello", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER,
            target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;"))
    private void initUuid(CallbackInfo ci) {
        if (!this.server.isOnlineMode()) {
            UUID uuid;
            if (((IClientConnection) client).getSpoofedUUID() != null) {
                uuid = ((IClientConnection) client).getSpoofedUUID();
            } else {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.profile.getName()).getBytes(Charsets.UTF_8));
            }

            this.profile = new GameProfile(uuid, this.profile.getName());

            if (((IClientConnection) client).getSpoofedProfile() != null) {
                for (Property property : ((IClientConnection) client).getSpoofedProfile()) {
                    this.profile.getProperties().put(property.getName(), property);
                }
            }
        }
    }
}
