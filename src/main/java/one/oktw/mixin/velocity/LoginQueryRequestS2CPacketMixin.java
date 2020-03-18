package one.oktw.mixin.velocity;

import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import one.oktw.interfaces.ILoginQueryRequestS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoginQueryRequestS2CPacket.class)
public abstract class LoginQueryRequestS2CPacketMixin implements Packet<ClientLoginPacketListener>, ILoginQueryRequestS2CPacket {
    @Shadow
    private int queryId;
    @Shadow
    private Identifier channel;
    @Shadow
    private PacketByteBuf payload;

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public Identifier getChannel() {
        return channel;
    }

    public void setChannel(Identifier channel) {
        this.channel = channel;
    }

    public PacketByteBuf getPayload() {
        return payload;
    }

    public void setPayload(PacketByteBuf payload) {
        this.payload = payload;
    }
}
