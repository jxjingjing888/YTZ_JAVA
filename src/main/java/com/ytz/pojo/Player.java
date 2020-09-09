package com.ytz.pojo;


import com.ytz.util.RuleUtils;

public class Player {
	private int[] diceArray = {};// 点数

	private int num;
	private int diceNum;

	private boolean zai;

	private boolean fei;

	private String userId;

	private String tokenId;

	public void init() {
		// 初始化
		diceArray = RuleUtils.yaoyiyao();
	}

	public void destory() {
	    this.zai = false;
	    this.fei = false;
	    this.num = 0 ;
	    this.diceNum = 0;
	    this.diceArray = null;

	}



	public int[] getDiceArray() {
		return diceArray;
	}

	public void setDiceArray(int[] diceArray) {
		this.diceArray = diceArray;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getDiceNum() {
		return diceNum;
	}

	public void setDiceNum(int diceNum) {
		this.diceNum = diceNum;
	}

	public boolean isZai() {
		return zai;
	}

	public void setZai(boolean zai) {
		this.zai = zai;
	}

	public boolean isFei() {
		return fei;
	}

	public void setFei(boolean fei) {
		this.fei = fei;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

}
