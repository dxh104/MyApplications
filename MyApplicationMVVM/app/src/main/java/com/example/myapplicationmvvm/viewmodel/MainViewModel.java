package com.example.myapplicationmvvm.viewmodel;

import android.app.Application;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.example.myapplicationmvvm.viewmodel.repository.MainRepository;

//逻辑控制(pst)
public class MainViewModel extends AndroidViewModel {
    MainRepository mainRepository;
    MediatorLiveData<Integer> mediatorLiveData = new MediatorLiveData();

    public MainViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application);
        this.mainRepository = mainRepository;
    }

    public MainRepository getMainRepository() {
        return mainRepository;
    }

    public MediatorLiveData<Integer> getMediatorLiveData() {
        return mediatorLiveData;
    }

    public void post(Integer integer) {
        Integer data = mainRepository.getdata();
        mediatorLiveData.postValue(integer + data);
    }

    public void setValue(Integer integer) {
        Integer data = mainRepository.getdata();
        mediatorLiveData.setValue(integer + data);
    }

    public Integer getValue() {
        return mediatorLiveData.getValue();
    }


}
