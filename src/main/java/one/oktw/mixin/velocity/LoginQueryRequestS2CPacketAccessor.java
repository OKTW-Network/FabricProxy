package one.oktw.mixin.velocity;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoginQueryRequestS2CPacket.class)
public interface LoginQueryRequestS2CPacketAccessor {
    @Accessor
    void setQueryId(int queryId);

    @Accessor
    void setChannel(Identifier channel);

    @Accessor
    void setPayload(PacketByteBuf payload);
}
