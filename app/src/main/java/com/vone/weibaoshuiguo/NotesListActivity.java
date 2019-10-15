package com.vone.weibaoshuiguo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.base.BaseRxDataActivity;
import com.vone.weibaoshuiguo.bean.JsonProtocol;
import com.vone.weibaoshuiguo.bean.Notepad;
import com.vone.weibaoshuiguo.util.BroadCastUtils;
import com.vone.weibaoshuiguo.util.DateFormatUtils;
import com.vone.weibaoshuiguo.util.DialogUtils;
import com.vone.weibaoshuiguo.util.DownloadUtils;
import com.vone.weibaoshuiguo.util.PackUtils;
import com.vone.weibaoshuiguo.util.SpinerPopWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotesListActivity extends BaseRxDataActivity implements View.OnClickListener {
    private ListView listView;
    private TextView excel;
    private TextView addNotes;
    private EditText searchEdit;
    private TextView clear;
    private TextView bindDevice;
    private NotesItemAdapter nodesItemAdapter;

    private List<String> mLongClick = new ArrayList<>();

    @Override
    protected void onActivityPrepared() {
        initView();

        mLongClick.add("删除");
    }


    @Override
    protected boolean hasRightLogo() {
        return false;
    }

    private View rootView;

    @Override
    protected View onCreateContentView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_notes_layout, null);
        return rootView;
    }

    @Override
    protected String getBarTitle() {
        return "记事本";
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }

    private SwipeRefreshLayout refreshLayout;

    private void initView() {
        listView = rootView.findViewById(R.id.recyclerView);
        excel = rootView.findViewById(R.id.excel);
        addNotes = rootView.findViewById(R.id.addNotes);
        searchEdit = rootView.findViewById(R.id.searchEdit);
        clear = rootView.findViewById(R.id.clear);
        bindDevice = rootView.findViewById(R.id.bindDevice);
        if (PackUtils.getInstance().getColor(this) != 0) {
            excel.setBackgroundResource(PackUtils.getInstance().getColor(this));
        }
        if (PackUtils.getInstance().getColor(this) != 0) {
            addNotes.setBackgroundResource(PackUtils.getInstance().getColor(this));
        }
        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        excel.setOnClickListener(this);
        clear.setOnClickListener(this);
        addNotes.setOnClickListener(this);
        bindDevice.setOnClickListener(this);
        nodesItemAdapter = new NotesItemAdapter();
        listView.setAdapter(nodesItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CreateNotesActivity.showActivity(NotesListActivity.this, mDatas.get(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow(nodesItemAdapter.getItem(position));
                return true;
            }
        });
        initNotes(true);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initNotes(false);
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = searchEdit.getText().toString().trim();
                mDatas.clear();
                for (int i = 0; i < allData.size(); i++) {
                    if (allData.get(i).getTitle().contains(trim) || allData.get(i).getMessage().contains(trim)) {
                        mDatas.add(allData.get(i));
                    }
                }
                nodesItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<Notepad> allData = new ArrayList<>();

    private SpinerPopWindow stringSpinerPopWindow;

    private void showPopupWindow(final Notepad item) {
        stringSpinerPopWindow = new SpinerPopWindow<>(NotesListActivity.this, mLongClick, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stringSpinerPopWindow.dismiss();
                deleteItem(item.getId());
            }
        });
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = NotesListActivity.this.getWindow().getAttributes();
        lp.alpha = 0.7f;
        NotesListActivity.this.getWindow().setAttributes(lp);

        stringSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        stringSpinerPopWindow.setWidth(800);
        stringSpinerPopWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void deleteItem(int id) {
        try {
            WbApplication.getInstance().getDbUtils().delete(Notepad.class, WhereBuilder.b("id", "=", id));
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(true);
            initNotes(true);
        } catch (DbException e) {
            e.printStackTrace();
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showActivity(Context context) {
        Intent intent = new Intent(context, NotesListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateNotesActivity.INTENT_REQUEST_CODE) {
            if (resultCode == 12) {
                if (data != null && data.getBooleanExtra("isRefresh", false)) {
                    initNotes(true);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void initNotes(boolean isLoading) {
        mDatas.clear();
        allData.clear();
        new AsyncTask<String, String, List<Notepad>>() {
            @Override
            protected List<Notepad> doInBackground(String... strings) {
                try {
                    return WbApplication.getInstance().getDbUtils().findAll(Selector.from(Notepad.class).orderBy("date", true));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Notepad> s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (s != null && s.size() != 0) {
                    if (s.size() <= 200) {
                        mDatas.addAll(s);
                        nodesItemAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            WbApplication.getInstance().getDbUtils().deleteAll(Notepad.class);
                            Toast.makeText(NotesListActivity.this, "由于记事本数量较大，已清除", Toast.LENGTH_SHORT).show();
                        } catch (DbException e) {
                            e.printStackTrace();
                            mDatas.addAll(s);
                        }
                        nodesItemAdapter.notifyDataSetChanged();
                    }
                } else {
                    nodesItemAdapter.notifyDataSetChanged();
                }
                allData.addAll(mDatas);
            }
        }.execute();
    }

    private List<Notepad> mDatas = new ArrayList<>();
    private String host = "";
    private String key = "";
    private String yhm = "";

    @Override
    public void onClick(View v) {
        if (v == excel) {
            if (mDatas.size() == 0) {
                Toast.makeText(this, "没有数据，请先添加记事本", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
            host = read.getString(StaticInfo.HOST, "");
            key = read.getString(StaticInfo.KEY, "");
            yhm = read.getString(StaticInfo.YHM, "");

//            if(TextUtils.isEmpty(yhm) && !TextUtils.isEmpty(key)){
//                Toast.makeText(this, "配置用户名为空，请重新配置！", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(TextUtils.isEmpty(yhm) && TextUtils.isEmpty(key)){
//                Toast.makeText(this, "导出excel功能需要进行扫码配置才能生！", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (PackUtils.getInstance().getJsbjiekou() == null || TextUtils.isEmpty(PackUtils.getInstance().getJsbjiekou())) {
                Toast.makeText(this, "授权失败，无法导出excel", Toast.LENGTH_SHORT).show();
                return;
            }

            HttpUtils httpUtils = new HttpUtils();
            RequestParams requestParams = new RequestParams();
            requestParams.addBodyParameter("yhm", yhm);
            requestParams.addBodyParameter("biaoshi", PackUtils.ONLY_TAG);
            requestParams.addBodyParameter("datas", JSON.toJSONString(mDatas));
            httpUtils.send(HttpRequest.HttpMethod.POST, PackUtils.getInstance().getJsbjiekou(), requestParams, new RequestCallBack<String>() {
                @Override
                public void onStart() {
                    super.onStart();
                    DialogUtils.getInstance().showLoadingDialog(NotesListActivity.this, "正在导出excel", false);
                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    DialogUtils.getInstance().dismissLoadingDialog();
                    String result = responseInfo.result;
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.containsKey("code") && jsonObject.containsKey("url")
                            && jsonObject.get("code") != null && jsonObject.get("url") != null) {
                        int code = jsonObject.getInteger("code");
                        String url = jsonObject.getString("url");
                        String xinxi = "";
                        if (jsonObject.containsKey("xinxi")) {
                            xinxi = jsonObject.getString("xinxi");
                        }
                        if (code == 1 && url != null && !TextUtils.isEmpty(url)) {
                            WebViewActivity.showActivity(NotesListActivity.this, url);
                        } else {
                            if (xinxi != null && !TextUtils.isEmpty(xinxi)) {
                                Toast.makeText(NotesListActivity.this, xinxi, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NotesListActivity.this, "授权失败.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(NotesListActivity.this, "授权失败.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    DialogUtils.getInstance().dismissLoadingDialog();
                    Toast.makeText(NotesListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (v == clear) {
            try {
                WbApplication.getInstance().getDbUtils().deleteAll(Notepad.class);
                Toast.makeText(this, "已清空", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(true);
                initNotes(true);
            } catch (DbException e) {
                e.printStackTrace();
                Toast.makeText(this, "清空失败", Toast.LENGTH_SHORT).show();
            }
        } else if (v == addNotes) {
            CreateNotesActivity.showActivity(this, null);
        } else if (v == bindDevice) {
            final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(NotesListActivity.this).builder()
                    .setTitle("请输入口令")
                    .setEditText("");
            myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String kouling = myAlertInputDialog.getResult();
                    checkUpdate(kouling);
                    myAlertInputDialog.dismiss();
                }
            }).setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAlertInputDialog.dismiss();
                }
            });
            myAlertInputDialog.show();
        }
    }

    public void checkUpdate(String kouling) {
        HttpUtils httpUtils = new HttpUtils();
        //读入保存的配置数据并显示
        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
        final String yhmJCSJ = read.getString(StaticInfo.YHM, "");
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("version", WbApplication.getVersion(this));
        requestParams.addBodyParameter("biaoshi", PackUtils.ONLY_TAG);
        requestParams.addBodyParameter("yhm", yhmJCSJ);
        requestParams.addBodyParameter("kouling", kouling);
        requestParams.addBodyParameter("mac", PackUtils.getInstance().getMacAddress(this));
        httpUtils.send(HttpRequest.HttpMethod.POST, PackUtils.getInstance().getCHECK_NEW_APK_URL(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.containsKey("xinxi")) {
                    String shengjixinxi = jsonObject.getString("xinxi");
                    if (shengjixinxi != null && !TextUtils.isEmpty(shengjixinxi)) {
                        Toast.makeText(NotesListActivity.this, shengjixinxi, Toast.LENGTH_SHORT).show();
                    }
                }
                if (jsonObject.containsKey("jtpz")) {
                    int jtpz = jsonObject.getInteger("jtpz");
                    if (jtpz == 1) {
                        SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                        if (jsonObject.containsKey("hywz")) {
                            String hywz = jsonObject.getString("hywz");
                            editor.putString(StaticInfo.WZ, hywz);
                        }
                        if (jsonObject.containsKey("jtwz")) {
                            String jtwz = jsonObject.getString("jtwz");
                            editor.putString(StaticInfo.HOST, jtwz);
                            host = jtwz;
                        }
                        if (jsonObject.containsKey("yhm")) {
                            String yhmString = jsonObject.getString("yhm");
                            editor.putString(StaticInfo.YHM, yhmString);
                            yhm = yhmString;
                        }
                        if (jsonObject.containsKey("key")) {
                            String keyString = jsonObject.getString("key");
                            editor.putString(StaticInfo.KEY, keyString);
                            key = keyString;
                        }
                        editor.apply();
                        JsonProtocol<Object> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_REFRESH);
                        BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(NotesListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class NotesItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Notepad getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NotesListActivity.this).inflate(R.layout.notes_list_item_layout, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.title);
                holder.time = convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Notepad item = getItem(position);
            holder.title.setText(item.getTitle());
            holder.time.setText(DateFormatUtils.getInstance().formatTime(item.getDate(), true));
            return convertView;
        }

        private class ViewHolder {
            private TextView title;
            private TextView time;
        }
    }
}
