package one.oktw.mixin.velocity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;
import one.oktw.FabricProxy;
import one.oktw.VelocityProxy;
import one.oktw.interfaces.IClientConnection;
import one.oktw.interfaces.ILoginQueryRequestS2CPacket;
import one.oktw.interfaces.ILoginQueryResponseC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.netty.buffer.Unpooled.EMPTY_BUFFER;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow
    @Final
    public ClientConnection client;
    private int velocityLoginQueryId = -1;
    private boolean ready = false;
    @Shadow
    private GameProfile profile;

    @Shadow
    public abstract void method_14384();

    @Shadow
    public abstract void disconnect(TextComponent textComponent_1);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onHello", at = @At(value = "FIELD", ordinal = 2, target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;state:Lnet/minecraft/server/network/ServerLoginNetworkHandler$State;"), cancellable = true)
    private void sendVelocityPacket(LoginHelloC2SPacket loginHelloC2SPacket_1, CallbackInfo ci) {
        if (FabricProxy.config.getVelocity()) {
            this.velocityLoginQueryId = java.util.concurrent.ThreadLocalRandom.current().nextInt();
            LoginQueryRequestS2CPacket packet = new LoginQueryRequestS2CPacket();
            ((ILoginQueryRequestS2CPacket) packet).setQueryId(velocityLoginQueryId);
            ((ILoginQueryRequestS2CPacket) packet).setChannel(VelocityProxy.PLAYER_INFO_CHANNEL);
            ((ILoginQueryRequestS2CPacket) packet).setPayload(new PacketByteBuf(EMPTY_BUFFER));

            client.send(packet);
            ci.cancel();
        }
    }

    @Inject(method = "onQueryResponse", at = @At("HEAD"), cancellable = true)
    private void forwardPlayerInfo(LoginQueryResponseC2SPacket packet, CallbackInfo ci) {
        if (FabricProxy.config.getVelocity() && ((ILoginQueryResponseC2SPacket) packet).getQueryId() == velocityLoginQueryId) {
            PacketByteBuf buf = ((ILoginQueryResponseC2SPacket) packet).getResponse();
            if (buf == null) {
                disconnect(new StringTextComponent("This server requires you to connect with Velocity."));
                return;
            }

            if (!VelocityProxy.checkIntegrity(buf)) {
                disconnect(new StringTextComponent("Unable to verify player details"));
                return;
            }

            ((IClientConnection) client).setRemoteAddress(new java.net.InetSocketAddress(VelocityProxy.readAddress(buf), ((java.net.InetSocketAddress) client.getAddress()).getPort()));

            profile = VelocityProxy.createProfile(buf);

            ready = true;
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;loginTicks:I"))
    private void login(CallbackInfo ci) {
        if (ready) {
            ready = false;
            method_14384();
        }
    }
}
