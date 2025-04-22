package com.example.steptycoon;

public class TycoonObject {
    String objectName;
    Integer cps;//Coins per second
    Integer count;
    Integer price;
    Boolean unlocked;

    public TycoonObject(String objectName, Integer cps, Integer price) {
        this.objectName = objectName;
        this.cps = cps;
        this.price = price;
        this.unlocked=false;
        count=0;
    }
    public TycoonObject(String objectName, Integer cps, Integer price,Boolean unlocked) {
        this.objectName = objectName;
        this.cps = cps;
        this.price = price;
        this.unlocked=unlocked;
        count=0;
    }
    public int getCurrentIncome(){
        return count*cps;
    }
    public void buyOne(){
        price= (int) (price*1.05);
        count++;
    }
    public void savedInstanceConvert(int cnt){
        while(count<cnt){
            buyOne();
        }
    }

}
