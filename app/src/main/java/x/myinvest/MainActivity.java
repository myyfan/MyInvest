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
    Timer timer;





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
        //updateTabView();


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
            }
        }


    }
  //  protected void updatDataArray(){
//
  //          stocksArr =stocksList.toArray(new Stock[stocksList.size()]);
  //          updateTabView();
//
  //  }
    protected void updateTabView(){
        textView.setText("浮盈："+String.format("%.2f", gain)+"    已实现盈利："+String.format("%.2f",gained)+"    总盈利："+String.format("%.2f",gain+gained));
        tableLayout.removeAllViews();
        tableLayout.setStretchAllColumns(true);
        //添加标题
        TableRow tableRow=new TableRow(this);

        TextView textView=new TextView(this); textView.setText("股票代码"); tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("现价");      tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("购买价格");  tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("购买数量");  tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("盈亏");       tableRow.addView(textView);
        textView=new TextView(this);          textView.setText("购买时间");   tableRow.addView(textView);

        tableLayout.addView(tableRow);
        //tableLayout.setDividerDrawable(getResources().getDrawable(R.drawable.bonus_list_item_divider));
        for(int i=0;i<stocksList.size();i++){
            Stock st = stocksList.get(i);
            addTabRow(st,i+1);
        }

    }
    protected void addTabRow(Stock stock,int num){
    //protected void addTabRow(String stock,String nowPrice,String price,String number){
        DecimalFormat df = new DecimalFormat("#.00");
        TableRow tableRow=new TableRow(this);
        TextView textView=new TextView(this);
        tableRow=new TableRow(this);

        textView=new TextView(this);
        textView.setText(num+"."+stock.name+"\n"+stock.code);
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(stock.nowPrice);
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(stock.price);
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(stock.number);
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(""+String.format("%.2f",stock.earn));
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(""+stock.buyDate);
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
                        stockData=div[i].split("~");
                    if(stockData[2].equals(st.code)){
                        st.name=stockData[1];
                        st.nowPrice=stockData[3];
                        int numInt = Integer.parseInt(st.number);
                        double nowValue=Double.parseDouble(st.nowPrice)*numInt;
                        double cost=Double.parseDouble(st.price)*numInt;
                        if(st.code.startsWith("0")||st.code.startsWith("3")||st.code.startsWith("6")) {
                            st.earn = nowValue - (nowValue > 16666.67 ? nowValue * 0.0003 : 5) - cost - (cost > 16666.67 ? cost * 0.0003 : 5);
                        }
                        else {
                            st.earn = nowValue - nowValue * 0.0003 - cost -  cost * 0.0003;
                        }
                        gain +=st.earn; }
                    else {
                        st.name="返回数据不匹配";
                        runOnUiThread(()->updateTabView());
                        return;
                    }

                }
                runOnUiThread(()->updateTabView());
                //updateTabView();
            }
            catch (Exception e){
                Log.w("network", e.toString(),e ); }
        }).start();



    }

}
