package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import java.util.Date;

import no.ntnu.mobapp20g6.app1.data.model.Location;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class NewTaskViewModel extends ViewModel {

    public MutableLiveData<Location> currentLocationLiveData = new MutableLiveData<>();
    public MutableLiveData<Bitmap> currentImageBitmapLiveData = new MutableLiveData<>();
    public MutableLiveData<Date> currentDateLiveData = new MutableLiveData<>();

    LoginRepository loginRepository;
    TaskRepository taskRepository;

    public NewTaskViewModel(LoginRepository loginRepository, TaskRepository taskRepository) {
        this.loginRepository = loginRepository;
        this.taskRepository = taskRepository;
    }


    public Boolean isLoggedin() {
        return loginRepository.isLoggedIn();
    }



}
