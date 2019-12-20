package one.oktw.mixin.bungee;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
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
    private ClientConnection client;

    @Inject(method = "onHandshake", at = @At(value = "HEAD"), cancellable = true)
    private void onProcessHandshakeStart(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (config.getBungeeCord() && packet.getIntendedState().equals(NetworkState.LOGIN)) {
            String[] split = ((IHandshakeC2SPacket) packet).getAddress().split("\00");
            if (split.length == 3 || split.length == 4) {
                ((IHandshakeC2SPacket) packet).setAddress(split[0]);
                ((IClientConnection) client).setRemoteAddress(new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) client.getAddress()).getPort()));
                ((IClientConnection) client).setSpoofedUUID(UUIDTypeAdapter.fromString(split[2]));
            } else {
                Text disconnectMessage = new LiteralText("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                client.send(new LoginDisconnectS2CPacket(disconnectMessage));
                client.disconnect(disconnectMessage);
                return;
            }
            if (split.length == 4) {
                ((IClientConnection) client).setSpoofedProfile(gson.fromJson(split[3], Property[].class));
            }
        }
    }
}
