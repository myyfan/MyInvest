package x.myinvest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private  SharedPreferences buyedStock=null;
    private  String buyedStockCodeString=null;
    private  String buyedStockPriceString=null;
    private  String[] buyedStockCodeArray=null;
    private  String[] buyedStockPriceArray=null;
    private ArrayList<String> stockList = new ArrayList<>();
    private ArrayList<String> priceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buyedStockCodeString=getSharedPreferences("buyedStockCode",0).getString("buyedStockCode","");
        buyedStockCodeArray=getSharedPreferences("buyedStockCode",0).getString("buyedStockCode","").split(",");
        buyedStockPriceString=getSharedPreferences("buyedStockPrice",0).getString("buyedStockPrice","");
        buyedStockPriceArray=getSharedPreferences("buyedStockPrice",0).getString("buyedStockPrice","").split(",");
        for(String a:buyedStockCodeArray){stockList.add(a);}
        for(String a:buyedStockPriceArray){priceList.add(a);}

        Button addButton=(Button) findViewById(R.id.add_stock);
        addButton.setOnClickListener(this::addStock);
    }


    protected void addStock(View view){
        EditText stockCodeEditText=(EditText) findViewById(R.id.editText_stock);
        EditText StockPriceEditText=(EditText) findViewById(R.id.editText_price);
        String newStockCode=((EditText)findViewById(R.id.editText_stock)).getText().toString();
        String newStockPrice=((EditText) findViewById(R.id.editText_price)).getText().toString();
        stockList.add(newStockCode);
        priceList.add(newStockPrice);

    }
}
