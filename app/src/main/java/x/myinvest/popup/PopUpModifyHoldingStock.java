package x.myinvest.popup;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import x.myinvest.HoldingStock;
import x.myinvest.MainActivity;
import x.myinvest.R;
import x.myinvest.Stock;

public class PopUpModifyHoldingStock extends LinearLayout {
    EditText row;
    EditText code;
    EditText price;
    EditText number;
    EditText buyDate;
    Button ok;
    Context context;
    int stockListNumber;

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == View.VISIBLE){

            ((MainActivity)context).timer.cancel();
        }
        else {
            ((MainActivity)context).timer = new Timer();
            ((MainActivity)context).timerTask=new TimerTask() {
                @Override
                public void run() {
                    ((MainActivity)context).pullNetworkData();
                }
            };

            ((MainActivity)context).timer.schedule(((MainActivity)context).timerTask , 0, 5000);
        }
    }

    public PopUpModifyHoldingStock(Context context, ArrayList<Stock> holdingStocksList, HoldingStock holdingStock){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_modify_holding_stock, this);
        row = (EditText) findViewById(R.id.layout_modifyHoldingStock_editText_holdingRow);
        code = (EditText) findViewById(R.id.layout_modifyHoldingStock_editText_stockCode);
        price = (EditText) findViewById(R.id.layout_modifyHoldingStock_editText_stockPrice);
        number = (EditText) findViewById(R.id.layout_modifyHoldingStock_editText_stockNumber);
        buyDate =(EditText) findViewById(R.id.layout_modifyHoldingStock_editText_buyDate);
        ok=((Button)findViewById(R.id.layout_modifyHoldingStock_button_ok));



        row.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b) {
                    String a =row.getText().toString();

                    if(!a.isEmpty()) {
                        stockListNumber = Integer.parseInt(a)-1;
                        code.setText(holdingStocksList.get(stockListNumber).code);
                        price.setText(holdingStocksList.get(stockListNumber).price);
                        number.setText(holdingStocksList.get(stockListNumber).number);
                        buyDate.setText(holdingStocksList.get(stockListNumber).buyDate);
                    }

                }
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                holdingStocksList.get(stockListNumber).code=code.getText().toString();
                holdingStocksList.get(stockListNumber).price=price.getText().toString();
                holdingStocksList.get(stockListNumber).number=number.getText().toString();
                holdingStocksList.get(stockListNumber).buyDate=buyDate.getText().toString();
                ((MainActivity)context).saveHoldingData();
                holdingStock.refreshText();
            }
        });
    }
}
