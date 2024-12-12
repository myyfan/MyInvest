package x.myinvest;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import x.myinvest.popup.PopUpModifySoldedStock;
import x.myinvest.popup.PopupRebuyFromSoldedStocks;

public class SoldStocks extends ScrollView {
    private MainActivity context;
    private TableLayout mTableLayout;
    private ArrayList<Stock> soldStocksList;
    public SoldStocks(MainActivity context, ArrayList<Stock> soldStocksList) {
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
        TextView textView = new TextView(context);  textView.setText("股票\n"+"代码");  tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("购价\n"+"卖价");tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("数量");tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("盈利\n"+"百分比");tableRow.addView(textView);
                 textView = new TextView(context);  textView.setText("购买日期\n"+"售出日期");tableRow.addView(textView);
        mTableLayout.addView(tableRow);
       int listLenth=soldStocksList.size();
     /*    for (int i = listLenth-1; i >=((listLenth-1-100)>=0?(listLenth-1-100):0); i--) {
            Stock stock = soldStocksList.get(i);
            tableRow = new TableRow(context);
            textView = new TextView(context);  textView.setText(i+1+"." +stock.name+"\n"+stock.code);  tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.price+"\n"+stock.nowPrice);tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.number);tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent)+"%");tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.buyDate+"\n"+stock.soldDate);tableRow.addView(textView);

            tableRow.setOnClickListener(( view) -> {
                Intent intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                String st ="" ;
                if (stock.code.startsWith("0")) st="sz";
                else if (stock.code.startsWith("6")) st="sh";
                intent.setData(Uri.parse("https://gu.qq.com/"+st+stock.code));//为Intent设置数据
                getContext().startActivity(intent);//将Intent传递给Activity
            });
            tableRow.setOnLongClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater menuInflater = new MenuInflater(context);
                menuInflater.inflate(R.menu.popup_soldedstocks_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.popupSoldedMenu_re_buy:

                            showPopupRebuyFormSoldedStocks(i);
                            // 处理选项1的点击事件
                            return true;
                        case R.id.popupSoldedMenu_modify:
                         //   context.showPopUpSaleStock(num);
                            // 处理选项2的点击事件
                            return true;
                        case R.id.popupSoldedMenu_delete:
                        //    context.showPopUpModifyStock(num);
                            // 处理选项2的点击事件
                            return true;

                        default:
                            return false;
                    }
                });
                return true;
            });


            mTableLayout.addView(tableRow);
        }*/
        for (int i = listLenth-1; i >=((listLenth-1-100)>=0?(listLenth-1-100):0); i--) {
            addTabRow(soldStocksList.get(i),i);
        }

    }

    protected void addTabRow(Stock stock,int num){


            TableRow tableRow = new TableRow(context);
            TextView textView = new TextView(context);  textView.setText(num+1+"." +stock.name+"\n"+stock.code);  tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.price+"\n"+stock.nowPrice);tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.number);tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(String.format("%.2f",stock.earn)+"\n"+String.format("%.2f",stock.earnPercent)+"%");tableRow.addView(textView);
            textView = new TextView(context);  textView.setText(stock.buyDate+"\n"+stock.soldDate);tableRow.addView(textView);

            tableRow.setOnClickListener(( view) -> {
                Intent intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                String st ="" ;
                if (stock.code.startsWith("0")) st="sz";
                else if (stock.code.startsWith("6")) st="sh";
                intent.setData(Uri.parse("https://gu.qq.com/"+st+stock.code));//为Intent设置数据
                getContext().startActivity(intent);//将Intent传递给Activity
            });
            tableRow.setOnLongClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater menuInflater = new MenuInflater(context);
                menuInflater.inflate(R.menu.popup_soldedstocks_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.popupSoldedMenu_re_buy:

                            showPopupRebuyFormSoldedStocks(num);
                            // 处理选项1的点击事件
                            return true;
                        case R.id.popupSoldedMenu_modify:
                            showPopupModifySoldedStock(num);
                            //   context.showPopUpSaleStock(num);
                            // 处理选项2的点击事件
                            return true;
                        case R.id.popupSoldedMenu_delete:
                            context.gained -= context.soldStockList.get(num).earn;
                            context.soldStockList.remove(num);
                            context.soldStocks.updateTableView();
                            context.saveSoldData();
                            //    context.showPopUpModifyStock(num);
                            // 处理选项2的点击事件
                            return true;

                        default:
                            return false;
                    }
                });
                return true;
            });


            mTableLayout.addView(tableRow);

    };
    public void showPopupRebuyFormSoldedStocks(int num) {
        View pop = new PopupRebuyFromSoldedStocks(context,soldStocksList,num);
        context.showPopupWindows(pop);
    }
    public void showPopupModifySoldedStock(int num) {
        View pop = new PopUpModifySoldedStock(context,soldStocksList, context.soldStocks, num);
        context.showPopupWindows(pop);
    }
}
