package x.myinvest;

import android.content.SharedPreferences;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences perf;
    //private SharedPreferences.Editor editor;

    private ArrayList<Stock> stocksList=new ArrayList<>();
    private Stock[] stocksArr;
    private TableLayout tableLayout;


    //private ArrayList<String> stockList = new ArrayList<>();
    //private ArrayList<Float> priceList = new ArrayList<>();
    //private ArrayList<Integer> numberList = new ArrayList<>();
    //private String[] stockArray ;
    //private Integer[] nowPriceArr;
    //private Float[] priceArray ;
    //private Integer[] numberArray ;
    //



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addButton=(Button) findViewById(R.id.add_stock);
        addButton.setOnClickListener(this::addStock);
        tableLayout = (TableLayout)findViewById(R.id.table_layout_invest);
        loadSavedData();
        //pullNetworkData();
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

            for(int i=0;i<stockArrayStr.length;i++){
                Stock savedStock = new Stock(stockArrayStr[i],priceArrayStr[i],numberArrayStr[i]);
                stocksList.add(savedStock);
            }

            //updatDataArray(stockList,priceList,numberList);
            updateTabView();
        }


    }

    protected void addStock(View view){

        String newStockCode=((EditText)findViewById(R.id.editText_stock)).getText().toString();
        String newStockPrice=((EditText) findViewById(R.id.editText_price)).getText().toString();
        String newStockNumber=((EditText) findViewById(R.id.editText_number)).getText().toString();

        if(newStockCode.length()==6){
            stocksList.add(new Stock(newStockCode,newStockPrice ,newStockNumber ));
        }
        //stockList.add(newStockCode);
        //priceList.add(Float.parseFloat(newStockPrice));
        //numberList.add(Integer.parseInt(newStockNumber));
        //updatDataArray(stockList,priceList,numberList);
        //saveData();
        //updateTabView();


    }
    protected void updatDataArray(){

            stocksArr =stocksList.toArray(new Stock[stocksList.size()]);
            //priceArray=price.toArray(new Float[price.size()]);
            //for(int j=0;j<priceArrayFl.length;j++) {
            //    priceArray[j]=priceArrayFl[j].floatValue();
            //    if(1==1);
            //}
            //numberArray=number.toArray(new Integer[number.size()]);
            //for(int j=0;j<numberArrayInt.length;j++) {
            //    numberArray[j]=numberArrayInt[j];
            //}


    }
    protected void updateTabView(){
        tableLayout.removeAllViews();
        tableLayout.setStretchAllColumns(true);
        //添加标题
        TableRow tableRow=new TableRow(this);

        TextView textView=new TextView(this);
        textView.setText("股票代码");
        tableRow.addView(textView);
        //textView=new TextView(this);
        //textView.setText("现价");
        //tableRow.addView(textView);
        textView=new TextView(this);
        textView.setText("购买价格");
        tableRow.addView(textView);
        textView=new TextView(this);
        textView.setText("购买数量");
        tableRow.addView(textView);

        tableLayout.addView(tableRow);
        //tableLayout.setDividerDrawable(getResources().getDrawable(R.drawable.bonus_list_item_divider));
        for(int i=0;i<stocksList.size();i++){
            Stock st = stocksList.get(i);
            addTabRow(st.code, st.price,st.number);
            //addTabRow(stockArray[i],nowPriceArr[i].toString(),priceArray[i].toString(),numberArray[i].toString());
            //tableRow=new TableRow(this);
            //textView=new TextView(this);
            //textView.setText(stockArray[i]);
            //tableRow.addView(textView);
            //textView=new TextView(this);
            //textView.setText(priceArray[i].toString());
            //tableRow.addView(textView);
            //textView=new TextView(this);
            //textView.setText(numberArray[i].toString());
            //tableRow.addView(textView);
            //tableLayout.addView(tableRow);
        }

    }
    protected void addTabRow(String stock,String price,String number){
    //protected void addTabRow(String stock,String nowPrice,String price,String number){
        TableRow tableRow=new TableRow(this);
        TextView textView=new TextView(this);
        tableRow=new TableRow(this);

        textView=new TextView(this);
        textView.setText(stock);
        tableRow.addView(textView);

        //textView=new TextView(this);
        //textView.setText(nowPrice);
        //tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(price);
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText(number);
        tableRow.addView(textView);

        tableLayout.addView(tableRow);
    }
    protected void saveData(){
        SharedPreferences.Editor editor;
        String stock="";
        String price="";
        String num="";

        for (int i=0;i<stocksList.size();i++) {
             Stock st=stocksList.get(i);
             if(i==0){
                 stock+=st.code;
                 price+=st.price;
                 num+=st.number;
             }
             else {
                 stock=stock+","+st.code;
                 price=price+","+st.price;
                 num=num+","+st.number;
             }
        }
        editor=perf.edit();
        editor.putString("buyedStockCode",stock);
        editor.putString("buyedStockPrice",price);
        editor.putString("buyedStockNumber",num);
        editor.apply();
    }

    protected void pullNetworkData(){

        String requestStockStr="";
        for(int i=0;i<stocksList.size();i++){
            Stock st=stocksList.get(i);
            if(st.code.startsWith("0")) requestStockStr +="s_sz"+st.code;
            else if(st.code.startsWith("6")) requestStockStr+="s_sh"+st.code;
            else if(st.code.startsWith("1")) requestStockStr+="sz"+st.code;
            else if(st.code.startsWith("5")) requestStockStr+="sh"+st.code;
        }
       try{
            URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("get");
            InputStream in= connection.getInputStream();
           BufferedReader reader=new BufferedReader(new InputStreamReader(in));

       }
       catch (Exception e){
           Log.w("network", e.toString(),e );
       }



    }

}
