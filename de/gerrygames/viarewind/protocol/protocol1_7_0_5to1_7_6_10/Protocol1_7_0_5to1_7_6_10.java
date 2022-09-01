package de.gerrygames.viarewind.protocol.protocol1_7_0_5to1_7_6_10;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.ClientboundPackets1_7;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.ServerboundPackets1_7;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Protocol1_7_0_5to1_7_6_10 extends AbstractProtocol<ClientboundPackets1_7, ClientboundPackets1_7, ServerboundPackets1_7, ServerboundPackets1_7> {
  public static final ValueTransformer<String, String> REMOVE_DASHES = new ValueTransformer<String, String>(Type.STRING) {
      public String transform(PacketWrapper packetWrapper, String s) {
        return s.replace("-", "");
      }
    };
  
  public Protocol1_7_0_5to1_7_6_10() {
    super(ClientboundPackets1_7.class, ClientboundPackets1_7.class, ServerboundPackets1_7.class, ServerboundPackets1_7.class);
  }
  
  protected void registerPackets() {
    registerClientbound(State.LOGIN, 2, 2, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING, Protocol1_7_0_5to1_7_6_10.REMOVE_DASHES);
            map(Type.STRING);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_7.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.STRING, Protocol1_7_0_5to1_7_6_10.REMOVE_DASHES);
            map(Type.STRING);
            handler(packetWrapper -> {
                  int size = ((Integer)packetWrapper.read((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size * 3; i++)
                    packetWrapper.read(Type.STRING); 
                });
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map(Types1_7_6_10.METADATA_LIST);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_7.TEAMS, new PacketRemapper() {
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
                  } 
                  if (mode == 0 || mode == 3 || mode == 4) {
                    List<String> entryList = new ArrayList<>();
                    int size = ((Short)packetWrapper.read((Type)Type.SHORT)).shortValue();
                    for (int i = 0; i < size; i++)
                      entryList.add((String)packetWrapper.read(Type.STRING)); 
                    entryList = (List<String>)entryList.stream().map(()).distinct().collect(Collectors.toList());
                    packetWrapper.write((Type)Type.SHORT, Short.valueOf((short)entryList.size()));
                    for (String entry : entryList)
                      packetWrapper.write(Type.STRING, entry); 
                  } 
                });
          }
        });
  }
  
  public void init(UserConnection userConnection) {}
}
