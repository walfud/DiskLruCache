package com.walfud.disklrucachedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private RecyclerView mRv;
    private List<Bitmap> mDataList;

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
                holder.keyTv.setText(String.format("%dKB", bitmap.getByteCount() / 1024));
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
