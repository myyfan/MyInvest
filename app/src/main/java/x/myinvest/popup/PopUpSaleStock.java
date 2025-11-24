package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import x.myinvest.MainActivity;
import x.myinvest.R;
import x.myinvest.Stock;

public class PopUpSaleStock extends LinearLayout {
    EditText row;
    EditText price;
    Context context;
    Button ok;
    public PopUpSaleStock(MainActivity context, ArrayList<Stock> holdingStocksList, ArrayList<Stock> soldStockList,int num){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_salestock,this);
      //  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      //  layoutParams.setMargins(10, 30, 10, 30);

    //    row = new EditText(context);
    //    price = new EditText(context);
    //    row.setHint("输入要卖出的股票行号");
    //    price.setHint("输入卖出的价格");
    //    ok = new Button(context);
    //    ok.setText("确定");
        row=findViewById(R.id.layout_saleStock_editText_rowNum);
        price=findViewById(R.id.layout_saleStock_editText_price);
        ok=findViewById(R.id.layout_saleStock_button_ok);
        if(num>-1){
            row.setText(Integer.toString(num+1));
            price.setText(holdingStocksList.get(num).nowPrice);
        }
        row.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b) {
                    String a =row.getText().toString();

                    if(!a.isEmpty()) {
                        int stockListNumber = Integer.parseInt(a)-1;
                        if(stockListNumber<holdingStocksList.size()){price.setText(holdingStocksList.get(stockListNumber).nowPrice);}
                    }

                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a =row.getText().toString();
                String b =price.getText().toString();
                if (holdingStocksList.isEmpty()) {
                    Toast.makeText(context, "没有持股", Toast.LENGTH_LONG).show();
                }
                else {

                    if (a.isEmpty() || b.isEmpty()) {
                        Toast.makeText(context, "输入为空", Toast.LENGTH_LONG).show();
                    }
                    else{
                        int rowNum = Integer.parseInt(a);//攻取行号
                        if (rowNum <= 0 || rowNum > holdingStocksList.size()) {
                            Toast.makeText(context, "超出范围", Toast.LENGTH_LONG).show();
                        }
                        else {


                            Stock st = holdingStocksList.get(rowNum - 1);//取得卖出的股票数据
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");//卖出日期

                            soldStockList.add(st);
                            holdingStocksList.remove(rowNum - 1);
                            st.soldDate = dateFormat.format(new Date());
                            st.nowPrice = price.getText().toString();


                            //计算盈利数据
                            int numInt = Integer.parseInt(st.number);
                            //区分是股票还是基金
                            if (st.code.startsWith("0") || st.code.startsWith("3") || st.code.startsWith("6")) {
                                st.nowValue = Double.parseDouble(st.nowPrice) * numInt;
                                st.nowValue = st.nowValue * (1 - 0.001) - (st.nowValue > 33333.33 ? st.nowValue * 0.00015 : 5);
                                st.cost = Double.parseDouble(st.price) * numInt;
                                st.cost = st.cost + (st.cost > 33333.33 ? st.cost * 0.00015 : 5);
                                st.earn = st.nowValue - st.cost;
                                st.earnPercent = st.earn / st.cost * 100;
                            }
                            else {
                            //    st.nowValue = Double.parseDouble(st.nowPrice) * numInt * (1 - 0.0003);
                            //    st.cost = Double.parseDouble(st.price) * numInt * (1 + 0.0003);
                            //    st.earn = st.nowValue - st.cost;
                            //    st.earnPercent = st.earn / st.cost * 100;

                                double shouXuFeiMai3,shouXuFeiMai4;
                                shouXuFeiMai3=Double.parseDouble(st.price) * numInt * 0.0001;
                                shouXuFeiMai3=shouXuFeiMai3>0.1?shouXuFeiMai3:0.1;
                                shouXuFeiMai3=Double.parseDouble(String.format("%.2f",shouXuFeiMai3));
                                shouXuFeiMai4=Double.parseDouble(st.nowPrice) * numInt * 0.0001;
                                shouXuFeiMai4=shouXuFeiMai4>0.1?shouXuFeiMai4:0.1;
                                shouXuFeiMai4=Double.parseDouble(String.format("%.2f",shouXuFeiMai4));
                                st.nowValue = Double.parseDouble(st.nowPrice) * numInt -shouXuFeiMai4;
                                st.cost = Double.parseDouble(st.price) * numInt +shouXuFeiMai3;
                                st.earn = st.nowValue - st.cost;
                                st.earnPercent = st.earn / st.cost * 100;
                            }

                            context.gained += st.earn;
                            context.holdingStockView.updateTabView(context);
                            context.soldStocksView.updateTableView(context);
                            context.saveHoldingData();
                            context.saveSoldData();

                            Toast.makeText(context, "卖出成功", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
        });

    //    addView(row, layoutParams);
    //    addView(price, layoutParams);
    //    addView(ok, layoutParams);
    }
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
}
