package com.example.chatbox.ui;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class ShareFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();

        ApplicationInfo api = getContext().getApplicationInfo();
        String apkPatch = api.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(apkPatch));
        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPatch)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(Intent.createChooser(intent, "ShareVia"));
    }

}
