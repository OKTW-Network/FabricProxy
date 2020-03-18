package one.oktw.mixin.velocity;

import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.util.PacketByteBuf;
import one.oktw.interfaces.ILoginQueryResponseC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoginQueryResponseC2SPacket.class)
public class LoginQueryResponseC2SPacketMixin implements ILoginQueryResponseC2SPacket {
    @Shadow
    private int queryId;
    @Shadow
    private PacketByteBuf response;

    public int getQueryId() {
        return queryId;
    }

    public PacketByteBuf getResponse() {
        return response;
    }
}
