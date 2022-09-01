package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import java.util.UUID;

public class BaseProtocol1_16 extends BaseProtocol1_7 {
  protected UUID passthroughLoginUUID(PacketWrapper wrapper) throws Exception {
    return (UUID)wrapper.passthrough(Type.UUID_INT_ARRAY);
  }
}
