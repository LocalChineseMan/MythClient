package de.gerrygames.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk1_8;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import de.gerrygames.viarewind.ViaRewind;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.items.ReplacementRegistry1_8to1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.sound.Effect;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.sound.SoundRemapper;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.types.Chunk1_8Type;
import de.gerrygames.viarewind.utils.PacketUtil;

public class WorldPackets {
  public static void register(Protocol<ClientboundPackets1_9, ClientboundPackets1_8, ServerboundPackets1_9, ServerboundPackets1_8> protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(packetWrapper -> {
                  CompoundTag tag = (CompoundTag)packetWrapper.get(Type.NBT, 0);
                  if (tag != null && tag.contains("SpawnData")) {
                    String entity = (String)((CompoundTag)tag.get("SpawnData")).get("id").getValue();
                    tag.remove("SpawnData");
                    tag.put("entityId", (Tag)new StringTag(entity));
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(packetWrapper -> {
                  int block = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  if (block >= 219 && block <= 234)
                    packetWrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(block = 130)); 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            handler(packetWrapper -> {
                  int combined = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int replacedCombined = ReplacementRegistry1_8to1_9.replace(combined);
                  packetWrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(replacedCombined));
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.MULTI_BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.INT);
            map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            handler(packetWrapper -> {
                  for (BlockChangeRecord record : (BlockChangeRecord[])packetWrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                    int replacedCombined = ReplacementRegistry1_8to1_9.replace(record.getBlockId());
                    record.setBlockId(replacedCombined);
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(packetWrapper -> {
                  String name = (String)packetWrapper.get(Type.STRING, 0);
                  name = SoundRemapper.getOldName(name);
                  if (name == null) {
                    packetWrapper.cancel();
                  } else {
                    packetWrapper.set(Type.STRING, 0, name);
                  } 
                });
            map((Type)Type.VAR_INT, (Type)Type.NOTHING);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.FLOAT);
            map((Type)Type.UNSIGNED_BYTE);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(packetWrapper -> {
                  int count = ((Integer)packetWrapper.read((Type)Type.INT)).intValue();
                  packetWrapper.write((Type)Type.INT, Integer.valueOf(count));
                  for (int i = 0; i < count; i++) {
                    packetWrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                    packetWrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                    packetWrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                  } 
                });
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.UNLOAD_CHUNK, (ClientboundPacketType)ClientboundPackets1_8.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  int chunkX = ((Integer)packetWrapper.read((Type)Type.INT)).intValue();
                  int chunkZ = ((Integer)packetWrapper.read((Type)Type.INT)).intValue();
                  ClientWorld world = (ClientWorld)packetWrapper.user().get(ClientWorld.class);
                  packetWrapper.write((Type)new Chunk1_8Type(world), new Chunk1_8(chunkX, chunkZ));
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  Chunk1_8 chunk1_8;
                  ClientWorld world = (ClientWorld)packetWrapper.user().get(ClientWorld.class);
                  Chunk chunk = (Chunk)packetWrapper.read((Type)new Chunk1_9_1_2Type(world));
                  for (ChunkSection section : chunk.getSections()) {
                    if (section != null)
                      for (int i = 0; i < section.getPaletteSize(); i++) {
                        int block = section.getPaletteEntry(i);
                        int replacedBlock = ReplacementRegistry1_8to1_9.replace(block);
                        section.setPaletteEntry(i, replacedBlock);
                      }  
                  } 
                  if (chunk.isFullChunk() && chunk.getBitmask() == 0) {
                    boolean skylight = (world.getEnvironment() == Environment.NORMAL);
                    ChunkSection[] sections = new ChunkSection[16];
                    ChunkSectionImpl chunkSectionImpl = new ChunkSectionImpl(true);
                    sections[0] = (ChunkSection)chunkSectionImpl;
                    chunkSectionImpl.addPaletteEntry(0);
                    if (skylight)
                      chunkSectionImpl.getLight().setSkyLight(new byte[2048]); 
                    chunk1_8 = new Chunk1_8(chunk.getX(), chunk.getZ(), true, 1, sections, chunk.getBiomeData(), chunk.getBlockEntities());
                  } 
                  packetWrapper.write((Type)new Chunk1_8Type(world), chunk1_8);
                  UserConnection user = packetWrapper.user();
                  chunk1_8.getBlockEntities().forEach(());
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION);
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            handler(packetWrapper -> {
                  int id = ((Integer)packetWrapper.get((Type)Type.INT, 0)).intValue();
                  id = Effect.getOldId(id);
                  if (id == -1) {
                    packetWrapper.cancel();
                    return;
                  } 
                  packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(id));
                  if (id == 2001) {
                    int replacedBlock = ReplacementRegistry1_8to1_9.replace(((Integer)packetWrapper.get((Type)Type.INT, 1)).intValue());
                    packetWrapper.set((Type)Type.INT, 1, Integer.valueOf(replacedBlock));
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.SPAWN_PARTICLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(packetWrapper -> {
                  int type = ((Integer)packetWrapper.get((Type)Type.INT, 0)).intValue();
                  if (type > 41 && !ViaRewind.getConfig().isReplaceParticles()) {
                    packetWrapper.cancel();
                    return;
                  } 
                  if (type == 42) {
                    packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(24));
                  } else if (type == 43) {
                    packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(3));
                  } else if (type == 44) {
                    packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(34));
                  } else if (type == 45) {
                    packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(1));
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN, (Type)Type.NOTHING);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.SOUND, (ClientboundPacketType)ClientboundPackets1_8.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  int soundId = ((Integer)packetWrapper.read((Type)Type.VAR_INT)).intValue();
                  String sound = SoundRemapper.oldNameFromId(soundId);
                  if (sound == null) {
                    packetWrapper.cancel();
                  } else {
                    packetWrapper.write(Type.STRING, sound);
                  } 
                });
            handler(packetWrapper -> packetWrapper.read((Type)Type.VAR_INT));
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.FLOAT);
            map((Type)Type.UNSIGNED_BYTE);
          }
        });
  }
}
