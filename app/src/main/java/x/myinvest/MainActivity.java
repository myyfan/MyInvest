package x.myinvest;

import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences perf;
    //private SharedPreferences.Editor editor;

    private ArrayList<Stock> stocksList=new ArrayList<>();
    private TableLayout tableLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addButton=(Button) findViewById(R.id.btn_addstock);
        Button delButton=(Button) findViewById(R.id.btn_delstock) ;
        addButton.setOnClickListener(this::addStock);
        delButton.setOnClickListener(this::delStock);
        tableLayout = (TableLayout)findViewById(R.id.table_layout_invest);
        loadSavedData();
        pullNetworkData();
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pullNetworkData();
            }
        }, 5000, 1000);
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
            String[] buyDate=perf.getString("buyedDate","").split(",");
            for(int i=0;i<stockArrayStr.length;i++){
                Stock savedStock = new Stock(stockArrayStr[i],priceArrayStr[i],numberArrayStr[i],"");
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
        if(newStockCode.length()==6){
            stocksList.add(new Stock(newStockCode,newStockPrice ,newStockNumber ,date));
        }
        //stockList.add(newStockCode);
        //priceList.add(Float.parseFloat(newStockPrice));
        //numberList.add(Integer.parseInt(newStockNumber));
        //updatDataArray(stockList,priceList,numberList);

        saveData();
        //updateTabView();


    }

    protected void delStock(View view ) {
        String delRow = ((EditText)findViewById(R.id.text_del)).getText().toString();
        stocksList.remove(Integer.parseInt(delRow));
    }
  //  protected void updatDataArray(){
//
  //          stocksArr =stocksList.toArray(new Stock[stocksList.size()]);
  //          updateTabView();
//
  //  }
    protected void updateTabView(){
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
            addTabRow(st);
        }

    }
    protected void addTabRow(Stock stock){
    //protected void addTabRow(String stock,String nowPrice,String price,String number){
        TableRow tableRow=new TableRow(this);
        TextView textView=new TextView(this);
        tableRow=new TableRow(this);

        textView=new TextView(this);
        textView.setText(stock.name+"\n"+stock.code);
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
        textView.setText(""+stock.earn);
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(""+stock.buyDate);
        tableRow.addView(textView);

        tableLayout.addView(tableRow);
    }
    protected void saveData(){
        SharedPreferences.Editor editor;
        String stock="";
        String price="";
        String num="";
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
          //  }
          //  else {
          //      stock=stock+","+st.code;
          //      price=price+","+st.price;
          //      num=num+","+st.number;
          //  }
        }

       // for (int i=0;i<stocksList.size();i++) {
       //      Stock st=stocksList.get(i);
       //      if(i==0){
       //          stock+=st.code;
       //          price+=st.price;
       //          num+=st.number;
       //      }
       //      else {
       //          stock=stock+","+st.code;
       //          price=price+","+st.price;
       //          num=num+","+st.number;
       //      }
       // }
        stock =stockBuilder.toString();
        editor=perf.edit();
        editor.putString("buyedStockCode",stock);
        editor.putString("buyedStockPrice",price);
        editor.putString("buyedStockNumber",num);
        editor.putString("buyDate",dateBuilder.toString() );
        editor.apply();
    }

    protected void pullNetworkData(){

        new Thread(()->{
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

            //    Scanner scanner = new Scanner(in);
            //
            //    while (scanner.hasNext()){
            //        //responce =scanner.nextLine();
            //        builder.append(scanner.nextLine());
            //    }
                responce=builder.toString();

                String[] div=responce.split(";");
                String[] stockData;
                for (int i = 0; i < stocksList.size(); i++) {
                    stockData=div[i].split("~");
                    stocksList.get(i).name=stockData[1];
                    stocksList.get(i).nowPrice=stockData[3];
                }
                runOnUiThread(()->updateTabView());
                //updateTabView();
            }
            catch (Exception e){
                Log.w("network", e.toString(),e ); }
        }).start();



    }

}
