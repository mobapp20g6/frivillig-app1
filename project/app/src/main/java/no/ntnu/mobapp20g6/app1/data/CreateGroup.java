package no.ntnu.mobapp20g6.app1.data;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.ntnu.mobapp20g6.app1.R;

public class CreateGroup extends Fragment {

    private CreateGroupViewModel mViewModel;

    public static CreateGroup newInstance() {
        return new CreateGroup();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_group_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateGroupViewModel.class);
        // TODO: Use the ViewModel
    }

}