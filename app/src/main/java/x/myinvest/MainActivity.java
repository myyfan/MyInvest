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
    private SharedPreferences perf;
    //private SharedPreferences.Editor editor;
    private TextView textView;
    private ArrayList<Stock> holdingStocksList =new ArrayList<>();
    private ArrayList<Stock> soldStockList = new ArrayList<>();
    private Timer timer;
    private HoldingStock holdingStock;//视图
    private LinearLayout mainView;//主视图
    private Stock shangZheng;//上证指数
    private String tenYears;//十年国债利率
    private double gain;//浮盈
    private double gained=-6731;//无法详细列出的历史盈利金额
    private Double allValue=0.0;//总市值
    private int reflashCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSavedData();
        textView=(TextView)findViewById(R.id.textView_gain);
        shangZheng=new Stock();

        holdingStock = new HoldingStock(this, holdingStocksList);
        holdingStock.updateTabView();

        mainView = (LinearLayout) findViewById(R.id.mainview);
        mainView.addView(holdingStock);

        //holdingStock = (HoldingStock) findViewById(R.id.view_holdingstock);
        //holdingStock.updateTabView();
        //pullNetworkData();

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
                int rowNum=Integer.parseInt(row.getText().toString());

                Stock stock = holdingStocksList.get(rowNum - 1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");
                stock.soldDate=dateFormat.format(new Date());
                stock.nowPrice=row.getText().toString();

                soldStockList.add(stock);
                holdingStocksList.remove(rowNum - 1);
                holdingStock.updateTabView();


            }
        });
    }

    void showPopUpDelStock() {
        View popUp = new PopupDelStock(this);
        showPopupWindows(popUp);
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
        ++reflashCount;
        textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"国债:"+tenYears+" 实现盈利："+String.format("%.0f",gained)+"\n浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue)+"  reflash:"+reflashCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullNetworkData();
        //textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"国债:"+tenYears+" 实现盈利："+String.format("%.0f",gained)+"\n浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue));
        updataTextView();
        holdingStock.refreshText();
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pullNetworkData();
                runOnUiThread(()->{
                    //textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"国债:"+tenYears+" 实现盈利："+String.format("%.0f",gained)+"\n浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue));
                    updataTextView();
                    holdingStock.refreshText();
                });
            }
        }, 5000, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    protected void loadSavedData(){

        String buyedStockCodeSaved =null;
        String buyedStockPriceSaved =null;
        perf=getSharedPreferences("buyedStock",0);
        //buyedStockCodeSaved =perf.getString("buyedStockCode","");
        //加载股票代码
        String[] stockArrayStr =perf.getString("buyedStockCode","").split(",");
        if(!stockArrayStr[0].isEmpty()){
            //加载股票价格
            String[] priceArrayStr=perf.getString("buyedStockPrice","").split(",");
            //加载股票数量
            String[] numberArrayStr=perf.getString("buyedStockNumber","").split(",");
            String[] buyDate=perf.getString("buyDate","").split(",");
            for(int i=0;i<stockArrayStr.length;i++){
                Stock savedStock = new Stock(stockArrayStr[i],priceArrayStr[i],numberArrayStr[i],buyDate[i]);
                holdingStocksList.add(savedStock);
            }
        }

        gained = Double.parseDouble(perf.getString("haveGained", "0"));


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
        saveData();
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
        saveData();
        holdingStock.updateTabView();
    }

    public void changeGained(double gained) {
        this.gained=gained;
        perf.edit().putString("haveGained", gained+"").apply();
    }

    //  protected void updatDataArray(){

    protected void pullNetworkData(){

        //new Thread(()->{
            gain =0;
            allValue=0.0;
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

                //updateTabView();
            }
            catch (Exception e){
                Log.w("network", e.toString(),e );

            }
        //}).start();
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


    protected void saveData(){
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

        editor=perf.edit();
        editor.putString("buyedStockCode",stockBuilder.toString());
        editor.putString("buyedStockPrice",priceBuilder.toString());
        editor.putString("buyedStockNumber",numBuilder.toString());
        editor.putString("buyDate",dateBuilder.toString());
        editor.apply();
    }





}
