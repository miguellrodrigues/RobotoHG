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

package com.miguel.scoreboard.major;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class NMS {

    static final Field PLAYER_SCORES;
    static final Constructor<?> PACKET_SCORE_REMOVE;
    static final Constructor<?> PACKET_SCORE;
    static final Constructor<?> SB_SCORE;
    static final Method SB_SCORE_SET;
    static final Constructor<?> PACKET_OBJ;
    static final Constructor<?> PACKET_DISPLAY;
    static final Field PLAYER_CONNECTION;
    static final Method SEND_PACKET;
    private static final String packageName;
    private static final Version version;
    private static final Map<Class<?>, Method> handles = new HashMap<>();

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();

        String ver = name.substring(name.lastIndexOf('.') + 1);
        version = new Version(ver);

        packageName = "net.minecraft.server." + ver;

        Field playerScores = null;

        Constructor<?> packetScoreRemove = null;
        Constructor<?> packetScore = null;

        Constructor<?> sbScore = null;
        Method sbScoreSet = null;

        Constructor<?> packetObj = null;
        Constructor<?> packetDisplay = null;

        Field playerConnection = null;
        Method sendPacket = null;

        try {
            Class<?> packetScoreClass = getClass("PacketPlayOutScoreboardScore");
            Class<?> packetDisplayClass = getClass("PacketPlayOutScoreboardDisplayObjective");
            Class<?> packetObjClass = getClass("PacketPlayOutScoreboardObjective");

            Class<?> scoreClass = getClass("ScoreboardScore");

            Class<?> sbClass = getClass("Scoreboard");
            Class<?> objClass = getClass("ScoreboardObjective");

            Class<?> playerClass = getClass("EntityPlayer");
            Class<?> playerConnectionClass = getClass("PlayerConnection");
            Class<?> packetClass = getClass("Packet");

            playerScores = sbClass.getDeclaredField("playerScores");
            playerScores.setAccessible(true);

            sbScore = scoreClass.getConstructor(sbClass, objClass, String.class);
            sbScoreSet = scoreClass.getMethod("setScore", int.class);

            if (version.getMajor().equals("1.7"))
                packetScore = packetScoreClass.getConstructor(scoreClass, int.class);
            else {
                packetScore = packetScoreClass.getConstructor(scoreClass);
                packetScoreRemove = packetScoreClass.getConstructor(String.class, objClass);
            }

            packetObj = packetObjClass.getConstructor(objClass, int.class);
            packetDisplay = packetDisplayClass.getConstructor(int.class, objClass);

            playerConnection = playerClass.getField("playerConnection");
            sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
        }

        PLAYER_SCORES = playerScores;

        PACKET_SCORE_REMOVE = packetScoreRemove;
        PACKET_SCORE = packetScore;

        SB_SCORE = sbScore;
        SB_SCORE_SET = sbScoreSet;

        PACKET_OBJ = packetObj;
        PACKET_DISPLAY = packetDisplay;

        PLAYER_CONNECTION = playerConnection;
        SEND_PACKET = sendPacket;
    }

    public static Version getVersion() {
        return version;
    }

    public static Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(packageName + "." + name);
    }

    public static Object getHandle(Object obj) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        Class<?> clazz = obj.getClass();

        if (!handles.containsKey(clazz)) {
            Method method = clazz.getDeclaredMethod("getHandle");

            if (!method.isAccessible())
                method.setAccessible(true);

            handles.put(clazz, method);
        }

        return handles.get(clazz).invoke(obj);
    }

    public static void sendPacket(Object packet, Player... players) {
        for (Player p : players) {
            try {
                Object playerConnection = PLAYER_CONNECTION.get(getHandle(p));
                SEND_PACKET.invoke(playerConnection, packet);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            }
        }
    }

    public static class Version {

        private final String name;

        private final String major;
        private final String minor;

        Version(String name) {
            this.name = name;

            String[] splitted = name.split("_");

            this.major = splitted[0].substring(1) + "." + splitted[1];
            this.minor = splitted[2].substring(1);
        }

        public String getName() {
            return name;
        }

        public String getMajor() {
            return major;
        }

        public String getMinor() {
            return minor;
        }

    }

}
