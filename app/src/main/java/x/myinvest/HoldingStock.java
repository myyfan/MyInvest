package x.myinvest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class HoldingStock extends ScrollView {
    Activity context;
    private ArrayList<Stock> stocksList;
    private TableLayout tableLayout;
    private TextView[][] textViewHandler;



    HoldingStock(Context context,ArrayList<Stock> stockList) {
        super(context);
        this.context=(Activity)context;
        this.stocksList=stockList;
        this.context.getLayoutInflater().inflate(R.layout.view_holding_stock, null);
        tableLayout = new TableLayout(context);
        updateTabView();
        addView(tableLayout);
        //tableLayout = (TableLayout)findViewById(R.id.table_layout_invest);
        //

    }

    protected void addTabRow(Stock stock,int num){
        //protected void addTabRow(String stock,String nowPrice,String price,String number){
        DecimalFormat df = new DecimalFormat("#.00");
        TableRow tableRow=new TableRow(context);
        TextView textView=new TextView(context);
        tableRow=new TableRow(context);
        //股票名称/代码
        textView=new TextView(context);
        textView.setText((num+1)+"."+stock.name+"\n"+stock.code);
        textViewHandler[num][0]=textView;
        tableRow.addView(textView);
        //股票现价/涨幅
        textView=new TextView(context);
        textView.setText(stock.nowPrice+"\n"+stock.increase+"%");
        textViewHandler[num][1]=textView;
        tableRow.addView(textView);
        //购买价格/金额
        textView=new TextView(context);
        textView.setText(stock.price+"\n"+String.format("%.2f",stock.cost));
        textViewHandler[num][2]=textView;
        tableRow.addView(textView);
        //股票数量
        textView=new TextView(context);
        textView.setText(stock.number);
        textViewHandler[num][3]=textView;
        tableRow.addView(textView);
        //盈利及百分比
        textView=new TextView(context);
        textView.setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent)+"%");
        textViewHandler[num][4]=textView;
        tableRow.addView(textView);
        //购买日期
        textView=new TextView(context);
        textView.setText(stock.buyDate);
        textViewHandler[num][5]=textView;
        tableRow.addView(textView);

        tableLayout.addView(tableRow);
    }

    public void refreshText() {
        //

        for (int i = 0; i < stocksList.size(); i++) {
            //  DecimalFormat df = new DecimalFormat("#.00");
            Stock stock = stocksList.get(i);
            //股票名称/代码
            textViewHandler[i][0].setText((i+1)+"."+stock.name+"\n"+stock.code);
            //股票现价/涨幅
            textViewHandler[i][1].setText(stock.nowPrice+"\n"+stock.increase+"%");
            //购买价格/金额
            textViewHandler[i][2].setText(stock.price+"\n"+String.format("%.2f",stock.cost));
            //股票数量/现值
            textViewHandler[i][3].setText(stock.number+"\n"+String.format("%.0f",stock.nowValue));
            //盈利及百分比
            textViewHandler[i][4].setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent)+"%");
            //购买日期
            textViewHandler[i][5].setText(stock.buyDate);
        }
    }

    public void updateTabView(){

        textViewHandler = new TextView[ stocksList.size() ][6];
        //textView.setText("浮盈："+String.format("%.0f", gain)+" 实现盈利："+String.format("%.0f",gained)+" 总盈利："+String.format("%.0f",gain+gained));
        tableLayout.removeAllViews();
        tableLayout.setStretchAllColumns(true);
        //添加标题
        TableRow tableRow=new TableRow(context);

        TextView textView=new TextView(context); textView.setText("股票代码"); tableRow.addView(textView);
        textView=new TextView(context);          textView.setText("现价涨幅");      tableRow.addView(textView);
        textView=new TextView(context);          textView.setText("购价成本");  tableRow.addView(textView);
        textView=new TextView(context);          textView.setText("数量现值");  tableRow.addView(textView);
        textView=new TextView(context);          textView.setText("盈亏现值");       tableRow.addView(textView);
        textView=new TextView(context);          textView.setText("购入日");   tableRow.addView(textView);

        tableLayout.addView(tableRow);
        //tableLayout.setDividerDrawable(getResources().getDrawable(R.drawable.bonus_list_item_divider));
        for(int i=0;i<stocksList.size();i++){
            Stock st = stocksList.get(i);
            addTabRow(st,i);
        }
        refreshText();

    }


}
