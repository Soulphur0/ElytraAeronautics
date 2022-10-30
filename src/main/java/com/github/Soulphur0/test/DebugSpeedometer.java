package com.github.Soulphur0.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DebugSpeedometer {

    static Vec3d lastPos = Vec3d.ZERO;

    static public void displayDebugSpeedometer(Vec3d pos, World world){
        // Calculate speed.
        String message = "SPEED = " + (
                Math.round(
                        Math.sqrt(
                                Math.pow(pos.x-lastPos.x,2) +
                                        Math.pow(pos.y-lastPos.y,2) +
                                        Math.pow(pos.z-lastPos.z,2))
                                * 20 /* 20 ticks every second */ * 100.0)) /100.0 + "m/s";
        lastPos = pos;
        // Send speed info.
        if (world.isClient()){
            List<? extends PlayerEntity> players = world.getPlayers();
            players.forEach(player -> player.sendMessage(Text.of(message), true));
        }
    }

    // Para usar speedometer escribir esta línea en el método modifyVelocity de LivingEntityMixin
    // ! DEBUG SPEEDOMETER
    // DebugSpeedometer.displayDebugSpeedometer(super.getPos(), this.getWorld());
    // ! DEBUG SPEEDOMETER
}
