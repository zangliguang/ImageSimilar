package com.liguang.imagesimilar;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonicartos.superslim.LayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import adapter.SimilarPictureAdapter;
import tool.ImageHelper;
import tool.LocalImage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="SIMILAR_IMAGE";
    private static final int PROGRESS_END =0;
    private static final int PROGRESS_START =1;
    private static final int PROGRESS_PROGRESS =2;
    RecyclerView mRecyclerView;
    LinearLayout mParentPanel;
    ProgressBar mPb;
    TextView mImageProgress;
    List<Set<LocalImage>> similarItems =new ArrayList<>();

    private ViewHolder mViews;

    private SimilarPictureAdapter mAdapter;

    private int mHeaderDisplay;

    private boolean mAreMarginsFixed;


    Handler mHandler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             switch (msg.what){
                 case PROGRESS_END:
                     mPb.setVisibility(View.GONE);
                     mImageProgress.setVisibility(View.GONE);
                     mAdapter = new SimilarPictureAdapter(MainActivity.this.getApplicationContext(), 18,similarItems);
                     mAdapter.setMarginsFixed(true);
                     mAdapter.setHeaderDisplay(18);
                     mViews.setAdapter(mAdapter);
                 case PROGRESS_START:
                     mPb.setMax(msg.arg1);
                 case PROGRESS_PROGRESS:
                     mPb.setProgress(msg.arg1);
                     mImageProgress.setText("解析图片"+msg.arg1+"/"+mPb.getMax());


             }


             super.handleMessage(msg);
         }
     };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mParentPanel = (LinearLayout) this.findViewById(R.id.parentPanel);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.similar_imagelist);
        mImageProgress = (TextView) this.findViewById(R.id.image_progress);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPb = (ProgressBar) this.findViewById(R.id.pb);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPath();
                Message msg = mHandler.obtainMessage();//同 new Message();
                msg.what=PROGRESS_END;
                mHandler.sendMessage(msg);
            }
        }).start();

        mViews = new ViewHolder(mRecyclerView);
        mViews.initViews(new LayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getPath() {
        List<LocalImage>imageList=new ArrayList<>();
        Uri uri = MediaStore.Images.Media.getContentUri("external");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c == null) {
            Log.e(TAG, "fail to query uri:" + uri);
        }
        c.moveToFirst();
        int count=c.getCount();
        Message msg = mHandler.obtainMessage();//同 new Message();
        msg.arg1 = count;
        msg.what=PROGRESS_START;
        mHandler.sendMessage(msg);
        int analyze_num=0;
        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            long size = c.getLong(c.getColumnIndex(MediaStore.MediaColumns.SIZE));
            int ringtoneID = c.getInt(c
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri
                        .parse(uri.toString() +"/"+ ringtoneID));
                if (null!=bitmap){
                    imageList.add(new LocalImage(ImageHelper.produceFingerPrint(bitmap), path,size,(uri.toString() +"/"+ ringtoneID)));
                }

                if(bitmap != null && !bitmap.isRecycled()){

                    // 回收并且置为null

                    bitmap.recycle();

                    bitmap = null;
                    System.gc();
                }
                analyze_num++;
                Log.e(TAG, "加载图片" + path);
                int  progress = (int) ((double) analyze_num / count) * 100;
                mPb.setProgress(progress);
                Message msg2 = mHandler.obtainMessage();//同 new Message();
                msg2.arg1 = analyze_num;
                msg2.what = PROGRESS_PROGRESS;
                mHandler.sendMessage(msg2);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Collections.sort(imageList, new Comparator<LocalImage>() {

            @Override
            public int compare(LocalImage localImage1, LocalImage localImage2) {
                return localImage1.getSourceHashCode().compareTo(localImage2.getSourceHashCode());
            }
        });
        Set<LocalImage> similarItem =new ArraySet<>();
        int index =0;
        for(int i=0;i<imageList.size()-1;i++){
            LocalImage first = imageList.get(i);
            LocalImage second = imageList.get(i + 1);
            similarItem.add(first);
            if(ImageHelper.hammingDistance(imageList.get(i - similarItem.size()+1).getSourceHashCode(), second.getSourceHashCode())<=5){
                similarItem.add(second);
                if(i==imageList.size()-2){
                    similarItems.add(similarItem);

                    Log.e(TAG, "相似结束" + similarItem.size());
                }
            }else {
                if (similarItem.size() > 1) {
                    similarItems.add(similarItem);
                    Log.e(TAG, "相似结束" + similarItem.size());
                }
                similarItem=new ArraySet<>();
            }

        }

    }



    private static class ViewHolder {

        private final RecyclerView mRecyclerHolderView;


        public ViewHolder(RecyclerView view) {
            mRecyclerHolderView = view;
        }

        public void initViews(LayoutManager lm) {
            mRecyclerHolderView.setLayoutManager(lm);
        }

        public void scrollToPosition(int position) {
            mRecyclerHolderView.scrollToPosition(position);
        }

        public void setAdapter(RecyclerView.Adapter<?> adapter) {
            mRecyclerHolderView.setAdapter(adapter);
        }

        public void smoothScrollToPosition(int position) {
            mRecyclerHolderView.smoothScrollToPosition(position);
        }
    }

}
