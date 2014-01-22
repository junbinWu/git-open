package com.koudaiv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.koudaiv.adapter.CustomAdapter;
import com.koudaiv.constant.AppConstant;
import com.koudaiv.domain.DownloadInfo;
import com.koudaiv.service.DownLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private ListView mListView;
    private CustomAdapter mAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new CustomAdapter(this,getData());
        mListView.setAdapter(mAdapter);

        File file = new File(AppConstant.SD_PATH);
        if(!file.exists()) {
            file.mkdir();
        }

    }

    private List<String> getData() {
        List<String> list = new ArrayList<String>();
        list.add(AppConstant.DOWN1);
        list.add(AppConstant.DOWN2);
        list.add(AppConstant.DOWN3);
        list.add(AppConstant.DOWN4);
        return list;
    }

}
