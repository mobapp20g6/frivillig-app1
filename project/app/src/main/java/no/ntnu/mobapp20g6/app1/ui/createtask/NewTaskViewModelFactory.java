package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.internal.ContextUtils;

import no.ntnu.mobapp20g6.app1.data.ds.GroupDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.LocationDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.PictureDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.TaskDataSource;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;
import no.ntnu.mobapp20g6.app1.ui.login.LoginViewModel;

public class NewTaskViewModelFactory implements ViewModelProvider.Factory{

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewTaskViewModel.class)) {
            return (T) new NewTaskViewModel(
                    LoginRepository.getInstance(new LoginDataSource()),
                    TaskRepository.getInstance(new TaskDataSource()),
                    SharedNonCacheRepository.getInstance(
                            new GroupDataSource(),
                            new LocationDataSource(),
                            new PictureDataSource()
                    ));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
