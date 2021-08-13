package com.vone.weibaoshuiguo.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.vone.qrcode.R;

import java.util.List;

public class SpinerPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private TextView bottomView;
    private List<T> list;
    private MyAdapter mAdapter;
    private boolean isWhiteBg;

    public SpinerPopWindow(Context context, List<T> list,boolean isWhiteBg, AdapterView.OnItemClickListener clickListener) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.isWhiteBg = isWhiteBg;
        init(clickListener);
    }

    public void showBottomView(final String title){
        if(title != null && !TextUtils.isEmpty(title)) {
            bottomView.setText(title);
        }
        bottomView.setVisibility(View.VISIBLE);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastVisible;
            private int totalItemCounts;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //最后一个可见item是第一个可见item加上所有可见item（第一个可见item为0）
                lastVisible=firstVisibleItem+visibleItemCount;
                //所有item为listview的所有item
                totalItemCounts=totalItemCount;
                if (totalItemCounts == lastVisible) {
                    bottomView.setText("---没有数据咯---");
                }else{
                    bottomView.setText(title);
                }
            }
        });
    }

    private void init(AdapterView.OnItemClickListener clickListener) {
        View view = inflater.inflate(R.layout.spiner_window_layout, null);
        View allLayout = view.findViewById(R.id.layout);
        if(isWhiteBg){
            allLayout.setBackgroundResource(R.drawable.shape_white_stroke_bg);
        }else{
            allLayout.setBackgroundResource(R.drawable.shape_gray_bg);
        }
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(R.id.listview);
        bottomView = (TextView) view.findViewById(R.id.bottomView);
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(clickListener);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.spiner_item_layout, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.itemLayout = (FrameLayout) convertView.findViewById(R.id.itemLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(isWhiteBg){
                holder.itemLayout.setBackgroundResource(R.drawable.shape_white_stroke_bg);
            }else{
                holder.itemLayout.setBackgroundResource(R.drawable.shape_gray_bg);
            }
            holder.tvName.setText(getItem(position).toString());
            return convertView;
        }
    }

    private class ViewHolder {
        private TextView tvName;
        private FrameLayout itemLayout;
    }
}