package x.myinvest;

import java.nio.charset.CoderMalfunctionError;

public class Stock {
     String name = "";
     String code;
     String price;
     String number;
     double cost;
     String nowPrice="";
     double nowValue=0;
     String increase="";
     double earnPercent=0;
     double earn=0;
     String buyDate="-";

    public Stock() { }
    public Stock(String code,String price,String number,String date) {
        this.code = code;
        this.price = price;
        this.number = number;
        this.buyDate=date;
    }

}
