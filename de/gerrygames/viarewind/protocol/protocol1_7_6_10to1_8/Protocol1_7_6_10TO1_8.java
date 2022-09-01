package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.ClientChunks;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets.EntityPackets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets.InventoryPackets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets.PlayerPackets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets.ScoreboardPackets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets.SpawnPackets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets.WorldPackets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.provider.CompressionHandlerProvider;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.CompressionSendStorage;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerAbilities;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerPosition;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.Scoreboard;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.Windows;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.WorldBorder;
import de.gerrygames.viarewind.utils.Ticker;

public class Protocol1_7_6_10TO1_8 extends AbstractProtocol<ClientboundPackets1_8, ClientboundPackets1_7, ServerboundPackets1_8, ServerboundPackets1_7> {
  public Protocol1_7_6_10TO1_8() {
    super(ClientboundPackets1_8.class, ClientboundPackets1_7.class, ServerboundPackets1_8.class, ServerboundPackets1_7.class);
  }
  
  protected void registerPackets() {
    EntityPackets.register(this);
    InventoryPackets.register(this);
    PlayerPackets.register(this);
    ScoreboardPackets.register(this);
    SpawnPackets.register(this);
    WorldPackets.register(this);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_8.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT, (Type)Type.INT);
          }
        });
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_8.SET_COMPRESSION);
    registerServerbound(ServerboundPackets1_7.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT, (Type)Type.VAR_INT);
          }
        });
    registerClientbound(State.LOGIN, 1, 1, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY);
            map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY);
          }
        });
    registerClientbound(State.LOGIN, 3, 3, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  ((CompressionHandlerProvider)Via.getManager().getProviders().get(CompressionHandlerProvider.class)).handleSetCompression(packetWrapper.user(), ((Integer)packetWrapper.read((Type)Type.VAR_INT)).intValue());
                  packetWrapper.cancel();
                });
          }
        });
    registerServerbound(State.LOGIN, 1, 1, new PacketRemapper() {
          public void registerMap() {
            map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE);
            map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE);
          }
        });
  }
  
  public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
    ((CompressionHandlerProvider)Via.getManager().getProviders().get(CompressionHandlerProvider.class))
      .handleTransform(packetWrapper.user());
    super.transform(direction, state, packetWrapper);
  }
  
  public void init(UserConnection userConnection) {
    Ticker.init();
    userConnection.put((StorableObject)new Windows(userConnection));
    userConnection.put((StorableObject)new EntityTracker(userConnection));
    userConnection.put((StorableObject)new PlayerPosition(userConnection));
    userConnection.put((StorableObject)new GameProfileStorage(userConnection));
    userConnection.put((StorableObject)new ClientChunks(userConnection));
    userConnection.put((StorableObject)new Scoreboard(userConnection));
    userConnection.put((StorableObject)new CompressionSendStorage(userConnection));
    userConnection.put((StorableObject)new WorldBorder(userConnection));
    userConnection.put((StorableObject)new PlayerAbilities(userConnection));
    userConnection.put((StorableObject)new ClientWorld(userConnection));
  }
  
  public void register(ViaProviders providers) {
    providers.register(CompressionHandlerProvider.class, (Provider)new CompressionHandlerProvider());
  }
}
