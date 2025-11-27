package x.myinvest.popup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;

import x.myinvest.MainActivity;
import x.myinvest.R;
import x.myinvest.StockSummary;
import x.myinvest.StockUtils;

import android.content.ClipData;
import android.content.ClipboardManager;

public class PopupTotalHolding extends LinearLayout {
    TextView textViewTotalHolding;
    Button bnOk;
    Context context;
    public PopupTotalHolding(MainActivity context) {
        super(context);


        //   this.context = context;
        LayoutInflater.from(context).inflate(R.layout.popup_total_holding, this);
        textViewTotalHolding = (TextView) findViewById(R.id.layout_totalHolding_editText_output);

        List<StockSummary> summary = StockUtils.summarizeStocks(context.holdingStocksList);
        summary.sort(Comparator.comparing(StockSummary::getPercentageValue).reversed());

        StringBuilder stringBuilder = new StringBuilder("");
// 打印结果（可用于调试或传给 RecyclerView Adapter）
        for (StockSummary s : summary) {
            //  Log.d("StockSummary", s.toString());
            stringBuilder.append(s.toString());
        }

        textViewTotalHolding.setText(stringBuilder.toString());

        bnOk = (Button) findViewById(R.id.layout_changeGained_button_copyBoard);



        bnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 复制到剪贴板
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("StockSummary", textViewTotalHolding.getText());
                clipboard.setPrimaryClip(clip);


                    try {
                        // 微信的包名
                        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                        if (intent != null) {
                            context.startActivity(intent);
                        } else {
                            // 未安装微信，跳转到应用商店
                            Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=com.tencent.mm"));
                            context.startActivity(marketIntent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "无法打开微信", Toast.LENGTH_SHORT).show();
                    }


                Toast.makeText(context, "已复制持仓汇总到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
