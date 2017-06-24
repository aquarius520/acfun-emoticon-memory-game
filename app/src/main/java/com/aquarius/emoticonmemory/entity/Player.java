package com.aquarius.emoticonmemory.entity;

/**
 * Created by aquarius on 2017/6/23.
 */
public class Player {

    private String name;
    private int score;
    private String elapsedTime; // 完成游戏消耗的时间

    public Player(String name, int score, String timestamp) {
        this.name = name;
        this.score = score;
        this.elapsedTime = timestamp;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", elapsedTime='" + elapsedTime + '\'' +
                '}';
    }
}
