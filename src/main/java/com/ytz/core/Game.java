package com.ytz.core;


import com.ytz.pojo.Player;

import java.util.UUID;

/**
 * @author Bob
 */
public class Game {
    private Player dealer = new Player();
    private Player player = new Player();
    private String gameId = null;

    public void init() {
        player.init();
        dealer.init();
        gameId = UUID.randomUUID().toString();
    }

    public void gameOver() {
        player.destory();
        dealer.destory();
        gameId = null;
    }

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Player dealer) {
        this.dealer = dealer;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


}
