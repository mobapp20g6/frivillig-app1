package no.ntnu.mobapp20g6.app1.ui.group.display;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import no.ntnu.mobapp20g6.app1.data.ds.GroupDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.LocationDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.PictureDataSource;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;

public class DisplayGroupViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DisplayGroupViewModel.class)) {
            return (T) new DisplayGroupViewModel(
                    SharedNonCacheRepository.getInstance(new GroupDataSource(), new LocationDataSource(), new PictureDataSource()),
                    LoginRepository.getInstance(new LoginDataSource()));
        } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
