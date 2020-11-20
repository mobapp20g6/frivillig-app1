package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import no.ntnu.mobapp20g6.app1.data.GroupDataSource;
import no.ntnu.mobapp20g6.app1.data.LocationDataSource;
import no.ntnu.mobapp20g6.app1.data.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.SharedNonCacheRepository;

public class CreateGroupViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
       if (modelClass.isAssignableFrom(CreateGroupViewModel.class)) {
         return (T) new CreateGroupViewModel(
                 SharedNonCacheRepository.getInstance(new GroupDataSource(),
                                                      new LocationDataSource()),
                 LoginRepository.getInstance(new LoginDataSource()));
       } else {
           throw new IllegalArgumentException("Unknown ViewModel class");
       }
    }
}
