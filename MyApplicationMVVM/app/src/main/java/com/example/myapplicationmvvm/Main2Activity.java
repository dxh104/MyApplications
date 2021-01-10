package com.example.myapplicationmvvm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplicationmvvm.viewmodel.MainViewModel;
import com.example.myapplicationmvvm.viewmodel.factory.MainVMFactory;

public class Main2Activity extends AppCompatActivity {

    private TextView tvData;
    private Button btnGetData;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        mainViewModel = ViewModelProviders.of(this, MainVMFactory.getInstance(getApplication())).get(MainViewModel.class);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.post(000);
            }
        });
        mainViewModel.getMediatorLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tvData.setText("数据:" + integer);
            }
        });
    }

    private void initView() {
        tvData = (TextView) findViewById(R.id.tv_data);
        btnGetData = (Button) findViewById(R.id.btn_getData);
    }
}
