package one.oktw.mixin.bungee;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.FabricProxy;
import one.oktw.interfaces.BungeeClientConnection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    private boolean bypassProxy = false;
    @Shadow
    @Final
    public ClientConnection connection;
    @Shadow
    private GameProfile profile;
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract void acceptPlayer();

    private boolean ready = false;

    @Inject(method = "onHello", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;", shift = At.Shift.AFTER), cancellable = true)
    private void initUuid(CallbackInfo ci) {
        if (FabricProxy.config.getBungeeCord()) {
            if (((BungeeClientConnection) connection).getSpoofedUUID() == null) {
                bypassProxy = true;
                return;
            }

            this.profile = new GameProfile(((BungeeClientConnection) connection).getSpoofedUUID(), this.profile.getName());

            if (((BungeeClientConnection) connection).getSpoofedProfile() != null) {
                for (Property property : ((BungeeClientConnection) connection).getSpoofedProfile()) {
                    this.profile.getProperties().put(property.getName(), property);
                }
            }
            ready = true;
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;loginTicks:I"))
    private void login(CallbackInfo ci) {
        if (ready) {
            ready = false;
            acceptPlayer();
        }
    }

}
