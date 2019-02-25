package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import x.myinvest.MainActivity;
import x.myinvest.R;

public class PopUpAddStock extends LinearLayout {
    EditText code;
    EditText price;
    EditText number;
    Context context;
    public  PopUpAddStock(Context context){
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
        btOk.setOnClickListener(this::addStock);
    }
    private void addStock(View view) {
        ((MainActivity)context).addStock(code.getText().toString(), price.getText().toString(), number.getText().toString());
    }
}
