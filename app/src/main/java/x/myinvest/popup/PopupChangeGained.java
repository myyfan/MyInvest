package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import x.myinvest.MainActivity;
import x.myinvest.R;

public class PopupChangeGained extends LinearLayout {
    TextView textViewGained;
    Button bnOk;
    Context context;
    public PopupChangeGained(MainActivity context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.popup_change_gained, this);
        textViewGained = (EditText) findViewById(R.id.layout_changeGained_editText_changeGained);
        bnOk = (Button) findViewById(R.id.layout_changeGained_button_ok);
        textViewGained.setText(context.gained+"");
        bnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Double gained = Double.parseDouble(textViewGained.getText().toString());
                ((MainActivity)context).changeGained(gained);
            }
        });
    }
}
