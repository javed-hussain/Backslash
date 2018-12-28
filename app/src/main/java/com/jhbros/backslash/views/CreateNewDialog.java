package com.jhbros.backslash.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jhbros.backslash.R;

import androidx.annotation.NonNull;


/*
 * Created by javed on 12/29/2018
 */

public class CreateNewDialog extends Dialog {
    private Button yes;
    private Button no;
    private EditText text;
    private boolean type;
    private TextView title, subtitle;

    public CreateNewDialog(@NonNull Context context, boolean type) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_file_dialog);
        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        text = findViewById(R.id.new_folder_name);
        title = findViewById(R.id.dialog_title);
        subtitle = findViewById(R.id.subtitle);
        if (type) {
            title.setText("Create a file");
            subtitle.setText("Please provide a new file name");
        } else {
            title.setText("Create a folder");
            subtitle.setText("Please provide a new folder name");
        }
    }

    public void attachCreateListner(View.OnClickListener listener) {
        yes.setOnClickListener(listener);
    }

    public void attachCancelListner(View.OnClickListener listener) {
        no.setOnClickListener(listener);
    }

    public String getText() {
        return text.getText().toString();
    }
}
