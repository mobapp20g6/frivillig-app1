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

    private MutableLiveData<CreateGroupFormState> createGroupFormState = new MutableLiveData<>();
    private MutableLiveData<CreateGroupResult> createGroupResult = new MutableLiveData<>();
    private SharedNonCacheRepository sharedRepo;
    private LoginRepository loginRepository;

    public CreateGroupViewModel(SharedNonCacheRepository sharedRepo, LoginRepository loginRepository) {
        this.sharedRepo = sharedRepo;
        this.loginRepository = loginRepository;
    }

    public LiveData<CreateGroupFormState> getCreateGroupFormState() {
        return createGroupFormState;
    }

    public LiveData<CreateGroupResult> getCreateGroupResult() {
        return createGroupResult;
    }

    public void createGroup(String name, String desc, String orgID, Consumer<Group> createGroupCallBack) {
        Long orgIDnum = Long.parseLong(orgID);
        sharedRepo.createGroup(loginRepository.getToken(), name, desc, orgIDnum, (groupResult -> {
            if (groupResult instanceof Result.Success) {
                Group createdGroup = (Group) ((Result.Success) groupResult).getData();
                createGroupResult.setValue(new CreateGroupResult(createdGroup));
                createGroupCallBack.accept(createdGroup);
            } else {
                createGroupResult.setValue(new CreateGroupResult(R.string.create_group_failed_creation));
            }
        }) );
    }

    public void createGroupInputChange(String name, String desc, String orgID) {
        if (name.equals("")) {
            createGroupFormState.setValue(new CreateGroupFormState(
                    R.string.create_group_group_name_missing,
                    null,
                    null));
        } else if (desc.equals("")) {
            createGroupFormState.setValue(new CreateGroupFormState(
                    null,
                    R.string.create_group_group_desc_missing,
                    null));
        } else {
            createGroupFormState.setValue(new CreateGroupFormState(true));
        }
        /*if (orgID.toString().length() != 9) {
            createGroupFormState.setValue(new CreateGroupFormState(null, null, R.string.invalid_OrgID_Length));
        }*/
    }
}