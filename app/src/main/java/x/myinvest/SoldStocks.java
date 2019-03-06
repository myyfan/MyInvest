package x.myinvest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class SoldStocks extends ScrollView {
    private Context context;
    private TableLayout mTableLayout;
    private ArrayList<Stock> soldStocksList;
    public SoldStocks(Context context, ArrayList<Stock> soldStocksList) {
        super(context);
        this.context=context;
        this.soldStocksList=soldStocksList;
        LayoutInflater.from(context).inflate(R.layout.view_saled_stock, this);
        mTableLayout = (TableLayout) findViewById(R.id.view_saledStock_tableLayout);
        mTableLayout.setStretchAllColumns(true);
        updateTableView();
    }

    public void updateTableView() {
        mTableLayout.removeAllViews();
        TableRow tableRow = new TableRow(context);
        TextView textView = new TextView(context);  textView.setText("股票"+"代码");  tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("购价"+"卖价");tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("数量");tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("盈利\n"+"百分比");tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("购买日期\n"+"售出日期");tableRow.addView(textView);
        mTableLayout.addView(tableRow);
        int listLenth=soldStocksList.size();
        for (int i = listLenth-1; i >=0; i--) {
            Stock stock = soldStocksList.get(i);
            tableRow = new TableRow(context);
            textView = new TextView(context);  textView.setText(stock.name+"\n"+stock.code);  tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.price+"\n"+stock.nowPrice);tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.number);tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent));tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.buyDate+"\n"+stock.soldDate);tableRow.addView(textView);
            mTableLayout.addView(tableRow);
        }

    }
}
