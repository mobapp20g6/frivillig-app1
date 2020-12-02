package no.ntnu.mobapp20g6.app1.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private LoginRepository loginRepository;


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}