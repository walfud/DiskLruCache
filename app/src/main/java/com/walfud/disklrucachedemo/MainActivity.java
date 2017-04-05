package com.walfud.disklrucachedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walfud.disklrucache.DiskLruCache;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static final String TAG = "DiskLruCacheDemo";

    private RecyclerView mRv;
    private List<Bitmap> mDataList;
    private DiskLruCache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRv.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_rv, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Bitmap bitmap = mDataList.get(position);
                holder.iv.setImageBitmap(bitmap);

                try {
                    DiskLruCache.Editor editor = mCache.edit(String.valueOf(position));
                    OutputStream outputStream = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    editor.commit();

                    holder.keyTv.setText(String.format("%dKB", mCache.get(String.valueOf(position)).getLength(0) / 1024));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public int getItemCount() {
                return mDataList.size();
            }
        });

        // Initialize data
        try {
            mDataList = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                mDataList.add(BitmapFactory.decodeStream(getResources().getAssets().open(String.format("%d.jpg", i))));
            }
            mRv.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        try {
            mCache = DiskLruCache.open(getExternalCacheDir(), BuildConfig.VERSION_CODE, 1, 1L * 1024 * 1024);   // Cache about 23 pictures
            mCache.setOnEventListener(new DiskLruCache.OnEventListener() {
                @Override
                public void onAddCache(String key) {
                    Log.i(TAG, "onAddCache: " + key);
                }

                @Override
                public void onRemoveCache(String key) {
                    Log.i(TAG, "onRemoveCache: " + key);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView keyTv;

        public ViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            keyTv = (TextView) itemView.findViewById(R.id.tv_key);
        }
    }
}
