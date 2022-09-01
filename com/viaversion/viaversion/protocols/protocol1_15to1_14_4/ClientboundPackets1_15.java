package com.viaversion.viaversion.protocols.protocol1_15to1_14_4;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;

public enum ClientboundPackets1_15 implements ClientboundPacketType {
  SPAWN_ENTITY, SPAWN_EXPERIENCE_ORB, SPAWN_GLOBAL_ENTITY, SPAWN_MOB, SPAWN_PAINTING, SPAWN_PLAYER, ENTITY_ANIMATION, STATISTICS, ACKNOWLEDGE_PLAYER_DIGGING, BLOCK_BREAK_ANIMATION, BLOCK_ENTITY_DATA, BLOCK_ACTION, BLOCK_CHANGE, BOSSBAR, SERVER_DIFFICULTY, CHAT_MESSAGE, MULTI_BLOCK_CHANGE, TAB_COMPLETE, DECLARE_COMMANDS, WINDOW_CONFIRMATION, CLOSE_WINDOW, WINDOW_ITEMS, WINDOW_PROPERTY, SET_SLOT, COOLDOWN, PLUGIN_MESSAGE, NAMED_SOUND, DISCONNECT, ENTITY_STATUS, EXPLOSION, UNLOAD_CHUNK, GAME_EVENT, OPEN_HORSE_WINDOW, KEEP_ALIVE, CHUNK_DATA, EFFECT, SPAWN_PARTICLE, UPDATE_LIGHT, JOIN_GAME, MAP_DATA, TRADE_LIST, ENTITY_POSITION, ENTITY_POSITION_AND_ROTATION, ENTITY_ROTATION, ENTITY_MOVEMENT, VEHICLE_MOVE, OPEN_BOOK, OPEN_WINDOW, OPEN_SIGN_EDITOR, CRAFT_RECIPE_RESPONSE, PLAYER_ABILITIES, COMBAT_EVENT, PLAYER_INFO, FACE_PLAYER, PLAYER_POSITION, UNLOCK_RECIPES, DESTROY_ENTITIES, REMOVE_ENTITY_EFFECT, RESOURCE_PACK, RESPAWN, ENTITY_HEAD_LOOK, SELECT_ADVANCEMENTS_TAB, WORLD_BORDER, CAMERA, HELD_ITEM_CHANGE, UPDATE_VIEW_POSITION, UPDATE_VIEW_DISTANCE, DISPLAY_SCOREBOARD, ENTITY_METADATA, ATTACH_ENTITY, ENTITY_VELOCITY, ENTITY_EQUIPMENT, SET_EXPERIENCE, UPDATE_HEALTH, SCOREBOARD_OBJECTIVE, SET_PASSENGERS, TEAMS, UPDATE_SCORE, SPAWN_POSITION, TIME_UPDATE, TITLE, ENTITY_SOUND, SOUND, STOP_SOUND, TAB_LIST, NBT_QUERY, COLLECT_ITEM, ENTITY_TELEPORT, ADVANCEMENTS, ENTITY_PROPERTIES, ENTITY_EFFECT, DECLARE_RECIPES, TAGS;
  
  public int getId() {
    return ordinal();
  }
  
  public String getName() {
    return name();
  }
}
