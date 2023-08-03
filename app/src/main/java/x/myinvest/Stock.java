package x.myinvest;

import java.nio.charset.CoderMalfunctionError;

public class Stock {
     String name = "";
     public String code;
     public String price="";
     public String number="";
     public double cost;
     public String nowPrice="";
     public double nowValue=0;
     String increase="";
     public double earnPercent=0;
     public double earn=0;//浮盈
     public String buyDate="";
     public String soldDate="";

    public Stock() { }
    public Stock(String code,String price,String number,String date) {
        this.code = code;
        this.price = price;
        this.number = number;
        this.buyDate=date;
    }

}
