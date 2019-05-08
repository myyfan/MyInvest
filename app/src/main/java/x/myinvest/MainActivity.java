package x.myinvest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import x.myinvest.popup.PopUpAddStock;
import x.myinvest.popup.PopupChangeGained;
import x.myinvest.popup.PopupDelStock;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences perfHoldingStocks;
    private SharedPreferences perfSoldStocks;
    //private SharedPreferences.Editor editor;
    private TextView textView;
    private ArrayList<Stock> holdingStocksList =new ArrayList<>();
    private ArrayList<Stock> soldStockList =new ArrayList<>();
    private Timer timer;
    private HoldingStock holdingStock;//视图
    private SoldStocks soldStocks;//视图
    private LinearLayout mainView;//主视图
    private Stock shangZheng;//上证指数
    private String tenYears;//十年国债利率
    private String shangZhengSYL;//上证平均市盈率
    private String shangZhengSY;//上证平均收益率
    private float haveMoney = 0;//预期最大投入金额
    private String cangWei;//仓位
    private double moneyNeedInvest=0;//根据仓位及最大投入金额计算的需投入资金
    private double moneyNeedAdd=0;//应投减已投入金额
    private double gain;//浮盈
    private double gained=-6731;//无法详细列出的历史盈利金额
    private Double allValue=0.0;//总市值

    private int reflashCount=0;
    private int getDataCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=(TextView)findViewById(R.id.textView_gain);
        shangZheng=new Stock();
        loadSavedData();

        holdingStock = new HoldingStock(this, holdingStocksList);
        //holdingStock.updateTabView();
        soldStocks = new SoldStocks(this,soldStockList);
        //soldStocks.updateTableView();

        TextView tvSpace = new TextView(this);
        tvSpace.setHeight(100);//作为分隔符

        mainView = (LinearLayout) findViewById(R.id.mainview);
        mainView.addView(holdingStock);
        mainView.addView(tvSpace);
        mainView.addView(soldStocks);


        //holdingStock = (HoldingStock) findViewById(R.id.view_holdingstock);
        //holdingStock.updateTabView();
       // pullNetworkData();
        //updataTextView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mainMenu_changGained:
                        showPopupChangeGaiend();
                        break;
                    case R.id.mainMenu_saleStock:
                        showPopupSaleStock();
                        break;
                    case R.id.mainMenu_addStock:
                        showPopUpAddStock();
                        //PopUpAddStock popAddStock = new PopUpAddStock(this);
                        break;
                    case R.id.mainMenu_delStock:
                        showPopUpDelStock();
                        break;
                    case R.id.mainMenu_delSoldStock:
                        showPopupDelSoldStock();
                        break;
                    case R.id.mainMenu_stockDividend:
                        showPopupDividend();
                        break;
                    case R.id.mainMenu_haveMoney:
                        showPopupHaveMoney();
                        break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showPopupChangeGaiend() {
        View popUp=new PopupChangeGained(this);
        showPopupWindows(popUp);

    }

    void showPopUpAddStock() {
        View popUp = new PopUpAddStock(this);
        //mainFrameLayout.addView(popUp);
        showPopupWindows(popUp);
    }

    void showPopupSaleStock() {
        //显示弹出窗口
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText row = new EditText(this);
        EditText price = new EditText(this);
        row.setHint("输入要卖出的股票行号");
        price.setHint("输入卖出的价格");

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(row,layoutParams);
        linearLayout.addView(price,layoutParams);
        linearLayout.addView(ok,layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowNum=Integer.parseInt(row.getText().toString());//攻取行号

                Stock st = holdingStocksList.get(rowNum - 1);//取得卖出的股票数据
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");//卖出日期

                soldStockList.add(st);
                holdingStocksList.remove(rowNum - 1);
                st.soldDate=dateFormat.format(new Date());
                st.nowPrice=price.getText().toString();


                //计算盈利数据
                int numInt = Integer.parseInt(st.number);
                if(st.code.startsWith("0")||st.code.startsWith("3")||st.code.startsWith("6")) {
                    st.nowValue=Double.parseDouble(st.nowPrice)*numInt;
                    st.nowValue=st.nowValue*(1-0.001) - (st.nowValue > 16666.67 ? st.nowValue * 0.0003 : 5);
                    st.cost=Double.parseDouble(st.price)*numInt;
                    st.cost=st.cost + (st.cost > 16666.67 ? st.cost * 0.0003 : 5);
                    st.earn = st.nowValue - st.cost;
                    st.earnPercent=st.earn/st.cost*100;
                }
                else {
                    st.nowValue=Double.parseDouble(st.nowPrice)*numInt * (1 - 0.0003);
                    st.cost=Double.parseDouble(st.price)*numInt * (1 + 0.0003);
                    st.earn = st.nowValue - st.cost;
                    st.earnPercent=st.earn/st.cost*100;
                }

                gained+=st.earn;
                holdingStock.updateTabView();
                soldStocks.updateTableView();
                saveHoldingData();
                saveSoldData();

            }
        });
    }

    void showPopupDividend() {
        //显示弹出窗口
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText name = new EditText(this);
        EditText money = new EditText(this);
        name.setHint("输入分红的股票");
        money.setHint("输入分红金额");

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(name,layoutParams);
        linearLayout.addView(money,layoutParams);
        linearLayout.addView(ok,layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Stock st = new Stock();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");//卖出日期

                st.name=name.getText().toString();
                st.code = "分红";
                st.soldDate=dateFormat.format(new Date());
                st.earn=Double.parseDouble(money.getText().toString());
                soldStockList.add(st);


                //计算盈利数据

                gained+=st.earn;
                holdingStock.updateTabView();
                soldStocks.updateTableView();
                saveHoldingData();
                saveSoldData();

            }
        });
    }

    void showPopUpDelStock() {
        View popUp = new PopupDelStock(this);
        showPopupWindows(popUp);
    }

    void showPopupDelSoldStock() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText row = new EditText(this);
        row.setHint("输入要删除的股票行号");

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(row,layoutParams);
        linearLayout.addView(ok,layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowNum=Integer.parseInt(row.getText().toString());//攻取行号
                gained-=soldStockList.get(rowNum-1).earn;
                soldStockList.remove(rowNum - 1);

                soldStocks.updateTableView();
                saveSoldData();

            }
        });
    }

    void showPopupHaveMoney() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText textHaveMoney = new EditText(this);
        textHaveMoney.setHint(Float.toString(haveMoney));

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(textHaveMoney,layoutParams);
        linearLayout.addView(ok,layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveMoney=Float.parseFloat(textHaveMoney.getText().toString());//攻取行号
                perfHoldingStocks.edit().putFloat("haveMoney", haveMoney).apply();

            }
        });
    }

    void showPopupWindows(View popUp) {
        PopupWindow popupWindow=new PopupWindow(popUp, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    protected void updataTextView() {
        ++reflashCount;  //  +" ref:"+reflashCount+"|"+getDataCount
        textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"市盈率:"+shangZhengSYL+" 收益率:"+shangZhengSY+"%\n"+"国债:"+tenYears+"  仓位:"+cangWei+"%"+"  应投："+String.format("%.0f",moneyNeedInvest)+"  追加"+String.format("%.0f",moneyNeedAdd)+"\n实现盈利："+String.format("%.0f",gained)+" 浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue));
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullNetworkData();
        pullShangZhengSYL();
        //textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"国债:"+tenYears+" 实现盈利："+String.format("%.0f",gained)+"\n浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue));
     //   updataTextView();
     //   holdingStock.refreshText();
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pullNetworkData();
             //   runOnUiThread(()->{
             //       //textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"国债:"+tenYears+" 实现盈利："+String.format("%.0f",gained)+"\n浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue));
             //       updataTextView();
             //       holdingStock.refreshText();
             //   });
            }
        }, 5000, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    protected void loadSavedData(){
        //加载持有中的股票数据
        String buyedStockCodeSaved =null;
        String buyedStockPriceSaved =null;
        perfHoldingStocks =getSharedPreferences("buyedStock",0);
        //加载预期投入的最大资金量
        haveMoney = perfHoldingStocks.getFloat("haveMoney", 0);
        //加载股票代码
        String[] stockArrayStr = perfHoldingStocks.getString("buyedStockCode","").split(",");
        if(!stockArrayStr[0].isEmpty()){
            //perfHoldingStocks = new ArrayList<Stock>(stockArrayStr.length);

            //加载股票价格
            String[] priceArrayStr= perfHoldingStocks.getString("buyedStockPrice","").split(",");
            //加载股票数量
            String[] numberArrayStr= perfHoldingStocks.getString("buyedStockNumber","").split(",");
            String[] buyDate= perfHoldingStocks.getString("buyDate","").split(",");
            for(int i=0;i<stockArrayStr.length;i++){
                Stock savedStock = new Stock(stockArrayStr[i],priceArrayStr[i],numberArrayStr[i],buyDate[i]);
                holdingStocksList.add(savedStock);
            }
        }

        //gained = Double.parseDouble(perfHoldingStocks.getString("haveGained", "0"));

        //加载已出售的股票
        String[] soldStocksStrArr = perfHoldingStocks.getString("soldStock", "").split(",");
        int lenSoldArr = soldStocksStrArr.length;
        //soldStockList = new ArrayList<Stock>(lenSoldArr);
        if (soldStocksStrArr[0] == "") {
            return;
        }

        for (int i=0;i<lenSoldArr;i+=9) {
            Stock stock =new Stock();
            stock.name = soldStocksStrArr[i];
            stock.code = soldStocksStrArr[i + 1];
            stock.price = soldStocksStrArr[i + 2];
            stock.nowPrice = soldStocksStrArr[i + 3];
            stock.number = soldStocksStrArr[i + 4];
            stock.earn = Double.parseDouble(soldStocksStrArr[i + 5]);
            stock.earnPercent = Double.parseDouble(soldStocksStrArr[i + 6]);
            stock.buyDate = soldStocksStrArr[i + 7];
            stock.soldDate = soldStocksStrArr[i + 8];
            soldStockList.add(stock);
            gained += stock.earn;
        }
        //soldStocks.updateTableView();
    }

    public void addStock(String newStockCode,String newStockPrice,String newStockNumber) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");
        String date = dateFormat.format(new Date());
        if(newStockCode.length()!=6||newStockPrice.isEmpty()||newStockNumber.isEmpty()){
            Toast.makeText(this, "输入数据错误", Toast.LENGTH_LONG).show();
        }
        else{
            holdingStocksList.add(new Stock(newStockCode,newStockPrice ,newStockNumber ,date));
            Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
        }
        saveHoldingData();
        holdingStock.updateTabView();
    }


    public void delStock(String delRow) {
        if(delRow.isEmpty()){
            Toast.makeText(MainActivity.this,"行号为空",Toast.LENGTH_LONG).show();
        }
        else{
            int dr=Integer.parseInt(delRow);
            if(dr> holdingStocksList.size()){
                Toast.makeText(MainActivity.this,"超出范围",Toast.LENGTH_LONG).show();
            }
            else {
                holdingStocksList.remove(dr-1);
                Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_LONG).show();
            }
        }
        saveHoldingData();
        holdingStock.updateTabView();
    }

    public void changeGained(double gained) {
        this.gained=gained;
        perfHoldingStocks.edit().putString("haveGained", gained+"").apply();
    }

    //  protected void updatDataArray(){

    protected void pullNetworkData(){

        //new Thread(()->{

            StringBuilder builder = new StringBuilder();
            //  String requestStockStr="";
            builder.append("http://qt.gtimg.cn/q=");
            for(int i = 0; i< holdingStocksList.size(); i++){
                Stock st= holdingStocksList.get(i);
                if(st.code.startsWith("0")) builder.append("s_sz"+st.code+",");
                else if(st.code.startsWith("6")) builder.append("s_sh"+st.code+",");
                else if(st.code.startsWith("1")) builder.append("sz"+st.code+",");
                else if(st.code.startsWith("5")) builder.append("sh"+st.code+",");
                //  if(st.code.startsWith("0")) requestStockStr +="s_sz"+st.code+",";
                //  else if(st.code.startsWith("6")) requestStockStr+="s_sh"+st.code+",";
                //  else if(st.code.startsWith("1")) requestStockStr+="sz"+st.code+",";
                //  else if(st.code.startsWith("5")) requestStockStr+="sh"+st.code+",";
            }
            //尾部增加查询上证指数
            builder.append("sh000001");
            try{
                URL url=new URL(builder.toString());
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                HttpURLConnection connection= (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in= connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"gbk"));
                String line;
                while ((line=reader.readLine())!=null){
                    //responce =scanner.nextLine();
                    builder.append(line);
                }
                String responce;
                responce=builder.toString();

                String[] div=responce.split(";");
                String[] stockData;
                //循环填充数据
                double gain =0;
                double allValue=0.0;
                for (int i = 0; i < holdingStocksList.size(); i++) {
                    Stock st = holdingStocksList.get(i);
                    if (i == div.length ) {
                        st.name = "无返回数据";
                        runOnUiThread(()->holdingStock.refreshText());
                        return;
                    }
                    stockData=div[i].split("~");
                    if(stockData[2].equals(st.code)){

                        st.name=stockData[1];
                        st.nowPrice=stockData[3];

                        if (st.code.startsWith("0") || st.code.startsWith("3") || st.code.startsWith("6")) {
                            st.increase = stockData[5];
                        } else {
                            st.increase = stockData[32];
                        }
                        //计算盈利数据
                        int numInt = Integer.parseInt(st.number);
                        if(st.code.startsWith("0")||st.code.startsWith("3")||st.code.startsWith("6")) {
                            st.nowValue=Double.parseDouble(st.nowPrice)*numInt;
                            st.nowValue=st.nowValue*(1-0.001) - (st.nowValue > 16666.67 ? st.nowValue * 0.0003 : 5);
                            st.cost=Double.parseDouble(st.price)*numInt;
                            st.cost=st.cost + (st.cost > 16666.67 ? st.cost * 0.0003 : 5);
                            st.earn = st.nowValue - st.cost;
                            st.earnPercent=st.earn/st.cost*100;
                        }
                        else {
                            st.nowValue=Double.parseDouble(st.nowPrice)*numInt * (1 - 0.0003);
                            st.cost=Double.parseDouble(st.price)*numInt * (1 + 0.0003);
                            st.earn = st.nowValue - st.cost;
                            st.earnPercent=st.earn/st.cost*100;
                        }
                        gain +=st.earn;
                        allValue+=st.nowValue;
                    }
                    else {
                        st.name="不匹配";
                        runOnUiThread(()->holdingStock.refreshText());
                        return;
                    }

                }
                this.gain=gain;
                this.allValue=allValue;
                //填入上证指数数据
                stockData=div[holdingStocksList.size()].split("~");
                shangZheng.nowPrice = stockData[3];
                shangZheng.increase = stockData[32];
                //connection.disconnect();

                //获取十年国债利率

                //in.close();
                url=new URL("https://forexdata.wallstreetcn.com/real?en_prod_code=CHINA10YEAR&fields=prod_name,last_px,px_change,px_change_rate,high_px,low_px,open_px,preclose_px,business_amount,business_balance,market_value,turnover_ratio,dyn_pb_rate,amplitude,pe_rate,bps,hq_type_code,trade_status,bid_grp,offer_grp,business_amount_in,business_amount_out,circulation_value,securities_type,update_time,price_precision,week_52_high,week_52_low");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Referer","https://wallstreetcn.com/markets/bonds/CHINA10YEAR");
                conn.setRequestProperty("Host","forexdata.wallstreetcn.com");
                conn.setRequestProperty("Connection","keep-alive");
                conn.connect();
                builder.setLength(0);
                InputStream  inx= conn.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(inx,"utf-8"));
                while ((line=read.readLine())!=null){
                    //responce =scanner.nextLine();
                    builder.append(line);
                }
                responce=builder.toString();
                div=responce.split(",");
                tenYears=div[2];
                orderTheList();

                //计算理论仓位
                double tenYears=Double.parseDouble(this.tenYears);
                double shangZhengShouYiLv = 1/(Double.parseDouble(shangZhengSYL)*(1+Double.parseDouble(shangZheng.increase)/100));
                shangZhengSY=String.format("%.2f",shangZhengShouYiLv*100);
                double zuiDiShouYiLv=0.015*tenYears;
                double zuiDaShouyiLv=0.03*tenYears;
                double cangWei=(shangZhengShouYiLv - zuiDiShouYiLv) / (zuiDaShouyiLv - zuiDiShouYiLv);
                moneyNeedInvest=(haveMoney+gained)*cangWei;
                moneyNeedAdd=moneyNeedInvest-(allValue-gain);
                this.cangWei = String.format("%.2f",cangWei*100);

                //responce=cangWei;

                //updateTabView();
            }
            catch (Exception e){
                Log.w("network", e.toString(),e );
                runOnUiThread(()->{
                    updataTextView();
                    holdingStock.refreshText();
                });
            }
            getDataCount++;
        runOnUiThread(()->{
                   updataTextView();
                   holdingStock.refreshText();
               });
    }

    protected void pullShangZhengSYL() {
        new Thread(() -> {
            StringBuilder builder = new StringBuilder();
            try {
                URL url = new URL("https://www.legulegu.com/stockdata/shanghaiPE");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    //responce =scanner.nextLine();
                    builder.append(line);
                }
                String responce = builder.toString();

                String[] div = responce.split("平均市盈率：");
                int a=div[1].charAt(4);
                if ( a>47 && a<58 )
                    shangZhengSYL = div[1].substring(0,5) ;
                else
                    shangZhengSYL = div[1].substring(0,4) ;

            } catch (Exception e) {
                Log.w("network", e.toString(), e);
                //   runOnUiThread(()->{
                //       updataTextView();
                //       holdingStock.refreshText();
                //   });
            }
        }
        ).start();
    }

    public void orderTheList() {
        Stock temp;
        int j= holdingStocksList.size()-1;
        int haveOrderRow=j;
        for (int i = 0; i < holdingStocksList.size(); i++) {
            for (int k = 0; k <j; k++) {
                if (holdingStocksList.get(k).earnPercent < holdingStocksList.get(k + 1).earnPercent) {
                    temp = holdingStocksList.get(k);
                    holdingStocksList.set(k, holdingStocksList.get(k + 1));
                    holdingStocksList.set(k + 1, temp);
                    haveOrderRow=j;
                }
            }
            j=haveOrderRow;
        }
    }


    protected void saveHoldingData(){
        SharedPreferences.Editor editor;

        StringBuilder stockBuilder =new StringBuilder("");
        StringBuilder priceBuilder =new StringBuilder("");
        StringBuilder numBuilder =new StringBuilder("");
        StringBuilder dateBuilder =new StringBuilder("");
        for (int i = 0; i< holdingStocksList.size(); i++) {
            Stock st= holdingStocksList.get(i);
           // if(i==0){
                stockBuilder.append(holdingStocksList.get(i).code + ",");
                priceBuilder.append(holdingStocksList.get(i).price + ",");
                numBuilder.append(holdingStocksList.get(i).number + ",");
                dateBuilder.append(holdingStocksList.get(i).buyDate + ",");

        }

        editor= perfHoldingStocks.edit();
        editor.putString("buyedStockCode",stockBuilder.toString());
        editor.putString("buyedStockPrice",priceBuilder.toString());
        editor.putString("buyedStockNumber",numBuilder.toString());
        editor.putString("buyDate",dateBuilder.toString());
        editor.apply();
    }

    protected void saveSoldData() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Stock stock1 : soldStockList) {
            stringBuilder.append(stock1.name+",")
                         .append(stock1.code+",")
                         .append(stock1.price+",")
                         .append(stock1.nowPrice+",")
                         .append(stock1.number+",")
                         .append(stock1.earn+",")
                         .append(stock1.earnPercent+",")
                         .append(stock1.buyDate+",")
                         .append(stock1.soldDate+",");
        }
        perfHoldingStocks.edit().putString("soldStock", stringBuilder.toString()).apply();

    }





}
