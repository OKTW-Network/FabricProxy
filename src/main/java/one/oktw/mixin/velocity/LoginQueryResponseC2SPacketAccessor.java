package one.oktw.mixin.velocity;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoginQueryResponseC2SPacket.class)
public interface LoginQueryResponseC2SPacketAccessor {
    @Accessor
    int getQueryId();

    @Accessor
    PacketByteBuf getResponse();
}
