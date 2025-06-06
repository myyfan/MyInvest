package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import x.myinvest.MainActivity;
import x.myinvest.R;

public class PopupChangeGained extends LinearLayout {
    TextView textViewGained;
    Button bnOk;
    Context context;
    public PopupChangeGained(MainActivity context,double gained) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.popup_change_gained, this);
        textViewGained = (EditText) findViewById(R.id.layout_changeGained_editText_changeGained);
        bnOk = (Button) findViewById(R.id.layout_changeGained_button_ok);
        textViewGained.setText(gained+"");
        bnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Double gained1 = Double.parseDouble(textViewGained.getText().toString());
                ((MainActivity)context).changeGained(gained1);
                context.gained=context.gained-gained+gained1;
                Toast.makeText(context, "调整历史收益成功", Toast.LENGTH_LONG).show();
           //     context.loadSavedData();
            }
        });
    }
}
