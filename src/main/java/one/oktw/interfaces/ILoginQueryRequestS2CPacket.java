package one.oktw.interfaces;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface ILoginQueryRequestS2CPacket {
    void setQueryId(int queryId);

    void setChannel(Identifier channel);

    void setPayload(PacketByteBuf payload);
}
