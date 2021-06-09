package one.oktw.mixin.velocity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryResponseC2SPacketAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import one.oktw.FabricProxy;
import one.oktw.VelocityLib;
import one.oktw.interfaces.BungeeClientConnection;
import one.oktw.mixin.ClientConnectionAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.netty.buffer.Unpooled.EMPTY_BUFFER;
import static one.oktw.FabricProxy.config;

@Mixin(value = ServerLoginNetworkHandler.class, priority = 1001)
public abstract class ServerLoginNetworkHandlerMixin {
    private int velocityLoginQueryId = -1;
    private boolean ready = false;
    private boolean bypassProxyVelocity = false;
    private LoginHelloC2SPacket loginPacket;

    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    private GameProfile profile;

    @Shadow
    public abstract void disconnect(Text text);

    @Shadow
    public abstract void onHello(LoginHelloC2SPacket loginHelloC2SPacket);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onHello",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/login/LoginHelloC2SPacket;getProfile()Lcom/mojang/authlib/GameProfile;"),
            cancellable = true)
    private void sendVelocityPacket(LoginHelloC2SPacket loginHelloC2SPacket, CallbackInfo ci) {
        // Bypass BungeeCord connection
        if (config.getBungeeCord() && ((BungeeClientConnection) connection).getSpoofedUUID() != null) {
            bypassProxyVelocity = true;
        }

        if (!bypassProxyVelocity && !ready) {
            if (FabricProxy.config.getAllowBypassProxy()) {
                loginPacket = loginHelloC2SPacket;
            }
            this.velocityLoginQueryId = java.util.concurrent.ThreadLocalRandom.current().nextInt();
            LoginQueryRequestS2CPacket packet = new LoginQueryRequestS2CPacket(
                    velocityLoginQueryId,
                    VelocityLib.PLAYER_INFO_CHANNEL,
                    new PacketByteBuf(EMPTY_BUFFER)
            );
            connection.send(packet);
            ci.cancel();
        }
    }

    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isLocal()Z"))
    private boolean skipAuth(ClientConnection connection) {
        return !bypassProxyVelocity;
    }

    @Inject(method = "onQueryResponse", at = @At("HEAD"), cancellable = true)
    private void forwardPlayerInfo(LoginQueryResponseC2SPacket packet, CallbackInfo ci) {
        if (FabricProxy.config.getVelocity() && ((LoginQueryResponseC2SPacketAccessor) packet).getQueryId() == velocityLoginQueryId) {
            PacketByteBuf buf = ((LoginQueryResponseC2SPacketAccessor) packet).getResponse();
            if (buf == null) {
                if (!FabricProxy.config.getAllowBypassProxy()) {
                    disconnect(new LiteralText("This server requires you to connect with Velocity."));
                    return;
                }

                bypassProxyVelocity = true;
                onHello(loginPacket);
                ci.cancel();
                return;
            }

            if (!VelocityLib.checkIntegrity(buf)) {
                disconnect(new LiteralText("Unable to verify player details"));
                return;
            }

            ((ClientConnectionAccessor) connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) connection.getAddress()).getPort()));

            profile = VelocityLib.createProfile(buf);

            ready = true;
            onHello(new LoginHelloC2SPacket(profile));
            ci.cancel();
        }
    }
}
