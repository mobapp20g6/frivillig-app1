package no.ntnu.mobapp20g6.app1.ui.createtask;

import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class NewTaskViewModel extends ViewModel {

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
