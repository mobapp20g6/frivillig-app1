package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    public void getBrregOrg(String groupOrgId, Consumer<JsonObject> brregCallBack) {
        sharedRepo.getVoluntaryBrregOrg(groupOrgId, brregResult -> {
            if (brregResult instanceof Result.Success) {
                JsonObject voluntaryOrg = (JsonObject) ((Result.Success) brregResult).getData();
                brregCallBack.accept(voluntaryOrg);
            }
        });
    }
}