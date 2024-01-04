package x.myinvest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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
import android.widget.TableLayout;
import android.widget.TableRow;
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

import x.myinvest.popup.PopUpAddStock;
import x.myinvest.popup.PopUpModifyHoldingStock;
import x.myinvest.popup.PopUpSaleStock;
import x.myinvest.popup.PopupChangeGained;
import x.myinvest.popup.PopupDelStock;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences perfHoldingStocks;
    private SharedPreferences perfSoldStocks;
    //private SharedPreferences.Editor editor;
    private TextView textView_gain;
    private ArrayList<Stock> holdingStocksList = new ArrayList<>();
    private ArrayList<Stock> soldStockList = new ArrayList<>();
    public Timer timer;
    public TimerTask timerTask;
    public HoldingStock holdingStock;//视图
    public SoldStocks soldStocks;//视图
    private LinearLayout mainView;//主视图
    private Stock shangZhengZhiShu;//上证指数
    private String tenYears;//十年国债利率
    private String[] banKuaiMingCheng = {"上海A股", "深圳A股", "沪深A股", "深市主板", "中小板", "创业板"};
    private String[] jingTaiShiYingLv = new String[6];//市场平均静态市盈率，0上海A股，1深圳A股，2沪深A股，3深市主板，4中小板，5创业板
    private String[] dongTaiShiYingLv = new String[6];//市场平均动态市盈率，0上海A股，1深圳A股，2沪深A股，3深市主板，4中小板，5创业板
    private String[] shiJingLv = new String[6];//市场平均市净率，0上海A股，1深圳A股，2沪深A股，3深市主板，4中小板，5创业板
    private String[] jingZiChanShouYiLv=new String[6];//市场平均净资产收益率，0上海A股，1深圳A股，2沪深A股，3深市主板，4中小板，5创业板
    private String[] guXiLv = new String[6];//市场平均股息率，0上海A股，1深圳A股，2沪深A股，3深市主板，4中小板，5创业板
    private String shangZhengSY;//上证平均收益率
    private double shangZhengJingZiChanShouYiLv;//上证平均净资产收益率
    private float haveMoney = 0;//预期最大投入金额
    private String cangWei;//仓位
    private double moneyNeedInvest = 0;//根据仓位及最大投入金额计算的需投入资金
    private double moneyNeedAdd = 0;//应投减已投入金额
    private double gain;//浮盈
    public double gained = -6731;//无法详细列出的历史盈利金额
    private Double allValue = 0.0;//总市值
    private Double dongTaiJinE=0.0;//参与动态控制的金额

    private int reflashCount = 0;
    private int getDataCount = 0;//数据更新次数计数
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        textView_gain = (TextView) findViewById(R.id.textView_gain);
        //弹出显示各板块相关数据,已经被后面重新定义的点击功能所替代
        textView_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout popUp = new TableLayout(MainActivity.this);
                popUp.setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.line_h));
                popUp.setBackgroundColor(0xeeeeeeee);
                TableRow tableRow = new TableRow(context);
                TextView textView = new TextView(context);
                textView.setText("板块名称");
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("静态市盈率");
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("滚动市盈率");
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("市净率");
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("资收率");
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("股息率");
                tableRow.addView(textView);
                popUp.addView(tableRow);
                for (int i = 0; i < 6; i++) {
                    tableRow = new TableRow(context);
                    textView = new TextView(context);
                    textView.setText(banKuaiMingCheng[i]);
                    tableRow.addView(textView);
                    textView = new TextView(context);
                    textView.setText(jingTaiShiYingLv[i]);
                    tableRow.addView(textView);
                    textView = new TextView(context);
                    textView.setText(dongTaiShiYingLv[i]);
                    tableRow.addView(textView);
                    textView = new TextView(context);
                    textView.setText(shiJingLv[i]);
                    tableRow.addView(textView);
                    textView = new TextView(context);
                    textView.setText(jingZiChanShouYiLv[i]);
                    tableRow.addView(textView);
                    textView = new TextView(context);
                    textView.setText(guXiLv[i]);
                    tableRow.addView(textView);
                    popUp.addView(tableRow);
                }
                showPopupWindows(popUp);

            }
        });
        shangZhengZhiShu = new Stock();
        //加载保存的数据到holdingStocksList和soldStockList
        loadSavedData();
        //新建两个股票的视图类
        holdingStock = new HoldingStock(this, holdingStocksList);
        //holdingStock.updateTabView();
        soldStocks = new SoldStocks(this, soldStockList);
        //soldStocks.updateTableView();
        // 两个股票视图之间的分隔符
        TextView tvSpace = new TextView(this);
        tvSpace.setHeight(100);
        //把三个视图添加进去
        mainView = (LinearLayout) findViewById(R.id.mainview);
        mainView.addView(holdingStock);
        mainView.addView(tvSpace);
        mainView.addView(soldStocks);
        pullQuanShiChangShuJu();


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
                View popUp = new PopUpSaleStock(this,holdingStocksList,soldStockList);
                showPopupWindows(popUp);
                break;
            case R.id.mainMenu_addStock:
                showPopUpAddStock();
                //PopUpAddStock popAddStock = new PopUpAddStock(this);
                break;
            case R.id.mainMenu_delStock:
                showPopUpDelStock();
                break;
            case R.id.mainMenu_modifyStock:
                showPopUpModifyStock();
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
            case R.id.mainMenu_tenYearShouYiLv:
                showPopupSetTenYearShouYiLv();
                break;
            case R.id.mainMenu_queryTenYearShouYiLv:
                Uri uri = Uri.parse("https://m.cn.investing.com/rates-bonds/china-10-year-bond-yield#");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
            case R.id.mainMenu_dongTaiJinE:
                showPopupSetDongTaiJinE();
                break;
            case R.id.mainMenu_dianShuCangWeiJiDieFu:
                showPopupJiSuanDianShuCangWeiJiDieFu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showPopupChangeGaiend() {
        View popUp = new PopupChangeGained(this,Double.parseDouble(perfHoldingStocks.getString("haveGained", "0")));
        showPopupWindows(popUp);

    }

    void showPopUpAddStock() {
        View popUp = new PopUpAddStock(this);
        //mainFrameLayout.addView(popUp);
        showPopupWindows(popUp);
    }

    void showPopUpModifyStock(){
        View popUp = new PopUpModifyHoldingStock(this,holdingStocksList,holdingStock);
        //mainFrameLayout.addView(popUp);
        showPopupWindows(popUp);

    }

    void showPopupSaleStock() {
        //显示弹出窗口
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xddffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText row = new EditText(this);
        EditText price = new EditText(this);
        row.setHint("输入要卖出的股票行号");
        price.setHint("输入卖出的价格");

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(row, layoutParams);
        linearLayout.addView(price, layoutParams);
        linearLayout.addView(ok, layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a =row.getText().toString();
                String b =price.getText().toString();
                if (holdingStocksList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "没有持股", Toast.LENGTH_LONG).show();
                }
                else {

                    if (a.isEmpty() || b.isEmpty()) {
                        Toast.makeText(MainActivity.this, "输入为空", Toast.LENGTH_LONG).show();
                    }
                    else{
                        int rowNum = Integer.parseInt(a);//攻取行号
                        if (rowNum <= 0 || rowNum > holdingStocksList.size()) {
                            Toast.makeText(MainActivity.this, "超出范围", Toast.LENGTH_LONG).show();
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
                            if (st.code.startsWith("0") || st.code.startsWith("3") || st.code.startsWith("6")) {
                                st.nowValue = Double.parseDouble(st.nowPrice) * numInt;
                                st.nowValue = st.nowValue * (1 - 0.001) - (st.nowValue > 16666.67 ? st.nowValue * 0.0003 : 5);
                                st.cost = Double.parseDouble(st.price) * numInt;
                                st.cost = st.cost + (st.cost > 16666.67 ? st.cost * 0.0003 : 5);
                                st.earn = st.nowValue - st.cost;
                                st.earnPercent = st.earn / st.cost * 100;
                            }
                            else {
                                st.nowValue = Double.parseDouble(st.nowPrice) * numInt * (1 - 0.0003);
                                st.cost = Double.parseDouble(st.price) * numInt * (1 + 0.0003);
                                st.earn = st.nowValue - st.cost;
                                st.earnPercent = st.earn / st.cost * 100;

                                double shouXuFeiMai3,shouXuFeiMai4;
                                shouXuFeiMai3=Double.parseDouble(st.price) * numInt * 0.0003;
                                shouXuFeiMai3=shouXuFeiMai3>0.1?shouXuFeiMai3:0.1;
                                shouXuFeiMai3=Double.parseDouble(String.format("%.2f",shouXuFeiMai3));
                                shouXuFeiMai4=Double.parseDouble(st.nowPrice) * numInt * 0.0003;
                                shouXuFeiMai4=shouXuFeiMai4>0.1?shouXuFeiMai4:0.1;
                                shouXuFeiMai4=Double.parseDouble(String.format("%.2f",shouXuFeiMai4));
                                st.nowValue = Double.parseDouble(st.nowPrice) * numInt -shouXuFeiMai4;
                                st.cost = Double.parseDouble(st.price) * numInt +shouXuFeiMai3;
                                st.earn = st.nowValue - st.cost;
                                st.earnPercent = st.earn / st.cost * 100;
                            }

                            gained += st.earn;
                            holdingStock.updateTabView();
                            soldStocks.updateTableView();
                            saveHoldingData();
                            saveSoldData();


                        }
                    }
                }
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

        linearLayout.addView(name, layoutParams);
        linearLayout.addView(money, layoutParams);
        linearLayout.addView(ok, layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Stock st = new Stock();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");//卖出日期

                st.name = "分红";
                st.code = name.getText().toString();
                st.soldDate = dateFormat.format(new Date());
                st.earn = Double.parseDouble(money.getText().toString());
                soldStockList.add(st);


                //计算盈利数据

                gained += st.earn;
             //   holdingStock.updateTabView();
                soldStocks.updateTableView();
              //  saveHoldingData();
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

        linearLayout.addView(row, layoutParams);
        linearLayout.addView(ok, layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowNum = Integer.parseInt(row.getText().toString());//获取行号
                if (rowNum > soldStockList.size()) return;
                if (rowNum < 1) return;
                gained -= soldStockList.get(rowNum - 1).earn;
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

        linearLayout.addView(textHaveMoney, layoutParams);
        linearLayout.addView(ok, layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveMoney = Float.parseFloat(textHaveMoney.getText().toString());//攻取行号
                perfHoldingStocks.edit().putFloat("haveMoney", haveMoney).apply();

            }
        });
    }

    void showPopupSetTenYearShouYiLv() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText textHaveMoney = new EditText(this);
        textHaveMoney.setHint(tenYears);

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(textHaveMoney, layoutParams);
        linearLayout.addView(ok, layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenYears = textHaveMoney.getText().toString();//获取收益率
                perfHoldingStocks.edit().putString("tenYears", tenYears).apply();
                updataTextView();
            }
        });
    }

    void showPopupSetDongTaiJinE() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText textDongTaiJinE = new EditText(this);
        textDongTaiJinE.setHint(dongTaiJinE.toString());

        Button ok = new Button(this);
        ok.setText("确定");

        linearLayout.addView(textDongTaiJinE, layoutParams);
        linearLayout.addView(ok, layoutParams);
        showPopupWindows(linearLayout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dongTaiJinE = Double.parseDouble(textDongTaiJinE.getText().toString());//获取收益率
                perfHoldingStocks.edit().putString("dongTaiJinE", dongTaiJinE.toString()).apply();
                updataTextView();
            }
        });
    }

    void showPopupJiSuanDianShuCangWeiJiDieFu() {
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setOrientation(LinearLayout.VERTICAL);
        tableLayout.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        tableLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TableRow tableRow=new TableRow(context);
        TextView textView=new TextView(this);
        textView.setText("目标仓位    ");
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText("预估点位    ");
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText("预计涨跌    ");
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText("预估PB    ");
        tableRow.addView(textView);

        textView=new TextView(this);
        textView.setText("预估PE");
        tableRow.addView(textView);

        tableLayout.addView(tableRow, layoutParams);

        Double [] yuCeCangWeiTable={0.0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95,1.0,1.05,1.10};

        double tenYears=Double.parseDouble(this.tenYears);
     //   double shangZhengKaiPanDianWei=Double.parseDouble(shangZhengZhiShu.nowPrice)/(1+Double.parseDouble(shangZhengZhiShu.increase)/100);
     //   float f1=Float.parseFloat(dongTaiShiYingLv[2]);
        //开盘点位改为当前点位,PE改为计算当前PE
        double shangZhengKaiPanDianWei=Double.parseDouble(shangZhengZhiShu.nowPrice);
      //double shangZhengKaiPanDianWei=Double.parseDouble(shangZhengZhiShu.nowPrice)/(1+Double.parseDouble(shangZhengZhiShu.increase)/100);
        double f1=Float.parseFloat(dongTaiShiYingLv[2])*(1+Double.parseDouble(shangZhengZhiShu.increase)/100); //计算当前市盈率

        double zuiDiShouYiLv=0.015*tenYears;
        double zuiDaShouyiLv=0.029*tenYears;
        Double yuCeDianWei;
        Double yuJiZhangDie;
        Double yuJiPB;
        Double yuJiPE;
        Double k;

        for(int i=0;i<yuCeCangWeiTable.length;i++){
            Double  j=((yuCeCangWeiTable[i]*(zuiDaShouyiLv-zuiDiShouYiLv))+zuiDiShouYiLv)*f1;//计算目标他们的收益率与当前收益率的比值
          //  k=yuCeCangWeiTable[i]*100;
            yuCeDianWei=shangZhengKaiPanDianWei/j;
          //  yuCeDianWei=Double.parseDouble(shangZhengZhiShu.nowPrice)/j;
            yuJiZhangDie=(yuCeDianWei-shangZhengKaiPanDianWei)/shangZhengKaiPanDianWei*100;
            yuJiPB=Double.parseDouble(shiJingLv[2])/j;
            yuJiPE=f1/j;

            tableRow=new TableRow(context);

            //目标仓位
            textView=new TextView(this);
            textView.setText(String.format("%.0f",yuCeCangWeiTable[i]*100)+"%");
            tableRow.addView(textView);

            //目标仓位的点位
            textView=new TextView(this);
            textView.setText(String.format("%.0f", yuCeDianWei));
            tableRow.addView(textView);

            //目标仓位的距当前点位的涨跌幅
            textView=new TextView(this);
            textView.setText(String.format("%.2f", yuJiZhangDie)+"%");
            tableRow.addView(textView);

            //预估目标仓位的PB
            textView=new TextView(this);
            textView.setText(String.format("%.2f", yuJiPB));
            tableRow.addView(textView);

            //预估目标仓位的PE
            textView=new TextView(this);
            textView.setText(String.format("%.2f", yuJiPE));
            tableRow.addView(textView);

            tableLayout.addView(tableRow, layoutParams);
        }
    //    EditText textDongTaiJinE = new EditText(this);
    //    textDongTaiJinE.setHint(dongTaiJinE.toString());
//
    //    Button ok = new Button(this);
    //    ok.setText("确定");




        showPopupWindows(tableLayout);

    }

    void showPopupWindows(View popUp) {
        PopupWindow popupWindow = new PopupWindow(popUp, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
        popupWindow.isShowing();
    }

    protected void updataTextView() {
        ++reflashCount;  //  +" ref:"+reflashCount+"|"+getDataCount
        textView_gain.setText("上证指数:" + shangZhengZhiShu.nowPrice + "涨幅:" + shangZhengZhiShu.increase + "动盈率:" + dongTaiShiYingLv[2] + " 收益率:" + shangZhengSY + "%\n" + "国债:" + tenYears + "  仓位:" + cangWei + "%" + "  应投:" + String.format("%.0f", moneyNeedInvest) + "  追加:" + String.format("%.0f", moneyNeedAdd) + "*" + getDataCount + "\n实现盈利:" + String.format("%.0f", gained) + " 总盈利：" + String.format("%.0f", gain + gained)+ " 浮盈:" + String.format("%.0f", gain)  + "现值:" + String.format("%.0f", allValue));
        textView_gain.setOnClickListener((view) -> {
            Intent intent=new Intent();//创建Intent对象
            intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
            intent.setData(Uri.parse("https://gu.qq.com/sh000001"));//为Intent设置数据
            startActivity(intent);//将Intent传递给Activity
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timer!=null){timer.cancel();}
        timer = new Timer();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                pullNetworkData();
                //      runOnUiThread(() -> {
                //          updataTextView();
                //          holdingStock.refreshText();
                //      });
                //   runOnUiThread(()->{
                //       //textView.setText("上证指数:"+shangZheng.nowPrice+"涨幅:"+shangZheng.increase+"国债:"+tenYears+" 实现盈利："+String.format("%.0f",gained)+"\n浮盈："+String.format("%.0f", gain)+" 总盈利："+String.format("%.0f",gain+gained)+"现值:"+String.format("%.0f",allValue));
                //       updataTextView();
                //       holdingStock.refreshText();
                //   });
            }
        };
        //   holdingStock.refreshText();

      //  timerTask.cancel();
        timer.schedule(timerTask , 0, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerTask.cancel();
        timer.cancel();
    }

    public void loadSavedData() {
        //加载持有中的股票数据
        String buyedStockCodeSaved = null;
        String buyedStockPriceSaved = null;
        perfHoldingStocks = getSharedPreferences("buyedStock", 0);
        //加载预期投入的最大资金量
        haveMoney = perfHoldingStocks.getFloat("haveMoney", 0);
        // 加载10年国债收益率
        tenYears = perfHoldingStocks.getString("tenYears", "0");
        //加载持有的股票代码
        dongTaiJinE=Double.parseDouble(perfHoldingStocks.getString("dongTaiJinE", "0"));
        gained=Double.parseDouble(perfHoldingStocks.getString("haveGained", "0"));
        String[] stockArrayStr = perfHoldingStocks.getString("buyedStockCode", "").split(",");
        if (!stockArrayStr[0].isEmpty()) {
            //perfHoldingStocks = new ArrayList<Stock>(stockArrayStr.length);

            //加载股票价格
            String[] priceArrayStr = perfHoldingStocks.getString("buyedStockPrice", "").split(",");
            //加载股票数量
            String[] numberArrayStr = perfHoldingStocks.getString("buyedStockNumber", "").split(",");
            String[] buyDate = perfHoldingStocks.getString("buyDate", "").split(",");
            for (int i = 0; i < stockArrayStr.length; i++) {
                Stock savedStock = new Stock(stockArrayStr[i], priceArrayStr[i], numberArrayStr[i], buyDate[i]);
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

        for (int i = 0; i < lenSoldArr; i += 9) {
            Stock stock = new Stock();
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

    public void addStock(String newStockCode, String newStockPrice, String newStockNumber) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");
        String date = dateFormat.format(new Date());
        if (newStockCode.length() != 6 || newStockPrice.isEmpty() || newStockNumber.isEmpty()) {
            Toast.makeText(this, "输入数据错误", Toast.LENGTH_LONG).show();
        } else {
            holdingStocksList.add(new Stock(newStockCode, newStockPrice, newStockNumber, date));
            Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
        }
        saveHoldingData();
        holdingStock.updateTabView();
    }


    public void delStock(String delRow) {
        if (delRow.isEmpty()) {
            Toast.makeText(MainActivity.this, "行号为空", Toast.LENGTH_LONG).show();
        } else {
            int dr = Integer.parseInt(delRow);
            if (dr<=0 || dr > holdingStocksList.size()) {
                Toast.makeText(MainActivity.this, "超出范围", Toast.LENGTH_LONG).show();
            } else {
                holdingStocksList.remove(dr - 1);
                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                saveHoldingData();
                holdingStock.updateTabView();
            }
        }


    }

    public void changeGained(double gained) {
     //   this.gained = gained;
        perfHoldingStocks.edit().putString("haveGained", gained + "").apply();
    }

    //  protected void updatDataArray(){

    public void pullNetworkData() {

        //new Thread(()->{
        //组建查询队列
        StringBuilder builder = new StringBuilder();
        //  String requestStockStr="";
        builder.append("http://qt.gtimg.cn/q=");
        for (int i = 0; i < holdingStocksList.size(); i++) {
            Stock st = holdingStocksList.get(i);
            if (st.code.startsWith("0")) builder.append("s_sz" + st.code + ",");
            else if (st.code.startsWith("6")) builder.append("s_sh" + st.code + ",");
            else if (st.code.startsWith("1")) builder.append("sz" + st.code + ",");
            else if (st.code.startsWith("5")) builder.append("sh" + st.code + ",");
            //  if(st.code.startsWith("0")) requestStockStr +="s_sz"+st.code+",";
            //  else if(st.code.startsWith("6")) requestStockStr+="s_sh"+st.code+",";
            //  else if(st.code.startsWith("1")) requestStockStr+="sz"+st.code+",";
            //  else if(st.code.startsWith("5")) requestStockStr+="sh"+st.code+",";
        }
        //尾部增加查询上证指数
        builder.append("sh000001");
        //网络查询及接收分析数据
        try {
            URL url = new URL(builder.toString());
            //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream in = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gbk"));
            String line;
            while ((line = reader.readLine()) != null) {
                //responce =scanner.nextLine();
                builder.append(line);
            }
            String responce;
            responce = builder.toString();

            String[] div = responce.split(";");
            String[] stockData;
            //循环填充数据
            double gain = 0;//浮盈
            double allValue = 0.0;
            double shouXuFeiMai3;//省得循环中不停重新分配变量
            double shouXuFeiMai4;
            for (int i = 0; i < holdingStocksList.size(); i++) {
                Stock st = holdingStocksList.get(i);
                if (i == div.length) {
                    st.name = "无返回数据";
                    runOnUiThread(() -> holdingStock.refreshText());
                    return;
                }
                stockData = div[i].split("~");
                //读取返回数据并填充
                if (stockData[2].equals(st.code)) {

                    st.name = stockData[1];
                    st.nowPrice = stockData[3];

                    if (st.code.startsWith("0") || st.code.startsWith("3") || st.code.startsWith("6")) {
                        st.increase = stockData[5];
                    } else {
                        st.increase = stockData[32];
                    }
                    //计算盈利数据
                    int numInt = Integer.parseInt(st.number);
                    //区分是股票还是基金
                    if (st.code.startsWith("0") || st.code.startsWith("3") || st.code.startsWith("6")) {
                        st.nowValue = Double.parseDouble(st.nowPrice) * numInt;
                        st.nowValue = st.nowValue * (1 - 0.001) - (st.nowValue > 16666.67 ? st.nowValue * 0.0003 : 5);
                        st.cost = Double.parseDouble(st.price) * numInt;
                        st.cost = st.cost + (st.cost > 16666.67 ? st.cost * 0.0003 : 5);
                        st.earn = st.nowValue - st.cost;
                        st.earnPercent = st.earn / st.cost * 100;
                    }
                    else {
                        shouXuFeiMai3=Double.parseDouble(st.price) * numInt * 0.0003;
                        shouXuFeiMai3=shouXuFeiMai3>0.1?shouXuFeiMai3:0.1;
                        shouXuFeiMai3=Double.parseDouble(String.format("%.2f",shouXuFeiMai3));
                        shouXuFeiMai4=Double.parseDouble(st.nowPrice) * numInt * 0.0003;
                        shouXuFeiMai4=shouXuFeiMai4>0.1?shouXuFeiMai4:0.1;
                        shouXuFeiMai4=Double.parseDouble(String.format("%.2f",shouXuFeiMai4));
                        st.nowValue = Double.parseDouble(st.nowPrice) * numInt -shouXuFeiMai4;
                        st.cost = Double.parseDouble(st.price) * numInt +shouXuFeiMai3;
                        st.earn = st.nowValue - st.cost;
                        st.earnPercent = st.earn / st.cost * 100;


                    }
                    gain += st.earn;
                    allValue += st.nowValue;
                } else {
                    st.name = "不匹配";
                    runOnUiThread(() -> holdingStock.refreshText());
                    return;
                }

            }
            this.gain = gain;//浮盈
            this.allValue = allValue;
            //填入上证指数数据
            stockData = div[holdingStocksList.size()].split("~");
            shangZhengZhiShu.nowPrice = stockData[3];
            shangZhengZhiShu.increase = stockData[32];
            //connection.disconnect();
            //排序
            orderTheList();

            //计算理论仓位
      //      float f1 = Float.parseFloat(dongTaiShiYingLv[0]);//动态市盈率
      //      double shangZhengShouYiLv =1/(f1 * (1 + Double.parseDouble(shangZheng.increase) / 100));
            //     //计算上证收益率
            //     //考虑时间因素
            //     Calendar cal = Calendar.getInstance();
            //     //  int y=cal.get(Calendar.YEAR);
            //     int m = cal.get(Calendar.MONTH) + 1;
            //     int d = cal.get(Calendar.DATE);
            //     float rase = 0;
//
            //     //使用上证计算规则
            //     if (m > 4)  rase = m - 5;
            //      else rase = m + 7;
            //     rase = rase + (float) d / 30;
            //     double shangZhengShouYiLv = (1 + shangZhengJingZiChanShouYiLv * rase / 12) / (f1 * (1 + Double.parseDouble(shangZheng.increase) / 100));
//
            //     //使用中证计算规则
            //  //   if (m > 4) {
            //  //       if (m > 10) rase = m - 11;
            //  //       else if (m > 8) rase = m - 9;
            //  //       else if (m > 4) rase = m - 5;
            //  //   } else rase = m + 1;
            //  //   rase = rase + (float) d / 30;
            //  //   double shangZhengShouYiLv = (1 + shangZhengJingZiChanShouYiLv * rase / 12) / (f1 * (1 + Double.parseDouble(shangZheng.increase) / 100));
            //     //不考虑时间因素
            float f1=Float.parseFloat(dongTaiShiYingLv[2]);
            //    f1=(float)11.5;
            double shangZhengShouYiLv = 1/(f1*(1+Double.parseDouble(shangZhengZhiShu.increase)/100));
            shangZhengSY=String.format("%.2f",shangZhengShouYiLv*100);

            double tenYears=Double.parseDouble(this.tenYears);
            double zuiDiShouYiLv=0.015*tenYears;
            double zuiDaShouyiLv=0.029*tenYears;
            double cangWei=(shangZhengShouYiLv - zuiDiShouYiLv) / (zuiDaShouyiLv - zuiDiShouYiLv);
            double allIntvestingMoney=haveMoney+gained+gain;
            moneyNeedInvest=(allIntvestingMoney)>=dongTaiJinE?(allIntvestingMoney+dongTaiJinE*(cangWei-1)):(allIntvestingMoney)*cangWei;//最大可投金额加上全部账面收益乘上仓位
       //     moneyNeedInvest=(haveMoney+gained+this.gain)*cangWei;//最大可投金额加上全部账面收益乘上仓位
            moneyNeedAdd=moneyNeedInvest-allValue;
      //      moneyNeedAdd=moneyNeedInvest-this.allValue;
            this.cangWei = String.format("%.2f",cangWei*100);


            //获取十年国债利率

            //in.close();
            //         url=new URL("https://forexdata.wallstreetcn.com/real?en_prod_code=CHINA10YEAR&fields=prod_name,last_px,px_change,px_change_rate,high_px,low_px,open_px,preclose_px,business_amount,business_balance,market_value,turnover_ratio,dyn_pb_rate,amplitude,pe_rate,bps,hq_type_code,trade_status,bid_grp,offer_grp,business_amount_in,business_amount_out,circulation_value,securities_type,update_time,price_precision,week_52_high,week_52_low");
            //         //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
            //         HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
            //         conn.setRequestMethod("GET");
            //         conn.setRequestProperty("Referer","https://wallstreetcn.com/markets/bonds/CHINA10YEAR");
            //         conn.setRequestProperty("Host","forexdata.wallstreetcn.com");
            //         conn.setRequestProperty("Connection","keep-alive");
            //         conn.connect();
            //         builder.setLength(0);
            //         InputStream  inx= conn.getInputStream();
            //         BufferedReader read = new BufferedReader(new InputStreamReader(inx,"utf-8"));
            //         while ((line=read.readLine())!=null){
            //             //responce =scanner.nextLine();
            //             builder.append(line);
            //         }
            //         responce=builder.toString();
            //         div=responce.split(",");
            //         this.tenYears=div[2];


            //updateTabView();

        } catch (Exception e) {
            Log.w("network", e.toString(), e);
            //  runOnUiThread(()->{
            //      updataTextView();
            //      holdingStock.refreshText();
            //  });
        }
        getDataCount++;
              runOnUiThread(() -> {
                  updataTextView();
                  holdingStock.refreshText();
              });
    }

    protected void pullQuanShiChangShuJu() {
        new Thread(() -> {
            try {//从中证指数公司获取全市场滚动市盈率
                URL url = new URL("https://www.csindex.com.cn/csindex-home/dataServer/queryCsiPeIndustryBytradeDate?tradeDate=&classType=3");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                StringBuilder builder = new StringBuilder();
                String line;
                int i1 = 0;
                while ((line = reader.readLine()) != null) {
                    //responce =scanner.nextLine();
              //      if (i1++ > 500) {
                        builder.append(line);
              //          if (i1 > 600) break;
              //      }

                }
                String responce = builder.toString();

                String[] div = responce.split("\"pe\":\"");
                char a1;
                String lkj;
                for(int i=1;i<10;i++){
                    a1=div[1].charAt(i);
                    if (a1==34) {
                        //              lkj = div[1].substring(0, i);
                        jingTaiShiYingLv[2] = div[1].substring(0, i);
                        break;
                    }
                }

                for(int i=1;i<10;i++){
                    a1=div[1].charAt(i);
                    if (a1==34) {
                        //        lkj = div[2].substring(0, i);
                        dongTaiShiYingLv[2] = div[2].substring(0, i);
                        break;
                    }
                }

                for(int i=1;i<8;i++){
                    a1=div[3].charAt(i);
                    if (a1==34) {
                        //               lkj = div[3].substring(0, i);
                        shiJingLv[2] = div[3].substring(0, i);
                        break;
                    }
                }

                for(int i=1;i<8;i++){
                    a1=div[4].charAt(i);
                    if (a1==34) {
                        //                lkj = div[4].substring(0, i);
                        guXiLv[2] = div[4].substring(0, i);
                        break;
                    }
                }



             /*   String[] div = responce.split("<tr>");
                String[] div1;
                int i2 = 3;
                int i3 = 2;
                int i4;
                int a;
                if(div.length>1) {
                    //  a = div[1].charAt(4);
                    for (; i2 < 9; i2++) {
                        div1 = div[i2].split("<td>");
                        char c1 = div1[2].charAt(0);
                        for (i4 = 1; c1 != 60; i4++) {
                            c1 = div1[2].charAt(i4);
                        }
                        jingTaiShiYingLv[i2 - 3] = div1[2].substring(0, i4 - 1);
                        //   float f1=Float.parseFloat(jingTaiShiYingLv[i2-3]);
                        //   jingTaiShiYingLv[i2-3]=div1[2].substring(0,i4-1);
                    }
                }


                //从中证指数公司获取市场平均动态市盈率
                url = new URL("http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zy2");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                builder = new StringBuilder();
                i1 = 0;
                while ((line = reader.readLine()) != null) {
                    //responce =scanner.nextLine();
                    if (i1++ > 500) {
                        builder.append(line);
                        if (i1 > 600) break;
                    }

                }
                responce = builder.toString();
                div = responce.split("<tr>");
                if(div.length>1) {
                    //       a = div[1].charAt(4);
                    i2 = 3;
                    i3 = 2;
                    for (; i2 < 9; i2++) {
                        div1 = div[i2].split("<td>");
                        char c1 = div1[2].charAt(0);
                        for (i4 = 1; c1 != 60; i4++) {
                            c1 = div1[2].charAt(i4);
                        }
                        dongTaiShiYingLv[i2 - 3] = div1[2].substring(0, i4 - 1);
                        //     float f1=Float.parseFloat(dongTaiShiYingLv[i2-3]);
                        //     dongTaiShiYingLv[i2-3]=div1[2].substring(0,i4-1);
                    }
                }

                //从中证指数公司获取市场平均市净率
                url = new URL("http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zy3");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                builder = new StringBuilder();
                i1 = 0;
                while ((line = reader.readLine()) != null) {
                    //responce =scanner.nextLine();
                    if (i1++ > 500) {
                        builder.append(line);
                        if (i1 > 600) break;
                    }

                }
                responce = builder.toString();
                div = responce.split("<tr>");
                if(div.length>1) {
                    //      a = div[1].charAt(4);
                    i2 = 3;
                    i3 = 2;
                    for (; i2 < 9; i2++) {
                        div1 = div[i2].split("<td>");
                        char c1 = div1[2].charAt(0);
                        for (i4 = 1; c1 != 60; i4++) {
                            c1 = div1[2].charAt(i4);
                        }
                        int i5 = i2 - 3;
                        shiJingLv[i5] = div1[2].substring(0, i4 - 1);
                        jingZiChanShouYiLv[i5] = String.format("%.1f", Double.parseDouble(shiJingLv[i5]) / Double.parseDouble(dongTaiShiYingLv[i5]) * 100);
                        //    float f1=Float.parseFloat(ShiJingLv[i2-3]);
                        //    ShiJingLv[i2-3]=div1[2].substring(0,i4-1);
                    }
                }


                //从中证指数公司获取市场平均股息率
                url = new URL("http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zy4");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                builder = new StringBuilder();
                i1 = 0;
                while ((line = reader.readLine()) != null) {
                    //responce =scanner.nextLine();
                    if (i1++ > 500) {
                        builder.append(line);
                        if (i1 > 600) break;
                    }

                }
                responce = builder.toString();
                div = responce.split("<tr>");
                if(div.length>1) {
                    //     a = div[1].charAt(4);
                    i2 = 3;
                    i3 = 2;
                    for (; i2 < 9; i2++) {
                        div1 = div[i2].split("<td>");
                        char c1 = div1[2].charAt(0);
                        for (i4 = 1; c1 != 60; i4++) {
                            c1 = div1[2].charAt(i4);
                        }
                        guXiLv[i2 - 3] = div1[2].substring(0, i4 - 1);
                        //      float f1=Float.parseFloat(guXiLv[i2-3]);
                        //      guXiLv[i2-3]=div1[2].substring(0,i4-1);
                    }
                }

*/
 /*               if (true || jingTaiShiYingLv[0] == null) {
                    //从乐估乐股获取静态市盈率
                    url = new URL("https://www.legulegu.com/stockdata/market_pe");
                    //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                    builder = new StringBuilder();
                    i1 = 0;
                    while ((line = reader.readLine()) != null) {
                        //responce =scanner.nextLine();
                        if (i1++ > 550) {
                            builder.append(line);
                            if (i1 > 650) break;
                        }

                    }
                    responce = builder.toString();

                    div = responce.split("<td class=\"icon-font-red\">");
                    if(div.length>1) {
                        div1 = div[1].split("</td>");
                        jingTaiShiYingLv[0]=div1[0];
                        div1 = div[2].split("</td>");
                        jingTaiShiYingLv[1]=div1[0];
                        div1 = div[3].split("</td>");
                        jingTaiShiYingLv[4]=div1[0];
                        div1 = div[4].split("</td>");
                        jingTaiShiYingLv[5]=div1[0];
                    }
                    //从乐估乐股获取市净率
                    url = new URL("https://www.legulegu.com/stockdata/market_pb");
                    //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                    builder = new StringBuilder();
                    i1 = 0;
                    while ((line = reader.readLine()) != null) {
                        //responce =scanner.nextLine();
                        if (i1++ > 500) {
                            builder.append(line);
                            if (i1 > 650) break;
                        }

                    }
                    responce = builder.toString();

                    div = responce.split("<td class=\"icon-font-red\">");
                    if(div.length>1) {
                        div1 = div[1].split("</td>");
                        shiJingLv[0]=div1[0];
                        div1 = div[2].split("</td>");
                        shiJingLv[1]=div1[0];
                        div1 = div[3].split("</td>");
                        shiJingLv[4]=div1[0];
                        div1 = div[4].split("</td>");
                        shiJingLv[5]=div1[0];
                    }


                    //从乐估乐股获取动态市盈率
                    url = new URL("https://legulegu.com/stockdata/weight-pe?marketId=000001.SH");
                    //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                    builder = new StringBuilder();
                    i1 = 0;

                    while ((line = reader.readLine()) != null) {
                        //responce =scanner.nextLine();
                        if (i1++ > 400) {
                            builder.append(line);
                            if (i1 > 500) break;
                        }

                    }
                    responce = builder.toString();
                    div = responce.split("style=\"color: #85BEDB; font-weight: bold; padding-left: 10px\">");
                    if(div.length>1) {
                        char c1 = div[1].charAt(0);
                        for (i4 = 0; div[1].charAt(i4) != 62; i4++) {
                            //       c1 = div[1].charAt(i4);
                        }
                        //  i4++;
                        int i8;
                        //       c1 = div[1].charAt(i4);
                        for (i8 = i4+1; div[1].charAt(i8) != 9; i8++) {

                            //           c1 = div[1].charAt(i8);
                        }

                        String i9 =shiJingLv[0];
                        dongTaiShiYingLv[0] = div[1].substring(i4+1, i8);
                        jingZiChanShouYiLv[0] = String.format("%.1f", Double.parseDouble(shiJingLv[0]) / Double.parseDouble(dongTaiShiYingLv[0]) * 100);

                        //           c1 = div[1].charAt(0);
                        //           for (i4 = 1; c1 != 62; i4++) {
                        //               c1 = div[2].charAt(i4);
                        //           }
//
                        //           jingZiChanShouYiLv[1] = String.format("%.1f", Double.parseDouble(shiJingLv[1]) / Double.parseDouble(dongTaiShiYingLv[1]) * 100);

                        //           c1 = div[3].charAt(0);
                        //           for (i4 = 1; c1 != 60; i4++) {
                        //               c1 = div[3].charAt(i4);
                        //           }
                        //           dongTaiShiYingLv[5] = div[3].substring(0, i4 - 1);
                        //           jingZiChanShouYiLv[5] = String.format("%.1f", Double.parseDouble(shiJingLv[5]) / Double.parseDouble(dongTaiShiYingLv[5]) * 100);
                    }




                    //  jingZiChanShouYiLv[i5] = String.format("%.1f", Double.parseDouble(ShiJingLv[i5]) / Double.parseDouble(dongTaiShiYingLv[i5]) * 100);
                    //    float f1=Float.parseFloat(ShiJingLv[i2-3]);
                    //    ShiJingLv[i2-3]=div1[2].substring(0,i4-1);

                }

                //从上证公司获取上证市场市盈率
                url = new URL("http://www.sse.com.cn/market/stockdata/statistic/");
                //URL url=new URL("http://qt.gtimg.cn/q="+requestStockStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                builder = new StringBuilder();
                i1 = 0;
                while ((line = reader.readLine()) != null) {
                    //responce =scanner.nextLine();
                    if (i1++ > 470) {
                        builder.append(line);
                        if (i1 > 520) break;
                    }

                }
                responce = builder.toString();
                div = responce.split("home_sjtj_zb.ratioOfPe = '");
                if(div.length>1) {
                    //     a = div[1].charAt(4);
                    i2 = 3;
                    i3 = 2;
                    char c1 = div[1].charAt(0);
                    for (i4 = 1; c1 != 39; i4++) {
                        c1 = div[1].charAt(i4);
                    }
                    jingTaiShiYingLv[0] = div[1].substring(0, i4 - 1);
                    //           String test =div[1].substring(0, i4 - 1);
                    // test =div[1].substring(0, i4 - 1);
                    //      guXiLv[i2-3]=div1[2].substring(0,i4-1);

                }
*/
                shangZhengJingZiChanShouYiLv = Double.parseDouble(shiJingLv[2]) / Double.parseDouble(dongTaiShiYingLv[2]);

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
        int j = holdingStocksList.size() - 1;
        int haveOrderRow = j;
        for (int i = 0; i < holdingStocksList.size(); i++) {
            for (int k = 0; k < j; k++) {
                if (holdingStocksList.get(k).earnPercent < holdingStocksList.get(k + 1).earnPercent) {
                    temp = holdingStocksList.get(k);
                    holdingStocksList.set(k, holdingStocksList.get(k + 1));
                    holdingStocksList.set(k + 1, temp);
                    haveOrderRow = j;
                }
            }
            j = haveOrderRow;
        }
    }


    public void saveHoldingData() {
        SharedPreferences.Editor editor;

        StringBuilder stockBuilder = new StringBuilder("");
        StringBuilder priceBuilder = new StringBuilder("");
        StringBuilder numBuilder = new StringBuilder("");
        StringBuilder dateBuilder = new StringBuilder("");
        for (int i = 0; i < holdingStocksList.size(); i++) {
            Stock st = holdingStocksList.get(i);
            // if(i==0){
            stockBuilder.append(holdingStocksList.get(i).code + ",");
            priceBuilder.append(holdingStocksList.get(i).price + ",");
            numBuilder.append(holdingStocksList.get(i).number + ",");
            dateBuilder.append(holdingStocksList.get(i).buyDate + ",");

        }

        editor = perfHoldingStocks.edit();
        editor.putString("buyedStockCode", stockBuilder.toString());
        editor.putString("buyedStockPrice", priceBuilder.toString());
        editor.putString("buyedStockNumber", numBuilder.toString());
        editor.putString("buyDate", dateBuilder.toString());
        editor.apply();
    }

    public void saveSoldData() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Stock stock1 : soldStockList) {
            stringBuilder.append(stock1.name + ",")
                    .append(stock1.code + ",")
                    .append(stock1.price + ",")
                    .append(stock1.nowPrice + ",")
                    .append(stock1.number + ",")
                    .append(stock1.earn + ",")
                    .append(stock1.earnPercent + ",")
                    .append(stock1.buyDate + ",")
                    .append(stock1.soldDate + ",");
        }
        perfHoldingStocks.edit().putString("soldStock", stringBuilder.toString()).apply();

    }


}
