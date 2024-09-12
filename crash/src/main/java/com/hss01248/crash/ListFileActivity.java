package com.hss01248.crash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * by hss
 * data:2020-04-28
 * desc:
 */
public class ListFileActivity extends Activity {

    ListView listView;
    BaseAdapter adapter;

    public static void launchCrashFiles(){
        launch(ActivityUtils.getTopActivity(), Utils.getApp().getExternalFilesDir("crashlog").getAbsolutePath());
    }

    public static void launch(Context context,String dir) {
        Intent intent = new Intent(context, ListFileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("dir",dir);
        ActivityUtils.getTopActivity().startActivityForResult(intent,99);
    }
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.crash_list_activity);

            listView = findViewById(R.id.listview);
            String path = getIntent().getStringExtra("dir");

            File[] files = new File(path).listFiles();
            //XLogUtil.obj(files);


            initListView(files);
        }catch(Exception e){
            e.printStackTrace();
        }






    }

    private void initListView(final File[] files) {
        if(files == null || files.length == 0){
            Toast.makeText(this,"没有日志",Toast.LENGTH_SHORT).show();
            return;
        }

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return files.length;
            }

            @Override
            public Object getItem(int position) {
                return files[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                ViewHolder holder = null;
                if(convertView == null){
                    convertView = new TextView(parent.getContext());
                    convertView.setPadding(40,30,20,30);
                    holder = new ViewHolder();
                    holder.textView = (TextView) convertView;
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.textView.setText(files[position].getName().replace("_i_","\n")+"    "+ files[position].length()/1024+"kB");
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextFileDisplayActivity.launch(parent.getContext(),files[position].getAbsolutePath());
                    }
                });
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    public static class ViewHolder{
        TextView textView;
    }
}
