package de.gerrygames.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;

public class ScoreboardPackets {
  public static void register(Protocol<ClientboundPackets1_9, ClientboundPackets1_8, ServerboundPackets1_9, ServerboundPackets1_8> protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.TEAMS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.BYTE);
            handler(packetWrapper -> {
                  byte mode = ((Byte)packetWrapper.get((Type)Type.BYTE, 0)).byteValue();
                  if (mode == 0 || mode == 2) {
                    packetWrapper.passthrough(Type.STRING);
                    packetWrapper.passthrough(Type.STRING);
                    packetWrapper.passthrough(Type.STRING);
                    packetWrapper.passthrough((Type)Type.BYTE);
                    packetWrapper.passthrough(Type.STRING);
                    packetWrapper.read(Type.STRING);
                    packetWrapper.passthrough((Type)Type.BYTE);
                  } 
                  if (mode == 0 || mode == 3 || mode == 4)
                    packetWrapper.passthrough(Type.STRING_ARRAY); 
                });
          }
        });
  }
}
