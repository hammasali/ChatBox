package com.example.chatbox.ui;


import android.app.Dialog;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.chatbox.R;

public class AboutUsFragment extends Fragment {

    Dialog dialog;
    Button button;
    TextView aboutText;

    @Override
    public void onStart() {
        super.onStart();

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_aboutus);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

        }
        //dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.aboutusradius));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


        aboutText = dialog.findViewById(R.id.aboutUsTextView);
        aboutText.setMovementMethod(new ScrollingMovementMethod());

        dialog.show();

        button = dialog.findViewById(R.id.buttonAboutUs);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
