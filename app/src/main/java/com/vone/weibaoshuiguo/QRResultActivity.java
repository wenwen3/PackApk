package com.vone.weibaoshuiguo;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.base.BaseRxDataActivity;
import com.vone.weibaoshuiguo.util.PackUtils;

/**
 * webView activity
 **/
public class QRResultActivity extends BaseRxDataActivity {

    public static final String INTENT_QR_RESULT = "intent_qr_result";

    private View rootView;
    private TextView title;
    private TextView copy;
    private String result;

    @Override
    protected void onActivityPrepared() {
        title = rootView.findViewById(R.id.title);
        copy = rootView.findViewById(R.id.copy);
        if(PackUtils.getInstance().getColor(this) != 0) {
            copy.setBackgroundResource(PackUtils.getInstance().getColor(this));
        }
        Intent intent = getIntent();
        result = intent.getStringExtra(INTENT_QR_RESULT);
        title.setText("扫描结果 : \n"+"             "+result);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从API11开始android推荐使用android.content.ClipboardManager
// 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
// 将文本内容放到系统剪贴板里。
                cm.setText(result);
                Toast.makeText(QRResultActivity.this, "复制成功!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected View onCreateContentView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.qr_result_layout, null);
        return rootView;
    }

    @Override
    protected String getBarTitle() {
        return "扫描结果";
    }

    @Override
    protected boolean hasRightLogo() {
        return false;
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }

    public static void showActivity(Context context,String result){
        Intent intent = new Intent(context, QRResultActivity.class);
        intent.putExtra(INTENT_QR_RESULT,result);
        context.startActivity(intent);
    }
}
