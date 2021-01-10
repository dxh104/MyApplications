package com.example.myapplicationmvvm.viewmodel.repository;

//数据处理（model）
public class MainRepository {
    public static MainRepository mInstance = new MainRepository();

    public Integer getdata() {
        return 11000;
    }
}
