package x.myinvest.popup;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import x.myinvest.HoldingStock;
import x.myinvest.MainActivity;
import x.myinvest.R;
import x.myinvest.SoldStocks;
import x.myinvest.Stock;

public class PopUpModifySoldedStock extends LinearLayout {
    EditText row;
    EditText code;
    EditText price;
    EditText soldPrice;
    EditText number;
    EditText buyDate;
    EditText soldDate;
    Button ok;
    MainActivity context;
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

    public PopUpModifySoldedStock(MainActivity context, ArrayList<Stock> soldStockList, SoldStocks soldStocks , int num){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_modify_solded_stock, this);
        row = (EditText) findViewById(R.id.layout_modifySoldedStock_editText_holdingRow);
        code = (EditText) findViewById(R.id.layout_modifySoldedStock_editText_stockCode);
        price = (EditText) findViewById(R.id.layout_modifySoldedStock_editText_stockPrice);
        soldPrice =(EditText) findViewById(R.id.layout_modifySoldedStock_editText_stocksoldPrice);
        number = (EditText) findViewById(R.id.layout_modifySoldedStock_editText_stockNumber);
        buyDate =(EditText) findViewById(R.id.layout_modifySoldedStock_editText_buyDate);
        soldDate =(EditText) findViewById(R.id.layout_modifySoldedStock_editText_soldDate);
        ok=((Button)findViewById(R.id.layout_modifySoldedStock_button_ok));

        if(num>-1) {
            row.setText(Integer.toString(num + 1));
            code.setText(soldStockList.get(num).code);
            price.setText(soldStockList.get(num).price);
            number.setText(soldStockList.get(num).number);
            buyDate.setText(soldStockList.get(num).buyDate);
            soldPrice.setText(soldStockList.get(num).nowPrice);
            soldDate.setText(soldStockList.get(num).soldDate);
        }

        row.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b) {
                    String a =row.getText().toString();

                    if(!a.isEmpty() && Integer.parseInt(a) <= soldStockList.size()) {
                        stockListNumber = Integer.parseInt(a)-1;
                        code.setText(soldStockList.get(stockListNumber).code);
                        price.setText(soldStockList.get(stockListNumber).price);
                        soldPrice.setText(soldStockList.get(stockListNumber).nowPrice);
                        number.setText(soldStockList.get(stockListNumber).number);
                        buyDate.setText(soldStockList.get(stockListNumber).buyDate);
                        soldDate.setText(soldStockList.get(stockListNumber).soldDate);
                    }
                    else {
                        Toast.makeText(context, "超出范围", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String a =row.getText().toString();
                if(!a.isEmpty() && Integer.parseInt(a) <= soldStockList.size()) {
                    soldStockList.get(stockListNumber).code = code.getText().toString();
                    soldStockList.get(stockListNumber).price = price.getText().toString();
                    soldStockList.get(stockListNumber).nowPrice = soldPrice.getText().toString();
                    soldStockList.get(stockListNumber).number = number.getText().toString();
                    soldStockList.get(stockListNumber).buyDate = buyDate.getText().toString();
                    soldStockList.get(stockListNumber).soldDate = soldDate.getText().toString();
                    ((MainActivity) context).saveHoldingData();
                    soldStocks.updateTableView();
                    context.saveSoldData();
                    Toast.makeText(context, "修改成功", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, "超出范围", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
