package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class CreateGroupViewModel extends ViewModel {

    private SharedNonCacheRepository sharedRepo;
    private LoginRepository loginRepository;

    public CreateGroupViewModel(SharedNonCacheRepository sharedRepo, LoginRepository loginRepository) {
        this.sharedRepo = sharedRepo;
        this.loginRepository = loginRepository;
    }

    public void createGroup(String name, String desc, String orgID, Consumer<Group> createGroupCallBack) {
        Long orgIDnum = null;
        if (!orgID.equals("")) {
            orgIDnum = Long.parseLong(orgID);
        }
        sharedRepo.createGroup(loginRepository.getToken(), name, desc, orgIDnum, (groupResult -> {
            if (groupResult instanceof Result.Success) {
                Group createdGroup = (Group) ((Result.Success) groupResult).getData();
                createGroupCallBack.accept(createdGroup);
            } else {

            }
        }) );
    }
}