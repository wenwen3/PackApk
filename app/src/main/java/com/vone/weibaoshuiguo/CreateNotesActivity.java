package com.vone.weibaoshuiguo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.base.BaseRxDataActivity;
import com.vone.weibaoshuiguo.bean.Notepad;
import com.vone.weibaoshuiguo.util.PackUtils;

import java.util.Date;

public class CreateNotesActivity extends BaseRxDataActivity implements View.OnClickListener {

    private View rootView;
    private EditText editTitle;
    private EditText editMessage;
    private View save;
    private Notepad bean;

    @Override
    protected void onActivityPrepared() {
        initView();

        bean = (Notepad) getIntent().getSerializableExtra(INTENT_BEAN);

        setDataToUi();

    }

    private void setDataToUi() {
        if( bean != null ){
            if(bean.getMessage() != null ) {
                editMessage.setText(bean.getMessage());
            }
            if(bean.getTitle() != null ) {
                editTitle.setText(bean.getTitle());
            }
        }
    }

    private void initView() {
        editTitle = rootView.findViewById(R.id.editTitle);
        editMessage = rootView.findViewById(R.id.editMessage);
        save = rootView.findViewById(R.id.save);
        save.setOnClickListener(this);
        if(PackUtils.getInstance().getColor(this) != 0) {
            save.setBackgroundResource(PackUtils.getInstance().getColor(this));
        }
    }

    @Override
    protected View onCreateContentView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_create_notes_layout, null);
        return rootView;
    }

    @Override
    protected String getBarTitle() {
        return "记事本";
    }

    @Override
    protected boolean hasRightLogo() {
        return false;
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }

    public static final String INTENT_BEAN = "intent_bean";
    public static final int INTENT_REQUEST_CODE = 22;

    public static void showActivity(Activity context, Notepad notesBean){
        Intent intent = new Intent(context, CreateNotesActivity.class);
        intent.putExtra(INTENT_BEAN,notesBean);
        context.startActivityForResult(intent,INTENT_REQUEST_CODE);
    }
    @Override
    public void onClick(View v) {
        if(v == save){
            String titleString = editTitle.getText().toString().trim();
            String messageString = editMessage.getText().toString().trim();
            if(TextUtils.isEmpty(titleString) ){
                Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(messageString) ){
                messageString = "同标题";
            }
            commit(titleString,messageString);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void commit(final String titleString, final String messageString) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String success = "success";
                try {
                    if(bean != null) {
                        Notepad first = WbApplication.getInstance().getDbUtils().findFirst(Selector.from(Notepad.class).where("id", "=", bean.getId()));
                        if (first != null) {
                            bean.setMessage(messageString);
                            bean.setTitle(titleString);
                            WbApplication.getInstance().getDbUtils().update(bean, "title", "message");
                        }
                    }else{
                        bean = new Notepad();
                        bean.setDate(new Date());
                        bean.setMessage(messageString);
                        bean.setTitle(titleString);
                        WbApplication.getInstance().getDbUtils().save(bean);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                    success = null;
                }
                return success;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s == null || TextUtils.isEmpty(s)){
                    Toast.makeText(CreateNotesActivity.this, "保存失败。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("isRefresh",true);
                setResult(12,intent);
                finish();
            }
        }.execute();
    }

}
