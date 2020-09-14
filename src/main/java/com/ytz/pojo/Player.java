package com.ytz.pojo;


import com.ytz.util.RuleUtils;
import lombok.Data;

/**
 * @author Bob
 */
@Data
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
        this.num = 0;
        this.diceNum = 0;
        this.diceArray = null;

    }



}
