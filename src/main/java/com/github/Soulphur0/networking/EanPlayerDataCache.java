package com.github.Soulphur0.networking;

import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EanPlayerDataCache {

    public static Map<UUID, Boolean> canPlayerLoadChunksMap = new HashMap<>();

    public static void setOrUpdateCanPlayerLoadChunks(PacketByteBuf buf){
        setOrUpdateCanPlayerLoadChunks(buf.readUuid(), buf.readBoolean());
    }

    public static void setOrUpdateCanPlayerLoadChunks(UUID playerUuid, boolean canLoadChunks){
        canPlayerLoadChunksMap.put(playerUuid, canLoadChunks);
    }

    public static boolean canPlayerLoadChunks(UUID playerUuid){
        if (canPlayerLoadChunksMap.get(playerUuid) != null)
            return canPlayerLoadChunksMap.get(playerUuid);
        else
            return true;
    }
}
