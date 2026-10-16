package x.myinvest.popup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

        //直接复制到剪切板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("StockSummary", textViewTotalHolding.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "已复制持仓汇总到剪贴板", Toast.LENGTH_SHORT).show();

        bnOk = (Button) findViewById(R.id.layout_changeGained_button_copyBoard);
        bnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Android 不允许一个应用往另一个应用的输入框里写字，无法自动“粘贴”；
                // 改用分享意图，把文本直接带进微信的聊天输入框，用户只需选择“文件传输助手”再发送即可。
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, textViewTotalHolding.getText().toString());
                // 限定只分享到微信，避免弹出系统分享面板里的其它应用
                shareIntent.setPackage("com.tencent.mm");

                boolean launched;
                try {
                    context.startActivity(shareIntent);
                    launched = true;
                } catch (Exception e) {
                    // 分享失败时兜底：至少把微信打开
                    launched = false;
                    try {
                        Intent openIntent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                        if (openIntent != null) {
                            context.startActivity(openIntent);
                            launched = true;
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (!launched) {
                    // 微信未安装或无法拉起，跳转到应用商店
                    Toast.makeText(context, "未安装微信，正在打开应用商店", Toast.LENGTH_SHORT).show();
                    try {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=com.tencent.mm"));
                        context.startActivity(marketIntent);
                    } catch (Exception e) {
                        Toast.makeText(context, "无法打开微信", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "请在微信里选择“文件传输助手”发送", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
