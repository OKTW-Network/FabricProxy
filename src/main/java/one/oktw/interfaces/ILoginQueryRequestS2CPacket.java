package one.oktw.interfaces;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public interface ILoginQueryRequestS2CPacket {
    int getQueryId();

    void setQueryId(int queryId);

    Identifier getChannel();

    void setChannel(Identifier channel);

    PacketByteBuf getPayload();

    void setPayload(PacketByteBuf payload);
}
