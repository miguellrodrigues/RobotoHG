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

import com.miguel.scoreboard.lib.type.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPlayerBoard {

    private final Player player;
    private Scoreboard scoreboard;

    private String name;

    private Objective objective;
    private Objective buffer;

    private List<Entry> lines = new ArrayList<>();

    private boolean deleted = false;

    public BPlayerBoard(Player player, String name) {
        this(player, null, name);
    }

    public BPlayerBoard(Player player, Scoreboard scoreboard, String name) {
        this.player = player;
        this.scoreboard = scoreboard;

        if (this.scoreboard == null) {
            Scoreboard sb = player.getScoreboard();

            if (sb == null || sb == Bukkit.getScoreboardManager().getMainScoreboard())
                sb = Bukkit.getScoreboardManager().getNewScoreboard();

            this.scoreboard = sb;
        }

        this.name = name;

        String subName = player.getName().length() <= 14
                ? player.getName()
                : player.getName().substring(0, 14);

        this.objective = this.scoreboard.getObjective("sb" + subName);
        this.buffer = this.scoreboard.getObjective("bf" + subName);

        if (this.objective == null)
            this.objective = this.scoreboard.registerNewObjective("sb" + subName, "dummy");
        if (this.buffer == null)
            this.buffer = this.scoreboard.registerNewObjective("bf" + subName, "dummy");

        this.objective.setDisplayName(name);
        sendObjective(this.objective, ObjectiveMode.CREATE);
        sendObjectiveDisplay(this.objective);

        this.buffer.setDisplayName(name);
        sendObjective(this.buffer, ObjectiveMode.CREATE);

        this.player.setScoreboard(this.scoreboard);
    }

    public Entry get(Integer score) {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        for (Entry entry : this.lines) {
            if (entry.getPosition() == score) {
                return entry;
            }
        }
        return null;
    }

    public BPlayerBoard reset() {
        delete();
        return new BPlayerBoard(player, name);
    	/*this.scoreboard.resetScores(name);
        this.lines.clear();

    	try {
             Object sbHandle = NMS.getHandle(scoreboard);

             Map scores = (Map) NMS.PLAYER_SCORES.get(sbHandle);
             scores.clear();
    	 }catch(Exception e1) {
    		 e1.printStackTrace();
    	 }*/
    }

    public void set(String name, Integer score) {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        name = name.replace("&", "§");

        if (get(score) != null) {
            String oldName = get(score).getName();

            if (name.equals(oldName))
                return;

//             remove(score);
            if (NMS.getVersion().getMajor().equals("1.7")) {
//                sendScore(this.objective, oldName, score, true);
                sendScore(this.objective, name, score, false);
            } else {
                sendScore(this.objective, name, score, false);
/*              sendScore(this.buffer, oldName, score, true);
                sendScore(this.buffer, name, score, false);

                swapBuffers();

                sendScore(this.buffer, oldName, score, true);
                sendScore(this.buffer, name, score, false);*/
            }
        } else {
            sendScore(this.objective, name, score, false);
//            sendScore(this.buffer, name, score, false);
        }

        this.lines.add(new Entry(name, score));
    }

    public void set(List<Entry> entrys) {
        for (Entry entry : entrys) {
            set(entry.getName(), entry.getPosition());
        }
    }

    private void swapBuffers() {
        sendObjectiveDisplay(this.buffer);

        Objective temp = this.buffer;

        this.buffer = this.objective;
        this.objective = temp;
    }

    private void sendObjective(Objective obj, ObjectiveMode mode) {
        try {
            Object objHandle = NMS.getHandle(obj);

            Object packetObj = NMS.PACKET_OBJ.newInstance(
                    objHandle,
                    mode.ordinal()
            );

            NMS.sendPacket(packetObj, player);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {

        }
    }

    private void sendObjectiveDisplay(Objective obj) {
        try {
            Object objHandle = NMS.getHandle(obj);

            Object packet = NMS.PACKET_DISPLAY.newInstance(
                    1,
                    objHandle
            );

            NMS.sendPacket(packet, player);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {

        }
    }

    @SuppressWarnings("unchecked")
    private void sendScore(Objective obj, String name, int score, boolean remove) {
        try {
            Object sbHandle = NMS.getHandle(scoreboard);
            Object objHandle = NMS.getHandle(obj);

            Object sbScore = NMS.SB_SCORE.newInstance(
                    sbHandle,
                    objHandle,
                    name
            );

            NMS.SB_SCORE_SET.invoke(sbScore, score);

            Map scores = (Map) NMS.PLAYER_SCORES.get(sbHandle);

            if (remove) {
                List<Object> toRemove = new ArrayList<>();
                for (Object s : scores.keySet()) {
                    if (String.valueOf(s).equals(name)) {
                        toRemove.add(s);
                    }
                }
                for (Object removeObj : toRemove) {
                    scores.remove(removeObj);
                }
               /* if(scores.containsKey(name))
                    ((Map) scores.get(name)).remove(objHandle);*/
            } else {
                if (!scores.containsKey(name))
                    scores.put(name, new HashMap());

                ((Map) scores.get(name)).put(objHandle, sbScore);
            }

            switch (NMS.getVersion().getMajor()) {
                case "1.7": {
                    Object packet = NMS.PACKET_SCORE.newInstance(
                            sbScore,
                            remove ? 1 : 0
                    );

                    NMS.sendPacket(packet, player);
                    break;
                }

                default: {
                    Object packet;

                    /*if(remove) {
                        packet = NMS.PACKET_SCORE_REMOVE.newInstance(
                                name,
                                objHandle
                        );
                    }
                    else {*/
                    packet = NMS.PACKET_SCORE.newInstance(
                            sbScore
                    );
//                   }

                    NMS.sendPacket(packet, player);
                    break;
                }
            }
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {

        }
    }


    public void remove(Integer score) {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        String name = get(score).getName();

        if (name == null)
            return;

        this.scoreboard.resetScores(name);
        this.lines.remove(get(score));
    }

    public void delete() {
        if (this.deleted)
            return;

        Netherboard.instance().removeBoard(player);

        sendObjective(this.objective, ObjectiveMode.REMOVE);
        sendObjective(this.buffer, ObjectiveMode.REMOVE);

        this.objective.unregister();
        this.objective = null;

        this.buffer.unregister();
        this.buffer = null;

        this.lines = null;

        this.deleted = true;
    }

    public List<Entry> getLines() {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        return new ArrayList<>(lines);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        if (name.equals(this.name)) {
            return;
        }

        this.name = name;

        this.objective.setDisplayName(name);
        this.buffer.setDisplayName(name);

        sendObjective(this.objective, ObjectiveMode.UPDATE);
        sendObjective(this.buffer, ObjectiveMode.UPDATE);
    }

    public Player getPlayer() {
        return player;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }


    private enum ObjectiveMode {CREATE, REMOVE, UPDATE}

}
