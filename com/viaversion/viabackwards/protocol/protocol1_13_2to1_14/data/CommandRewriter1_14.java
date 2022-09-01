package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.data;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.rewriter.CommandRewriter;

public class CommandRewriter1_14 extends CommandRewriter {
  public CommandRewriter1_14(Protocol protocol) {
    super(protocol);
    this.parserHandlers.put("minecraft:nbt_tag", wrapper -> wrapper.write((Type)Type.VAR_INT, Integer.valueOf(2)));
    this.parserHandlers.put("minecraft:time", wrapper -> {
          wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)1));
          wrapper.write((Type)Type.INT, Integer.valueOf(0));
        });
  }
  
  protected String handleArgumentType(String argumentType) {
    switch (argumentType) {
      case "minecraft:nbt_compound_tag":
        return "minecraft:nbt";
      case "minecraft:nbt_tag":
        return "brigadier:string";
      case "minecraft:time":
        return "brigadier:integer";
    } 
    return super.handleArgumentType(argumentType);
  }
}
