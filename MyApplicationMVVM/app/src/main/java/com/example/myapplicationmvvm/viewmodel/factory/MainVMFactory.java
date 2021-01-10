package com.example.myapplicationmvvm.viewmodel.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplicationmvvm.viewmodel.MainViewModel;
import com.example.myapplicationmvvm.viewmodel.repository.MainRepository;

public class MainVMFactory extends ViewModelProvider.NewInstanceFactory {
    private final Application mApplication;
    private final MainRepository mRepository;

    public static MainVMFactory getInstance(Application application) {
        return new MainVMFactory(application, MainRepository.mInstance);
    }

    public MainVMFactory(Application application, MainRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (MainViewModel.class.isAssignableFrom(modelClass)) {
            // noinspection unchecked
            return (T) new MainViewModel(mApplication, mRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
