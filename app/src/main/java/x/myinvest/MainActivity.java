package x.myinvest;

import android.content.SharedPreferences;
import android.print.PrinterId;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  SharedPreferences buyedStock=null;
    private  String buyedStockCode=getApplicationContext().getSharedPreferences("buyedStockCode",0).getString("buyedStockCode","");
    private  String buyedStockPrice=getApplicationContext().getSharedPreferences("buyedStockCode",0).getString("buyedStockCode","");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
