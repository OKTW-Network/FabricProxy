package one.oktw.mixin.bungee;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import one.oktw.interfaces.IClientConnection;
import one.oktw.interfaces.IHandshakeC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static one.oktw.FabricProxy.config;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    private static final Gson gson = new Gson();

    @Shadow
    @Final
    private ClientConnection connection;

    @Inject(method = "onHandshake", at = @At(value = "HEAD"), cancellable = true)
    private void onProcessHandshakeStart(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (config.getBungeeCord() && packet.getIntendedState().equals(NetworkState.LOGIN)) {
            String[] split = ((IHandshakeC2SPacket) packet).getAddress().split("\00");
            if (split.length == 3 || split.length == 4) {
                ((IHandshakeC2SPacket) packet).setAddress(split[0]);
                ((IClientConnection) connection).setRemoteAddress(new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) connection.getAddress()).getPort()));
                ((IClientConnection) connection).setSpoofedUUID(UUIDTypeAdapter.fromString(split[2]));
            } else {
                Text disconnectMessage = new LiteralText("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                connection.send(new LoginDisconnectS2CPacket(disconnectMessage));
                connection.disconnect(disconnectMessage);
                return;
            }
            if (split.length == 4) {
                ((IClientConnection) connection).setSpoofedProfile(gson.fromJson(split[3], Property[].class));
            }
        }
    }
}
