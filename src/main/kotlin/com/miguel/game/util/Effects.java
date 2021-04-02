/*
 *
 *  * Copyright (©) RobotoHG-1.8
 *  *
 *  * Projeto desenvolvido por Miguell Rodrigues
 *  * Todos os direitos reservados
 *  *
 *  * Modificado em: 23/05/2020 18:17
 *
 */

package com.miguel.game.util;

import com.miguel.RobotoHG;
import com.miguel.game.manager.GameManager;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Effects {

    public static BukkitTask coneEffect(final Location location) {
        return new BukkitRunnable() {
            final double radius = 1.7;
            double phi = 0;

            public void run() {
                phi = phi + Math.PI / 8;
                double x, y, z;

                for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                    for (double i = 0; i <= 1; i += 1) {

                        x = 0.4 * (2 * Math.PI - t) * radius * Math.cos(t + phi + i * Math.PI);
                        y = 1.6 * t;
                        z = 0.4 * (2 * Math.PI - t) * radius * Math.sin(t + phi + i * Math.PI);

                        location.add(x, y, z);

                        sendParticle(EnumParticle.PORTAL, location, 0);

                        location.subtract(x, y, z);
                    }
                }
            }
        }.runTaskTimerAsynchronously(RobotoHG.Companion.getINSTANCE(), 0, 0);
    }

    public static BukkitTask frostLord(final Player player) {
        return new BukkitRunnable() {
            final double pi = Math.PI;
            double t = 0;

            public void run() {
                t += pi / 16;
                Location loc = player.getLocation();
                for (double phi = 0; phi <= 2 * pi; phi += pi / 2) {
                    double x = 0.3 * (4 * pi - t) * Math.cos(t + phi);
                    double y = 0.2 * t;
                    double z = 0.3 * (4 * pi - t) * Math.sin(t + phi);

                    loc.add(x, y, z);
                    sendParticle(EnumParticle.FLAME, loc, 0);
                    loc.subtract(x, y, z);

                    if (t >= 4 * pi) {
                        this.cancel();
                        loc.add(x, y, z);
                        sendParticle(EnumParticle.FLAME, loc, 1);
                        loc.subtract(x, y, z);
                    }
                }
            }
        }.runTaskTimerAsynchronously(RobotoHG.Companion.getINSTANCE(), 0L, 1L);
    }

    public static BukkitTask sphere(final Player player) {
        Location location = player.getLocation();
        return new BukkitRunnable() {
            final double r = 1.5;
            double phi = 0;

            @Override
            public void run() {
                phi += Math.PI / 10;

                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {

                    double x = r * Math.cos(theta) * Math.sin(phi);
                    double y = r * Math.cos(phi) + 1.5;
                    double z = r * Math.sin(theta) * Math.sin(phi);

                    location.add(x, y, z);

                    sendParticle(EnumParticle.DRIP_WATER, location, 1);

                    location.subtract(x, y, z);
                }

                if (phi > Math.PI)
                    cancel();
            }
        }.runTaskTimerAsynchronously(RobotoHG.Companion.getINSTANCE(), 0L, 1L);
    }

    public static void test(final Player player) {
        Location location = player.getLocation().add(0, 0.5, 0);

        /*for(double j = 0; j < 1.5; j += 0.5) {
            for (int degree = 0; degree < 360; degree++) {
                double radians = Math.toRadians(degree);

                double x = j * Math.cos(radians);
                double z = j * Math.sin(radians);

                location.add(x, 0, z);

                location.getWorld().spawnParticle(Particle.DRIP_WATER, location.getX(), location.getY(), location.getZ(), 1);

                location.subtract(x, 0, z);
            }
        }*/

        new BukkitRunnable() {

            final double radius = 1.5;

            @Override
            public void run() {
                Location location1 = player.getLocation();

                for (double j = 0; j < 3; j++) {
                    for (int degree = 0; degree < 360; degree++) {
                        double radians = Math.toRadians(degree);

                        double x = radius * Math.cos(radians);
                        double z = radius * Math.sin(radians);

                        location1.add(x, j, z);

                        sendParticle(EnumParticle.REDSTONE, location1, 1);

                        location1.subtract(x, j, z);
                    }
                }
            }
        }.runTaskTimer(RobotoHG.Companion.getINSTANCE(), 0, 15);
    }

    public static void sendParticle(EnumParticle particle, Location location, int i) {
        PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(
                particle,
                true,
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                0,
                0,
                0,
                0,
                i
        );

        for (Player onlinePlayer : GameManager.INSTANCE.getPlayers()) {
            ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(particles);
        }
    }
}
