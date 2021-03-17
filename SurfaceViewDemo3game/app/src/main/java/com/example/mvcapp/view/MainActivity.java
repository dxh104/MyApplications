package com.example.mvcapp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvcapp.R;
import com.example.mvcapp.base.BaseActivity;
import com.example.mvcapp.bean.CalendarInfo;
import com.example.mvcapp.http.ApiUtil;
import com.example.mvcapp.utils.ToastUtil;
import com.example.mvcapp.widget.GameView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.observers.DefaultObserver;

public class MainActivity extends BaseActivity {


    @BindView(R.id.tv_data)
    TextView tvData;
    private android.widget.Button btnGetData;
    private android.widget.Button btnGetData2;
    private com.example.mvcapp.widget.GameView gameView;

    @OnClick({R.id.btn_getData, R.id.btn_getData2, R.id.tv_data})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_data:
                Toast.makeText(mContext, "tvdata", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_getData:
                showLoadingDialog();//加载等待框
                //处理获取的数据
                ApiUtil.calendarDetails(mProvider, "ceshi", "1462377600", "CD78D9012F1C063E54C640EA27952F80", new DefaultObserver<CalendarInfo>() {
                    @Override
                    public void onNext(CalendarInfo calendarInfo) {
                        showCalendarInfo(calendarInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestOnError(e);
                    }

                    @Override
                    public void onComplete() {
                        hideLoadingDialog();
                    }
                });
                break;
            case R.id.btn_getData2:
                Toast.makeText(mContext, "2", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    protected int setContentLayoutRes() {
        return R.layout.activity_main;
    }

    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    AlertDialog alertDialog;

    @Override
    protected void initView() {
//        View actionBar = setActionBar("");
        gameView = (GameView) findViewById(R.id.gameView);
        gameView.setOnUpdateListener(new GameView.OnUpdateListener() {
            @Override
            public void onUpdate(final int mark, final int 当前掉落速度, final int 当前射速) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setActionBar("分数：" + mark + " 当前射速：" + decimalFormat.format(当前射速 / 1000f) + "s/个" + " 当前掉落速度：" + decimalFormat.format(当前掉落速度 / 1000f) + "s/个");
                    }
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, "你失败了");
                        if (alertDialog == null)
                            alertDialog = new AlertDialog.Builder(mContext)
                                    .setMessage("你失败了,是否重开?")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            gameView.surfaceCreated(gameView.mHolder);
                                            dialog.cancel();

                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                        }
                                    })
                                    .create();
                        alertDialog.show();

                    }
                });
            }


        });
    }

    @Override
    protected float getTitleSize() {
        return getResources().getDimensionPixelSize(R.dimen.sp_10);
    }

    @Override
    protected void initData() {

    }

    //展示日历信息
    private void showCalendarInfo(CalendarInfo calendarInfo) {
        tvData.setText(calendarInfo.toString());
    }
}
