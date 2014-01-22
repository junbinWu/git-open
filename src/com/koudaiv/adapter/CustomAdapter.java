package com.koudaiv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.koudaiv.R;
import com.koudaiv.service.DownLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends BaseAdapter implements View.OnClickListener ,DownLoader.OnDownloadProgressChangedListener  {

    private List<String> urls;
    private Context context;
    private Map<String,DownLoader> loaders = new HashMap<String, DownLoader>();

    public CustomAdapter(Context context , List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item,null);
            holder = new ViewHolder();
            holder.convertView = convertView;
            holder.content = (TextView) convertView.findViewById(R.id.tv);
            holder.downloadButton = (Button) convertView.findViewById(R.id.download);
            holder.downloadButton.setOnClickListener(this);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.pb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(getItem(position));
        if(!loaders.containsKey(holder.content.getText())) {
            DownLoader loader = new DownLoader(context, (String) holder.content.getText(),holder.progressBar);
            loader.setOnDownloadProgressChangedListener(this);
            loaders.put((String) holder.content.getText(),loader);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                responseDownload(v);
                break;
        }
    }

    private void responseDownload(View v) {
        ViewGroup group = (ViewGroup) v.getParent();
        TextView tv = (TextView) group.findViewById(R.id.tv);
        Button btn = (Button) group.findViewById(R.id.download);
        ViewGroup group2 = (ViewGroup) group.getParent();
        ProgressBar pb = (ProgressBar) group2.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        DownLoader loader = loaders.get(tv.getText());
        if(loader.isDownloading()) {
            btn.setText("下载");
            loader.pause();
        } else {
            btn.setText("暂停");
            loader.download();
        }
    }

    @Override
    public void onProgressChange(ProgressBar bar, int progress) {
        bar.setProgress(progress);
    }

    @Override
    public void onFinish(ProgressBar bar) {
        ViewGroup group = (ViewGroup) bar.getParent().getParent();
        Button btn = (Button) group.findViewById(R.id.download);
        btn.setClickable(false);
        btn.setText("下载完成");
    }

    final class ViewHolder {
        View convertView;

        TextView content;
        Button downloadButton;
        ProgressBar progressBar;
    }
}
