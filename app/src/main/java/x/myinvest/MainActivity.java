package x.myinvest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences perf;
    //private SharedPreferences.Editor editor;

    private ArrayList<Stock> stocksList=new ArrayList<>();
    private TableLayout tableLayout;
    private double gain;
    private double gained=-6731;
    private TextView textView;
    private Timer timer;
    private TextView[][] textViewHandler;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.textView_gain);

        Button addButton=(Button) findViewById(R.id.btn_addstock);
        Button delButton=(Button) findViewById(R.id.btn_delstock) ;
        addButton.setOnClickListener(this::addStock);
        delButton.setOnClickListener(this::delStock);
        tableLayout = (TableLayout)findViewById(R.id.table_layout_invest);
        loadSavedData();
        updateTabView();
        pullNetworkData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer=new Timer();
        refreshText();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pullNetworkData();
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
                stocksList.add(savedStock);
            }

            //updatDataArray();

        }


    }

    protected void addStock(View view){

        String newStockCode=((EditText)findViewById(R.id.editText_stock)).getText().toString();
        String newStockPrice=((EditText) findViewById(R.id.editText_price)).getText().toString();
        String newStockNumber=((EditText) findViewById(R.id.editText_number)).getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");
        String date = dateFormat.format(new Date());
        if(newStockCode.length()!=6||newStockPrice.isEmpty()||newStockNumber.isEmpty()){
            Toast.makeText(this, "输入数据错误", Toast.LENGTH_LONG).show();
        }
        else{
            stocksList.add(new Stock(newStockCode,newStockPrice ,newStockNumber ,date));
            Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
        }
        //stockList.add(newStockCode);
        //priceList.add(Float.parseFloat(newStockPrice));
        //numberList.add(Integer.parseInt(newStockNumber));
        //updatDataArray(stockList,priceList,numberList);

        saveData();
        updateTabView();


    }

    protected void delStock(View view ) {
        String delRow=(   (EditText)findViewById(R.id.text_del)   ).getText().toString();
        if(delRow.isEmpty()){
            Toast.makeText(MainActivity.this,"行号为空",Toast.LENGTH_LONG).show();
        }
        else{
            int dr=Integer.parseInt(delRow);
            if(dr>stocksList.size()){
                Toast.makeText(MainActivity.this,"超出范围",Toast.LENGTH_LONG).show();
            }
            else {
                stocksList.remove(dr-1);
                Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_LONG).show();
            }
        }
        saveData();
        updateTabView();

    }
  //  protected void updatDataArray(){
//
  //          stocksArr =stocksList.toArray(new Stock[stocksList.size()]);
  //          updateTabView();
//
  //  }
    protected void updateTabView(){

        textViewHandler = new TextView[ stocksList.size() ][6];
        textView.setText("浮盈："+String.format("%.2f", gain)+" 已实现盈利："+String.format("%.2f",gained)+" 总盈利："+String.format("%.2f",gain+gained));
        tableLayout.removeAllViews();
        tableLayout.setStretchAllColumns(true);
        //添加标题
        TableRow tableRow=new TableRow(this);

        TextView textView=new TextView(this); textView.setText("股票代码"); tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("现价");      tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("购价成本");  tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("数量");  tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("盈亏现值");       tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("购入日");   tableRow.addView(textView);

        tableLayout.addView(tableRow);
        //tableLayout.setDividerDrawable(getResources().getDrawable(R.drawable.bonus_list_item_divider));
        for(int i=0;i<stocksList.size();i++){
            Stock st = stocksList.get(i);
            addTabRow(st,i);
        }

    }

    public void refreshText() {
        textView.setText("浮盈："+String.format("%.2f", gain)+" 已实现盈利："+String.format("%.2f",gained)+" 总盈利："+String.format("%.2f",gain+gained));

        for (int i = 0; i < stocksList.size(); i++) {
            DecimalFormat df = new DecimalFormat("#.00");
            Stock stock = stocksList.get(i);
            //股票名称/代码
            textViewHandler[i][0].setText((i+1)+"."+stock.name+"\n"+stock.code);
            //股票现价/涨幅
            textViewHandler[i][1].setText(stock.nowPrice+"\n"+stock.increase+"%");
            //购买价格/金额
            textViewHandler[i][2].setText(stock.price+"\n"+String.format("%.2f",stock.cost));
            //股票数量
            textViewHandler[i][3].setText(stock.number);
            //盈利及百分比
            textViewHandler[i][4].setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent)+"%");
            //购买日期
            textViewHandler[i][5].setText(stock.buyDate);
        }
    }

    protected void addTabRow(Stock stock,int num){
    //protected void addTabRow(String stock,String nowPrice,String price,String number){
        DecimalFormat df = new DecimalFormat("#.00");
        TableRow tableRow=new TableRow(this);
        TextView textView=new TextView(this);
        tableRow=new TableRow(this);
        //股票名称/代码
        textView=new TextView(this);
        textView.setText((num+1)+"."+stock.name+"\n"+stock.code);
        textViewHandler[num][0]=textView;
        tableRow.addView(textView);
        //股票现价/涨幅
        textView=new TextView(this);
        textView.setText(stock.nowPrice+"\n"+stock.increase+"%");
        textViewHandler[num][1]=textView;
        tableRow.addView(textView);
        //购买价格/金额
        textView=new TextView(this);
        textView.setText(stock.price+"\n"+String.format("%.2f",stock.cost));
        textViewHandler[num][2]=textView;
        tableRow.addView(textView);
        //股票数量
        textView=new TextView(this);
        textView.setText(stock.number);
        textViewHandler[num][3]=textView;
        tableRow.addView(textView);
        //盈利及百分比
        textView=new TextView(this);
        textView.setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent)+"%");
        textViewHandler[num][4]=textView;
        tableRow.addView(textView);
        //购买日期
        textView=new TextView(this);
        textView.setText(stock.buyDate);
        textViewHandler[num][5]=textView;
        tableRow.addView(textView);

        tableLayout.addView(tableRow);
    }
    protected void saveData(){
        SharedPreferences.Editor editor;

        StringBuilder stockBuilder =new StringBuilder("");
        StringBuilder priceBuilder =new StringBuilder("");
        StringBuilder numBuilder =new StringBuilder("");
        StringBuilder dateBuilder =new StringBuilder("");
        for (int i=0;i<stocksList.size();i++) {
            Stock st=stocksList.get(i);
           // if(i==0){
                stockBuilder.append(stocksList.get(i).code + ",");
                priceBuilder.append(stocksList.get(i).price + ",");
                numBuilder.append(stocksList.get(i).number + ",");
                dateBuilder.append(stocksList.get(i).buyDate + ",");

        }

        editor=perf.edit();
        editor.putString("buyedStockCode",stockBuilder.toString());
        editor.putString("buyedStockPrice",priceBuilder.toString());
        editor.putString("buyedStockNumber",numBuilder.toString());
        editor.putString("buyDate",dateBuilder.toString());
        editor.apply();
    }

    protected void pullNetworkData(){

        new Thread(()->{
            gain =0;
            StringBuilder builder = new StringBuilder();
            String responce;
            String requestStockStr="";
            for(int i=0;i<stocksList.size();i++){
                Stock st=stocksList.get(i);
                if(st.code.startsWith("0")) requestStockStr +="s_sz"+st.code+",";
                else if(st.code.startsWith("6")) requestStockStr+="s_sh"+st.code+",";
                else if(st.code.startsWith("1")) requestStockStr+="sz"+st.code+",";
                else if(st.code.startsWith("5")) requestStockStr+="sh"+st.code+",";
            }
            try{
                //URL url=new URL("http://qt.gtimg.cn/");
                URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //connection.connect();
                InputStream in= connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"gbk"));
                String line;
                while ((line=reader.readLine())!=null){
                    //responce =scanner.nextLine();
                    builder.append(line);
                }
                responce=builder.toString();

                String[] div=responce.split(";");
                String[] stockData;
                for (int i = 0; i < stocksList.size(); i++) {
                        Stock st =stocksList.get(i);
                    if (i == div.length ) {
                        st.name = "无返回数据";
                        runOnUiThread(()->refreshText());
                        return;
                    }
                        stockData=div[i].split("~");
                    if(stockData[2].equals(st.code)){

                        st.name=stockData[1];
                        st.nowPrice=stockData[3];
                        st.increase = stockData[5];
                        int numInt = Integer.parseInt(st.number);

                        if(st.code.startsWith("0")||st.code.startsWith("3")||st.code.startsWith("6")) {
                            st.nowValue=Double.parseDouble(st.nowPrice)*numInt - (st.nowValue > 16666.67 ? st.nowValue * 0.0003 : 5);
                            st.cost=Double.parseDouble(st.price)*numInt - (st.cost > 16666.67 ? st.cost * 0.0003 : 5);
                            st.earn = st.nowValue - st.cost;
                            st.earnPercent=st.earn/st.cost*100;
                        }
                        else {
                            st.nowValue=Double.parseDouble(st.nowPrice)*numInt * (1 - 0.0003);
                            st.cost=Double.parseDouble(st.price)*numInt * (1 - 0.0003);
                            st.earn = st.nowValue - st.cost;
                            st.earnPercent=st.earn/st.cost*100;
                        }
                        gain +=st.earn; }
                    else {
                        st.name="返回数据不匹配";
                        runOnUiThread(()->refreshText());
                        return;
                    }

                }
                orderTheList();
                runOnUiThread(()->refreshText());
                //updateTabView();
            }
            catch (Exception e){
                Log.w("network", e.toString(),e );
            }
        }).start();



    }

    public void orderTheList() {
        int j=stocksList.size();
        Stock temp;
        for (int i = 0; i < j - 1; i++) {
            for (int k = 0; k < j - 1; k++) {
                if (stocksList.get(k).earnPercent < stocksList.get(k + 1).earnPercent) {
                    temp=stocksList.get(k);
                    stocksList.set(k,stocksList.get(k + 1));
                    stocksList.set(k+1,temp);
                }
            }
            j--;
        }
    }

}
