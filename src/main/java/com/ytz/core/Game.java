package com.ytz.core;


import com.ytz.pojo.Player;
import lombok.Data;

import java.util.UUID;

/**
 * @author Bob
 */
@Data
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




}
