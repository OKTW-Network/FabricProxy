package one.oktw.interfaces;

import net.minecraft.util.PacketByteBuf;

public interface ILoginQueryResponseC2SPacket {
    int getQueryId();

    PacketByteBuf getResponse();
}
