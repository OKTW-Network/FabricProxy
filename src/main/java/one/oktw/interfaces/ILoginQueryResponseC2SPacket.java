package one.oktw.interfaces;

import net.minecraft.network.PacketByteBuf;

public interface ILoginQueryResponseC2SPacket {
    int getQueryId();

    PacketByteBuf getResponse();
}
