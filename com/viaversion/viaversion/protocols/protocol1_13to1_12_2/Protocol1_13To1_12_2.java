package com.viaversion.viaversion.protocols.protocol1_13to1_12_2;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticMappings;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.metadata.MetadataRewriter1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockConnectionStorage;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.TabCompleteTracker;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.util.ChatColorUtil;
import com.viaversion.viaversion.util.GsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Protocol1_13To1_12_2 extends AbstractProtocol<ClientboundPackets1_12_1, ClientboundPackets1_13, ServerboundPackets1_12_1, ServerboundPackets1_13> {
  public static final MappingData MAPPINGS = new MappingData();
  
  private static final Map<Character, Character> SCOREBOARD_TEAM_NAME_REWRITE = new HashMap<>();
  
  private static final Set<Character> FORMATTING_CODES = Sets.newHashSet((Object[])new Character[] { Character.valueOf('k'), Character.valueOf('l'), Character.valueOf('m'), Character.valueOf('n'), Character.valueOf('o'), Character.valueOf('r') });
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new MetadataRewriter1_13To1_12_2(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  public static final PacketHandler POS_TO_3_INT;
  
  private static final PacketHandler SEND_DECLARE_COMMANDS_AND_TAGS;
  
  static {
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('0'), Character.valueOf('g'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('1'), Character.valueOf('h'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('2'), Character.valueOf('i'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('3'), Character.valueOf('j'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('4'), Character.valueOf('p'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('5'), Character.valueOf('q'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('6'), Character.valueOf('s'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('7'), Character.valueOf('t'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('8'), Character.valueOf('u'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('9'), Character.valueOf('v'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('a'), Character.valueOf('w'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('b'), Character.valueOf('x'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('c'), Character.valueOf('y'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('d'), Character.valueOf('z'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('e'), Character.valueOf('!'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('f'), Character.valueOf('?'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('k'), Character.valueOf('#'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('l'), Character.valueOf('('));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('m'), Character.valueOf(')'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('n'), Character.valueOf(':'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('o'), Character.valueOf(';'));
    SCOREBOARD_TEAM_NAME_REWRITE.put(Character.valueOf('r'), Character.valueOf('/'));
    POS_TO_3_INT = (wrapper -> {
        Position position = (Position)wrapper.read(Type.POSITION);
        wrapper.write((Type)Type.INT, Integer.valueOf(position.getX()));
        wrapper.write((Type)Type.INT, Integer.valueOf(position.getY()));
        wrapper.write((Type)Type.INT, Integer.valueOf(position.getZ()));
      });
    SEND_DECLARE_COMMANDS_AND_TAGS = (w -> {
        w.create((PacketType)ClientboundPackets1_13.DECLARE_COMMANDS, ()).scheduleSend(Protocol1_13To1_12_2.class);
        w.create((PacketType)ClientboundPackets1_13.TAGS, ()).scheduleSend(Protocol1_13To1_12_2.class);
      });
  }
  
  public Protocol1_13To1_12_2() {
    super(ClientboundPackets1_12_1.class, ClientboundPackets1_13.class, ServerboundPackets1_12_1.class, ServerboundPackets1_13.class);
  }
  
  protected void registerPackets() {
    this.entityRewriter.register();
    this.itemRewriter.register();
    EntityPackets.register(this);
    WorldPackets.register((Protocol)this);
    registerClientbound(State.LOGIN, 0, 0, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT)));
          }
        });
    registerClientbound(State.STATUS, 0, 0, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String response = (String)wrapper.get(Type.STRING, 0);
                    try {
                      JsonObject json = (JsonObject)GsonUtil.getGson().fromJson(response, JsonObject.class);
                      if (json.has("favicon"))
                        json.addProperty("favicon", json.get("favicon").getAsString().replace("\n", "")); 
                      wrapper.set(Type.STRING, 0, GsonUtil.getGson().toJson((JsonElement)json));
                    } catch (JsonParseException e) {
                      e.printStackTrace();
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.STATISTICS, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int size = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    List<StatisticData> remappedStats = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                      String name = (String)wrapper.read(Type.STRING);
                      String[] split = name.split("\\.");
                      int categoryId = 0;
                      int newId = -1;
                      int value = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      if (split.length == 2) {
                        categoryId = 8;
                        Integer newIdRaw = (Integer)StatisticMappings.CUSTOM_STATS.get(name);
                        if (newIdRaw != null) {
                          newId = newIdRaw.intValue();
                        } else {
                          Via.getPlatform().getLogger().warning("Could not find 1.13 -> 1.12.2 statistic mapping for " + name);
                        } 
                      } else if (split.length > 2) {
                        String category = split[1];
                        switch (category) {
                          case "mineBlock":
                            categoryId = 0;
                            break;
                          case "craftItem":
                            categoryId = 1;
                            break;
                          case "useItem":
                            categoryId = 2;
                            break;
                          case "breakItem":
                            categoryId = 3;
                            break;
                          case "pickup":
                            categoryId = 4;
                            break;
                          case "drop":
                            categoryId = 5;
                            break;
                          case "killEntity":
                            categoryId = 6;
                            break;
                          case "entityKilledBy":
                            categoryId = 7;
                            break;
                        } 
                      } 
                      if (newId != -1)
                        remappedStats.add(new StatisticData(categoryId, newId, value)); 
                    } 
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(remappedStats.size()));
                    for (StatisticData stat : remappedStats) {
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(stat.getCategoryId()));
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(stat.getNewId()));
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(stat.getValue()));
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.BOSSBAR, new PacketRemapper() {
          public void registerMap() {
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action == 0 || action == 3)
                      ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT)); 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT)));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int index, length;
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(((TabCompleteTracker)wrapper.user().get(TabCompleteTracker.class)).getTransactionId()));
                    String input = ((TabCompleteTracker)wrapper.user().get(TabCompleteTracker.class)).getInput();
                    if (input.endsWith(" ") || input.isEmpty()) {
                      index = input.length();
                      length = 0;
                    } else {
                      int lastSpace = input.lastIndexOf(' ') + 1;
                      index = lastSpace;
                      length = input.length() - lastSpace;
                    } 
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(index));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(length));
                    int count = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int i = 0; i < count; i++) {
                      String suggestion = (String)wrapper.read(Type.STRING);
                      if (suggestion.startsWith("/") && index == 0)
                        suggestion = suggestion.substring(1); 
                      wrapper.write(Type.STRING, suggestion);
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.STRING);
            handler(wrapper -> ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT)));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.COOLDOWN, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int item = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int ticks = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.cancel();
                    if (item == 383) {
                      for (int i = 0; i < 44; ) {
                        Integer newItem = Integer.valueOf(Protocol1_13To1_12_2.this.getMappingData().getItemMappings().get(item << 16 | i));
                        if (newItem != null) {
                          PacketWrapper packet = wrapper.create((PacketType)ClientboundPackets1_13.COOLDOWN);
                          packet.write((Type)Type.VAR_INT, newItem);
                          packet.write((Type)Type.VAR_INT, Integer.valueOf(ticks));
                          packet.send(Protocol1_13To1_12_2.class);
                          i++;
                        } 
                      } 
                    } else {
                      for (int i = 0; i < 16; ) {
                        int newItem = Protocol1_13To1_12_2.this.getMappingData().getItemMappings().get(item << 4 | i);
                        if (newItem != -1) {
                          PacketWrapper packet = wrapper.create((PacketType)ClientboundPackets1_13.COOLDOWN);
                          packet.write((Type)Type.VAR_INT, Integer.valueOf(newItem));
                          packet.write((Type)Type.VAR_INT, Integer.valueOf(ticks));
                          packet.send(Protocol1_13To1_12_2.class);
                          i++;
                        } 
                      } 
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.DISCONNECT, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT)));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int data = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    if (id == 1010) {
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(Protocol1_13To1_12_2.this.getMappingData().getItemMappings().get(data << 4)));
                    } else if (id == 2001) {
                      int blockId = data & 0xFFF;
                      int blockData = data >> 12;
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(WorldPackets.toNewId(blockId << 4 | blockData)));
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    wrapper.user().getEntityTracker(Protocol1_13To1_12_2.class).addEntity(entityId, (EntityType)Entity1_13Types.EntityType.PLAYER);
                    ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    clientChunks.setEnvironment(dimensionId);
                  }
                });
            handler(Protocol1_13To1_12_2.SEND_DECLARE_COMMANDS_AND_TAGS);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.CRAFT_RECIPE_RESPONSE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.BYTE);
            handler(wrapper -> wrapper.write(Type.STRING, "viaversion:legacy/" + wrapper.read((Type)Type.VAR_INT)));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.COMBAT_EVENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue() == 2) {
                      wrapper.passthrough((Type)Type.VAR_INT);
                      wrapper.passthrough((Type)Type.INT);
                      ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT));
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int iconCount = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int i = 0; i < iconCount; i++) {
                      byte directionAndType = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                      int type = (directionAndType & 0xF0) >> 4;
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(type));
                      wrapper.passthrough((Type)Type.BYTE);
                      wrapper.passthrough((Type)Type.BYTE);
                      byte direction = (byte)(directionAndType & 0xF);
                      wrapper.write((Type)Type.BYTE, Byte.valueOf(direction));
                      wrapper.write(Type.OPTIONAL_COMPONENT, null);
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.UNLOCK_RECIPES, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    for (int i = 0; i < ((action == 0) ? 2 : 1); i++) {
                      int[] ids = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                      String[] stringIds = new String[ids.length];
                      for (int j = 0; j < ids.length; j++)
                        stringIds[j] = "viaversion:legacy/" + ids[j]; 
                      wrapper.write(Type.STRING_ARRAY, stringIds);
                    } 
                    if (action == 0)
                      wrapper.create((PacketType)ClientboundPackets1_13.DECLARE_RECIPES, (PacketHandler)new Object(this))
                        
                        .send(Protocol1_13To1_12_2.class); 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    clientWorld.setEnvironment(dimensionId);
                    if (Via.getConfig().isServersideBlockConnections())
                      ConnectionData.clearBlockStorage(wrapper.user()); 
                  }
                });
            handler(Protocol1_13To1_12_2.SEND_DECLARE_COMMANDS_AND_TAGS);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.SCOREBOARD_OBJECTIVE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    byte mode = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    if (mode == 0 || mode == 2) {
                      String value = (String)wrapper.read(Type.STRING);
                      wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(value));
                      String type = (String)wrapper.read(Type.STRING);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(type.equals("integer") ? 0 : 1));
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.TEAMS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    byte action = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    if (action == 0 || action == 2) {
                      String displayName = (String)wrapper.read(Type.STRING);
                      wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(displayName));
                      String prefix = (String)wrapper.read(Type.STRING);
                      String suffix = (String)wrapper.read(Type.STRING);
                      wrapper.passthrough((Type)Type.BYTE);
                      wrapper.passthrough(Type.STRING);
                      wrapper.passthrough(Type.STRING);
                      int colour = ((Byte)wrapper.read((Type)Type.BYTE)).intValue();
                      if (colour == -1)
                        colour = 21; 
                      if (Via.getConfig().is1_13TeamColourFix()) {
                        char lastColorChar = Protocol1_13To1_12_2.this.getLastColorChar(prefix);
                        colour = ChatColorUtil.getColorOrdinal(lastColorChar);
                        suffix = '§' + Character.toString(lastColorChar) + suffix;
                      } 
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(colour));
                      wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(prefix));
                      wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(suffix));
                    } 
                    if (action == 0 || action == 3 || action == 4) {
                      String[] names = (String[])wrapper.read(Type.STRING_ARRAY);
                      for (int i = 0; i < names.length; i++)
                        names[i] = Protocol1_13To1_12_2.this.rewriteTeamMemberName(names[i]); 
                      wrapper.write(Type.STRING_ARRAY, names);
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.UPDATE_SCORE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String displayName = (String)wrapper.read(Type.STRING);
                    displayName = Protocol1_13To1_12_2.this.rewriteTeamMemberName(displayName);
                    wrapper.write(Type.STRING, displayName);
                    byte action = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                    wrapper.write((Type)Type.BYTE, Byte.valueOf(action));
                    wrapper.passthrough(Type.STRING);
                    if (action != 1)
                      wrapper.passthrough((Type)Type.VAR_INT); 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.TITLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action >= 0 && action <= 2)
                      ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT)); 
                  }
                });
          }
        });
    (new SoundRewriter((Protocol)this)).registerSound((ClientboundPacketType)ClientboundPackets1_12_1.SOUND);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.TAB_LIST, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT));
                    ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT));
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.ADVANCEMENTS, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.passthrough((Type)Type.BOOLEAN);
                    int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int i = 0; i < size; i++) {
                      wrapper.passthrough(Type.STRING);
                      if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                        wrapper.passthrough(Type.STRING); 
                      if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue()) {
                        ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT));
                        ChatRewriter.processTranslate((JsonElement)wrapper.passthrough(Type.COMPONENT));
                        Item icon = (Item)wrapper.read(Type.ITEM);
                        Protocol1_13To1_12_2.this.itemRewriter.handleItemToClient(icon);
                        wrapper.write(Type.FLAT_ITEM, icon);
                        wrapper.passthrough((Type)Type.VAR_INT);
                        int flags = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                        if ((flags & 0x1) != 0)
                          wrapper.passthrough(Type.STRING); 
                        wrapper.passthrough((Type)Type.FLOAT);
                        wrapper.passthrough((Type)Type.FLOAT);
                      } 
                      wrapper.passthrough(Type.STRING_ARRAY);
                      int arrayLength = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                      for (int array = 0; array < arrayLength; array++)
                        wrapper.passthrough(Type.STRING_ARRAY); 
                    } 
                  }
                });
          }
        });
    cancelServerbound(State.LOGIN, 2);
    cancelServerbound(ServerboundPackets1_13.QUERY_BLOCK_NBT);
    registerServerbound(ServerboundPackets1_13.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (Via.getConfig().isDisable1_13AutoComplete())
                      wrapper.cancel(); 
                    int tid = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    ((TabCompleteTracker)wrapper.user().get(TabCompleteTracker.class)).setTransactionId(tid);
                  }
                });
            map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                  public String transform(PacketWrapper wrapper, String inputValue) {
                    ((TabCompleteTracker)wrapper.user().get(TabCompleteTracker.class)).setInput(inputValue);
                    return "/" + inputValue;
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    wrapper.write(Type.OPTIONAL_POSITION, null);
                    if (!wrapper.isCancelled() && Via.getConfig().get1_13TabCompleteDelay() > 0) {
                      TabCompleteTracker tracker = (TabCompleteTracker)wrapper.user().get(TabCompleteTracker.class);
                      wrapper.cancel();
                      tracker.setTimeToSend(System.currentTimeMillis() + Via.getConfig().get1_13TabCompleteDelay() * 50L);
                      tracker.setLastTabComplete((String)wrapper.get(Type.STRING, 0));
                    } 
                  }
                });
          }
        });
    registerServerbound(ServerboundPackets1_13.EDIT_BOOK, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item item = (Item)wrapper.read(Type.FLAT_ITEM);
                    boolean isSigning = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                    Protocol1_13To1_12_2.this.itemRewriter.handleItemToServer(item);
                    wrapper.write(Type.STRING, isSigning ? "MC|BSign" : "MC|BEdit");
                    wrapper.write(Type.ITEM, item);
                  }
                });
          }
        });
    cancelServerbound(ServerboundPackets1_13.ENTITY_NBT_REQUEST);
    registerServerbound(ServerboundPackets1_13.PICK_ITEM, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write(Type.STRING, "MC|PickItem");
                  }
                });
          }
        });
    registerServerbound(ServerboundPackets1_13.CRAFT_RECIPE_REQUEST, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.BYTE);
            handler(wrapper -> {
                  String s = (String)wrapper.read(Type.STRING);
                  Integer id;
                  if (s.length() < 19 || (id = Ints.tryParse(s.substring(18))) == null) {
                    wrapper.cancel();
                    return;
                  } 
                  wrapper.write((Type)Type.VAR_INT, id);
                });
          }
        });
    registerServerbound(ServerboundPackets1_13.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (type == 0) {
                      String s = (String)wrapper.read(Type.STRING);
                      Integer id;
                      if (s.length() < 19 || (id = Ints.tryParse(s.substring(18))) == null) {
                        wrapper.cancel();
                        return;
                      } 
                      wrapper.write((Type)Type.INT, id);
                    } 
                    if (type == 1) {
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.read((Type)Type.BOOLEAN);
                      wrapper.read((Type)Type.BOOLEAN);
                    } 
                  }
                });
          }
        });
    registerServerbound(ServerboundPackets1_13.RENAME_ITEM, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> wrapper.write(Type.STRING, "MC|ItemName"));
          }
        });
    registerServerbound(ServerboundPackets1_13.SELECT_TRADE, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> wrapper.write(Type.STRING, "MC|TrSel"));
            map((Type)Type.VAR_INT, (Type)Type.INT);
          }
        });
    registerServerbound(ServerboundPackets1_13.SET_BEACON_EFFECT, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> wrapper.write(Type.STRING, "MC|Beacon"));
            map((Type)Type.VAR_INT, (Type)Type.INT);
            map((Type)Type.VAR_INT, (Type)Type.INT);
          }
        });
    registerServerbound(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> wrapper.write(Type.STRING, "MC|AutoCmd"));
            handler(Protocol1_13To1_12_2.POS_TO_3_INT);
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int mode = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    byte flags = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                    String stringMode = (mode == 0) ? "SEQUENCE" : ((mode == 1) ? "AUTO" : "REDSTONE");
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((flags & 0x1) != 0)));
                    wrapper.write(Type.STRING, stringMode);
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((flags & 0x2) != 0)));
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((flags & 0x4) != 0)));
                  }
                });
          }
        });
    registerServerbound(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK_MINECART, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write(Type.STRING, "MC|AdvCmd");
                    wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)1));
                  }
                });
            map((Type)Type.VAR_INT, (Type)Type.INT);
          }
        });
    registerServerbound(ServerboundPackets1_13.UPDATE_STRUCTURE_BLOCK, (ServerboundPacketType)ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> wrapper.write(Type.STRING, "MC|Struct"));
            handler(Protocol1_13To1_12_2.POS_TO_3_INT);
            map((Type)Type.VAR_INT, new ValueTransformer<Integer, Byte>((Type)Type.BYTE) {
                  public Byte transform(PacketWrapper wrapper, Integer action) throws Exception {
                    return Byte.valueOf((byte)(action.intValue() + 1));
                  }
                });
            map((Type)Type.VAR_INT, new ValueTransformer<Integer, String>(Type.STRING) {
                  public String transform(PacketWrapper wrapper, Integer mode) throws Exception {
                    return (mode.intValue() == 0) ? "SAVE" : (
                      (mode.intValue() == 1) ? "LOAD" : (
                      (mode.intValue() == 2) ? "CORNER" : 
                      "DATA"));
                  }
                });
            map(Type.STRING);
            map((Type)Type.BYTE, (Type)Type.INT);
            map((Type)Type.BYTE, (Type)Type.INT);
            map((Type)Type.BYTE, (Type)Type.INT);
            map((Type)Type.BYTE, (Type)Type.INT);
            map((Type)Type.BYTE, (Type)Type.INT);
            map((Type)Type.BYTE, (Type)Type.INT);
            map((Type)Type.VAR_INT, new ValueTransformer<Integer, String>(Type.STRING) {
                  public String transform(PacketWrapper wrapper, Integer mirror) throws Exception {
                    return (mirror.intValue() == 0) ? "NONE" : (
                      (mirror.intValue() == 1) ? "LEFT_RIGHT" : 
                      "FRONT_BACK");
                  }
                });
            map((Type)Type.VAR_INT, new ValueTransformer<Integer, String>(Type.STRING) {
                  public String transform(PacketWrapper wrapper, Integer rotation) throws Exception {
                    return (rotation.intValue() == 0) ? "NONE" : (
                      (rotation.intValue() == 1) ? "CLOCKWISE_90" : (
                      (rotation.intValue() == 2) ? "CLOCKWISE_180" : 
                      "COUNTERCLOCKWISE_90"));
                  }
                });
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    float integrity = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    long seed = ((Long)wrapper.read((Type)Type.VAR_LONG)).longValue();
                    byte flags = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((flags & 0x1) != 0)));
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((flags & 0x2) != 0)));
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((flags & 0x4) != 0)));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(integrity));
                    wrapper.write((Type)Type.VAR_LONG, Long.valueOf(seed));
                  }
                });
          }
        });
  }
  
  protected void onMappingDataLoaded() {
    ConnectionData.init();
    RecipeData.init();
    BlockIdData.init();
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(userConnection, (EntityType)Entity1_13Types.EntityType.PLAYER));
    userConnection.put((StorableObject)new TabCompleteTracker());
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StorableObject)new ClientWorld(userConnection)); 
    userConnection.put((StorableObject)new BlockStorage());
    if (Via.getConfig().isServersideBlockConnections() && 
      Via.getManager().getProviders().get(BlockConnectionProvider.class) instanceof com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.PacketBlockConnectionProvider)
      userConnection.put((StorableObject)new BlockConnectionStorage()); 
  }
  
  public void register(ViaProviders providers) {
    providers.register(BlockEntityProvider.class, (Provider)new BlockEntityProvider());
    providers.register(PaintingProvider.class, (Provider)new PaintingProvider());
  }
  
  public char getLastColorChar(String input) {
    int length = input.length();
    for (int index = length - 1; index > -1; index--) {
      char section = input.charAt(index);
      if (section == '§' && index < length - 1) {
        char c = input.charAt(index + 1);
        if (ChatColorUtil.isColorCode(c) && !FORMATTING_CODES.contains(Character.valueOf(c)))
          return c; 
      } 
    } 
    return 'r';
  }
  
  protected String rewriteTeamMemberName(String name) {
    if (ChatColorUtil.stripColor(name).isEmpty()) {
      StringBuilder newName = new StringBuilder();
      for (int i = 1; i < name.length(); i += 2) {
        char colorChar = name.charAt(i);
        Character rewrite = SCOREBOARD_TEAM_NAME_REWRITE.get(Character.valueOf(colorChar));
        if (rewrite == null)
          rewrite = Character.valueOf(colorChar); 
        newName.append('§').append(rewrite);
      } 
      name = newName.toString();
    } 
    return name;
  }
  
  public static int[] toPrimitive(Integer[] array) {
    int[] prim = new int[array.length];
    for (int i = 0; i < array.length; i++)
      prim[i] = array[i].intValue(); 
    return prim;
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
