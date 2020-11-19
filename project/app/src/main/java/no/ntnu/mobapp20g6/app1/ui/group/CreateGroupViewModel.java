package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.lifecycle.ViewModel;

import no.ntnu.mobapp20g6.app1.data.GroupDataSource;
import no.ntnu.mobapp20g6.app1.data.LocationDataSource;
import no.ntnu.mobapp20g6.app1.data.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.model.Group;

public class CreateGroupViewModel extends ViewModel {

    private SharedNonCacheRepository sharedRepo;
    private LoginRepository loginRepository;

    public CreateGroupViewModel() {
        super();
        sharedRepo = SharedNonCacheRepository.getInstance();
        loginRepository = LoginRepository.getInstance(new LoginDataSource());
    }

    public void createGroup(String name, String desc, Long orgID) {
        sharedRepo.createGroup(loginRepository.getToken(), name, desc, orgID, (groupResult -> {
            if (groupResult instanceof Result.Success) {
                Group createdGroup = (Group) ((Result.Success) groupResult).getData();
                System.out.println(createdGroup.getGroupName());
            }
        }) );
    }
    // TODO: Implement the ViewModel
}