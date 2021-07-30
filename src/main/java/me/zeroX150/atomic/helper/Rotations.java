package me.zeroX150.atomic.helper;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.PacketEvents;
import me.zeroX150.atomic.mixin.network.IPlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Rotations {
    static boolean enabled = false;
    private static float clientPitch;
    private static float clientYaw;
    private static long lastModificationTime = 0;
    private static Vec3d targetV3;

    static {
        Events.Packets.registerEventHandler(PacketEvents.PACKET_SEND, event -> {
            if (isEnabled() && event.getPacket() instanceof PlayerMoveC2SPacket packet) {
                IPlayerMoveC2SPacketAccessor accessor = (IPlayerMoveC2SPacketAccessor) packet;
                accessor.setPitch(getClientPitch());
                accessor.setYaw(getClientYaw());
            }
        });
    }

    static void timeoutCheck() {
        if (System.currentTimeMillis() - lastModificationTime > 1000) disable();
        else enable();
    }

    static void tick() {
        timeoutCheck();
    }

    public static void lookAtV3(Vec3d target) {
        targetV3 = target;
        lastModificationTime = System.currentTimeMillis();
    }

    public static void update() {
        tick();
        if (targetV3 != null) {
            double vec = 57.2957763671875;
            Vec3d target = targetV3.subtract(Atomic.client.player.getEyePos());
            double square = Math.sqrt(target.x * target.x + target.z * target.z);
            float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(target.y, square) * vec)));
            clientYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(target.z, target.x) * vec) - 90.0F);
            clientPitch = pitch;
        }
    }

    public static float getClientPitch() {
        return clientPitch;
    }

    public static void setClientPitch(float clientPitch) {
        lastModificationTime = System.currentTimeMillis();
        Rotations.clientPitch = clientPitch;
    }

    public static float getClientYaw() {
        return clientYaw;
    }

    public static void setClientYaw(float clientYaw) {
        lastModificationTime = System.currentTimeMillis();
        Rotations.clientYaw = clientYaw;
    }

    public static void enable() {
        enabled = true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void disable() {
        enabled = false;
        targetV3 = null;
    }
}

