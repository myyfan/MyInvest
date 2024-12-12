package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import x.myinvest.MainActivity;
import x.myinvest.R;
import x.myinvest.Stock;

public class PopupRebuyFromSoldedStocks extends LinearLayout{
    EditText code;
    EditText price;
    EditText number;
    Context context;

    public  PopupRebuyFromSoldedStocks(MainActivity context, ArrayList<Stock> soldStockList , int num){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_rebuy_from_soldedstocks,this);
        View pop=findViewById(R.id.popup_rebuy_from_soldedstocks);
        //  pop.setOnClickListener(new OnClickListener() {
        //      @Override
        //      public void onClick(View v) {
        //          //空函数,只为阶段点击消息向上传递
        //      }
        //  });
        code = (EditText) findViewById(R.id.popup_Soldedstocks_rebuy_code );
        price = (EditText) findViewById(R.id.popup_Soldedstocks_rebuy_price);
        number = (EditText) findViewById(R.id.popup_Soldedstocks_rebuy_number);
        Button btOk = (Button) findViewById(R.id.popup_Soldedstocks_rebuy_ok);
        if(num!=-1){//不等于-1为复购
            code.setText(soldStockList.get(num).code);

        }


        btOk.setOnClickListener(this::addStock);
    }
    private void addStock(View view) {
        ((MainActivity)context).addStock(code.getText().toString(), price.getText().toString(), number.getText().toString());
    }
}


