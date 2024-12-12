package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import x.myinvest.MainActivity;
import x.myinvest.R;
import x.myinvest.Stock;

public class PopUpAddStock extends LinearLayout {
    EditText code;
    EditText price;
    EditText number;
    Context context;
    public  PopUpAddStock(MainActivity context, ArrayList<Stock> holdingStocksList , int num){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_addstock,this);
        View pop=findViewById(R.id.layout_addstock_LinearLayout);
      //  pop.setOnClickListener(new OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
      //          //空函数,只为阶段点击消息向上传递
      //      }
      //  });
        code = (EditText) findViewById(R.id.layout_addstock_editText_stockCode);
        price = (EditText) findViewById(R.id.layout_addstock_editText_stockPrice);
        number = (EditText) findViewById(R.id.layout_addstock_editText_stockNumber);
        Button btOk = (Button) findViewById(R.id.layout_addstock_button_ok);
        if(num!=-1){//不等于-1为复购
            code.setText(holdingStocksList.get(num).code);
            price.setText(holdingStocksList.get(num).nowPrice);

        }


        btOk.setOnClickListener(this::addStock);
    }
    private void addStock(View view) {
        ((MainActivity)context).addStock(code.getText().toString(), price.getText().toString(), number.getText().toString());
    }
}
