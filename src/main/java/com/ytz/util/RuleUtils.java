package com.ytz.util;

import com.ytz.pojo.MatchResult;
import com.ytz.pojo.Player;

import java.util.Random;



public class RuleUtils {

    // 随机5个骰子数 ,不会是顺子
    public static int[] yaoyiyao() {
    	int[] dices = new int[5];
        for (int i = 0; i < 5; i++) {
            int num = randomNum(1, 6);
            dices[i] = num;
        }
        //判斷是否是順子，如果是重摇
        for(int i = 0; i < 4; i++){
        	int diceCheck = dices[i];
        	for(int j = i+1; j < 5; j++){
        		int diceCheck2 = dices[j];
        		if(diceCheck == diceCheck2){
        			return dices;
        		}
        	}
        }

        return yaoyiyao();
    }
    
/*    public static void main(String[] args) {
    	for(int i=0;i<100;i++){
    	int[] result = yaoyiyao();
    	System.out.println(result[0]+""+result[1]+""+result[2]+""+result[3]+""+result[4]);
    	}
	}*/
    private static Random random = new Random();  
    // 生成从minNum到maxNum的随机数
    public static int randomNum(int minNum, int maxNum) {
        return random.nextInt(maxNum - minNum + 1) + minNum;
    }

    /**
     * zhai: true ：斋 false：不斋 fei ： 飞
     * 
     */
    public static MatchResult match(int[] myDices, int[] heDices, int num, int dice, boolean zhai, boolean fei) {
        // 斋：比个数
        if (zhai) {
            return zhai(myDices, heDices, num, dice);
        }

        // 正常模式

        return normal(myDices, heDices, num, dice);
    }

    public static MatchResult normal(int[] myDices, int[] heDices, int num, int dice) {
        int dices = 0;
        int dicesTotal1 = 0; boolean hasOne1 = false;
        int dicesTotal2 = 0; boolean hasOne2 = false;
        for (int i = 0; i < 5; i++) {
            int dice0 = myDices[i];
            int dice1 = heDices[i];
            if (dice0 == dice || dice0 == 1) {// 1可以充当任意骰子数
                dices++;
                dicesTotal1++;
                if(dice0 == 1){
                	hasOne1 =true;
                }
            }
            if (dice1 == dice || dice1 == 1) {// 1可以充当任意骰子数
                dices++;
                dicesTotal2++;
                if(dice1 == 1){
                	hasOne2 =true;
                }
            }
        }
        
        if(dicesTotal1 == 5){//豹子+1
        	dices ++;
        	if(!hasOne1){//纯豹子+1
        		dices ++;
        	}
        }
        
        if(dicesTotal2 == 5){
        	dices ++;
        	if(!hasOne2){
        		dices ++;
        	}
        }
        
        if (num > dices) {
        	return new MatchResult(true, dices - num,dice,dices);
        }
        return new MatchResult(false, num - dices,dice,dices);
        

    }

    public static MatchResult zhai(int[] myDices, int[] heDices, int num, int dice) {
        int dices = 0;
        int dicesTotal1 = 0; 
        int dicesTotal2 = 0; 
        for (int i = 0; i < 5; i++) {
            int dice0 = myDices[i];
            int dice1 = heDices[i];
            if (dice0 == dice) {
                dices++;
                dicesTotal1 ++;
            }
            if (dice1 == dice) {
                dices++;
                dicesTotal2++;
            }
        }
        
        if(dicesTotal1 == 5){//豹子+1
        	dices +=2;

        }
        
        if(dicesTotal2 == 5){
        	dices +=2;
        }
        if (num > dices) {
        	return new MatchResult(true, dices - num,dice,dices); 
        }
        return new MatchResult(false, num - dices,dice,dices);
        
    }
    
    
    public static int  getDealerDice(int[] heDices, int dice, boolean isZai) {
        int dices = 0;
        int dicesTotal1 = 0; boolean hasOne1 = false;
        for (int i = 0; i < 5; i++) {
            int dice1 = heDices[i];
            if (dice1 == dice || (dice1 == 1 && !isZai)) {// 1可以充当任意骰子数
                dices++;
                dicesTotal1++;
                if(dice1 == 1){
                	hasOne1 =true;
                }
            }
        }
        
        if(dicesTotal1 == 5){//豹子+1
        	dices ++;
        	if(!hasOne1){//纯豹子+1
        		dices ++;
        	}
        }
        

        return dices;

    }
    
    /**
     * 计算出机器人稳赢的骰子
     * 
     * @param num 需要几个
     * @param dice  骰子
     * @return
     */
   public static int[] makeRobotDice(int numTotal, int dice, Player dealer){
	   int userTotalNum = getDealerDice(dealer.getDiceArray(),dice,dealer.isZai());
	   numTotal = numTotal - userTotalNum;
	   boolean isZai = dealer.isZai();
	   int[] dices = new int[5];
	   int[] demo = {1,dice};
       for (int i = 0; i < 5; i++) {
    	   int num = 0;
    	   if(i < numTotal){
    		   if(isZai){
    			   dices[i] = dice;  
    		   }else{
    			   int rn = randomNum(0, 1); 
    			   dices[i]  = demo[rn];  
    		   }
    		   
    		   continue;
    	   }else{
    		    num = randomNum(1, 6);  
    	   }
           
           dices[i] = num;
       }
       return dices;
   }
   
   public  static int getMostDice(int[] diceArray,boolean isZai){
	   int one = 0;
	   int two = 0;
	   int three = 0;
	   int four = 0;
	   int five = 0;
	   int six = 0;
	   
	   int max = 0;
	   int mostDice = 0;//默認一個
	   for (int i = 0; i < diceArray.length; i++) {
		   int dice = diceArray[i];
    	  if(dice == 1){
    		  one ++;
    		  if(!isZai){
    			  two ++; 
    			  three ++;
    			  four ++;
    			  five ++;
    			  six ++ ;
    		  }
    	  }else if(dice  == 2){
    		  two ++;
    	  }else if(dice  == 3){
    		  three ++;
    	  }else if(dice  == 4){
    		  four ++;
    	  }else if(dice  == 5){
    		  five ++;
    	  }else if(dice  == 6){
    		  six ++ ;
    	  }
    	  
       }
	   if(two > three){
		   max = two; 
		   mostDice = 2;
	   }else{
		   max = three;
		   mostDice = 3;
	   }
	   if(four > max){
		   max = four;
		   mostDice =4;
	   }
	   if(five > max){
		   max = five;  
		   mostDice = 5;
	   }
	   if(six > max){
		   max = six;  
		   mostDice = 6;
	   }
	   
	   if(isZai && one > max){//没有斋，找出2-6哪个点数最多
			   max = one; 
			   mostDice = 1;
		   
	   }
	   
	   
	   
	   
	   return mostDice;
	   
   }
   
  


public static void main(String[] args) {
	int[] diceArray = {1,1,4,4,1};
	System.out.println(getMostDice(diceArray,true));
}
}
