package x.myinvest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TableLayout;

public class SaledStocks extends ScrollView {
    private Context context;
    private TableLayout mTableLayout;
    private SharedPreferences sharedPreferences;
    public SaledStocks(Context context) {
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.view_saled_stock, this);
        mTableLayout = (TableLayout) findViewById(R.id.view_saledStock_tableLayout);
        updateTableView();
    }

    public void updateTableView() {
        sharedPreferences=context.getSharedPreferences("saledStock", 0);

    }
}
