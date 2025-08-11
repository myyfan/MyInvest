package x.myinvest.popup;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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
import x.myinvest.Stock;

public class PopUpSplitHoldingStock extends LinearLayout {
    EditText row;
    EditText code;
    EditText orginNumber;
    EditText firstNumber;
    EditText secendNumber;
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

    public PopUpSplitHoldingStock(MainActivity context, ArrayList<Stock> holdingStocksList, HoldingStock holdingStock, int num){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_split_holding_stock, this);
        row = (EditText) findViewById(R.id.layout_splitHoldingStock_editText_holdingRow);
        code = (EditText) findViewById(R.id.layout_splitHoldingStock_editText_stockCode);
        orginNumber = (EditText) findViewById(R.id.layout_splitHoldingStock_editText_orginNumber);
        firstNumber = (EditText) findViewById(R.id.layout_splitHoldingStock_editText_firstNumber);
        secendNumber =(EditText) findViewById(R.id.layout_splitHoldingStock_editText_secendNumber);
        ok=((Button)findViewById(R.id.layout_splitHoldingStock_button_ok));

        if(num>-1) {
            row.setText(Integer.toString(num + 1));
            code.setText(holdingStocksList.get(num).code);
            orginNumber.setText(holdingStocksList.get(num).number);
            secendNumber.setText(holdingStocksList.get(num).number);
        }

        firstNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    Integer second = Integer.parseInt(holdingStocksList.get(num).number) - Integer.parseInt(editable.toString());
                    secendNumber.setText(second.toString());
                    //secendNumber.setText(Integer.parseInt(orgin) - Integer.parseInt(orginNumber.getText().toString()));
                }
                else {
                    secendNumber.setText(holdingStocksList.get(stockListNumber).number);
                }
            }
        });

        row.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b) {
                    String a =row.getText().toString();

                    if(!a.isEmpty() && Integer.parseInt(a) <= holdingStocksList.size()) {
                        stockListNumber = Integer.parseInt(a)-1;
                        orginNumber.setText(holdingStocksList.get(stockListNumber).number);
                        firstNumber.setText("");
                        secendNumber.setText(holdingStocksList.get(stockListNumber).number);
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
                String first = firstNumber.getText().toString();

                if(a.isEmpty() || Integer.parseInt(a) <= 0 || Integer.parseInt(a) > holdingStocksList.size()){
                    Toast.makeText(context, "超出范围", Toast.LENGTH_LONG).show();
                }
                else if( first.isEmpty() || first == "0" || first.equals( orginNumber.getText().toString()) || secendNumber.getText().toString().equals( orginNumber.getText().toString())){
                    Toast.makeText(context, "无需拆分", Toast.LENGTH_LONG).show();
                }
                else {

                    holdingStocksList.get(stockListNumber).number = firstNumber.getText().toString();

                    holdingStocksList.add(new Stock(holdingStocksList.get(stockListNumber).code,holdingStocksList.get(stockListNumber).price,secendNumber.getText().toString(),holdingStocksList.get(stockListNumber).buyDate));
                    ((MainActivity) context).saveHoldingData();
                    holdingStock.updateTabView(context);
                    Toast.makeText(context, "拆分成功", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
