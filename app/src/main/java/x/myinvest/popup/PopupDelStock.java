package x.myinvest.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import x.myinvest.MainActivity;
import x.myinvest.R;

public class PopupDelStock extends LinearLayout {
    EditText row;
    Context context;
    public  PopupDelStock(Context context){
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.popup_deletestock, this);
        row = (EditText) findViewById(R.id.layout_delStock_editText_rowNum);
        View pop = findViewById(R.id.layout_delStock_LinearLayout);
      //  pop.setOnClickListener(new OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
      //          //截断消息
      //      }
      //  });
        ((Button)findViewById(R.id.layout_delStock_button_ok)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               ((MainActivity)context).delStock(row.getText().toString());
            }
        });
    }

}
