package com.ytz.pojo;

public class MatchResult {

    /**
     * true :赢了，false：输了
     */
    private boolean status;
    private int leftNum;
    private int totalNum;
    private int diceNum;
    private int[] playerDices;
    private String money;

    public MatchResult(boolean status, int leftNum, int diceNum, int totalNum) {
        super();
        this.status = status;
        this.leftNum = leftNum;
        this.totalNum = totalNum;
        this.diceNum = diceNum;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getLeftNum() {
        return leftNum;
    }

    public void setLeftNum(int leftNum) {
        this.leftNum = leftNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getDiceNum() {
        return diceNum;
    }

    public void setDiceNum(int diceNum) {
        this.diceNum = diceNum;
    }

    public int[] getPlayerDices() {
        return playerDices;
    }

    public void setPlayerDices(int[] playerDices) {
        this.playerDices = playerDices;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }


}
