package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.model.Location;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.ui.account.UserAccountViewModel;
import no.ntnu.mobapp20g6.app1.ui.account.UserAccountViewModelFactory;

public class NewTaskFragment extends Fragment {

    private static final int REQUEST_CODE = 747400;

    public static NewTaskFragment newInstance() {
        return new NewTaskFragment();
    }


    private NewTaskViewModel newTaskViewModel;
    private UserAccountViewModel userAccountViewModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.newTaskViewModel = new ViewModelProvider(requireActivity(), new NewTaskViewModelFactory())
                .get(NewTaskViewModel.class);

        this.userAccountViewModel = new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory())
                .get(UserAccountViewModel.class);
        this.navController = NavHostFragment.findNavController(getParentFragment());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_createtask,container,false);

        if (userAccountViewModel.isUserLoggedIn()) {
            userAccountViewModel.fetchUserFromServer();
        } else {
            navController.navigate(R.id.action_nav_createtask_to_nav_login);
        }
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        super.onViewCreated(view, savedInstanceState);

        final EditText fieldTaskTitle = view.findViewById(R.id.createtask_field_title);
        final EditText fieldTaskDescr = view.findViewById(R.id.createtask_field_description);

        final Button btnSetLocation = view.findViewById(R.id.createtask_extras_btn_location);
        final Button btnUnsetLocation = view.findViewById(R.id.createtask_extras_btn_remove_location);
        final Button btnSetPicture = view.findViewById(R.id.createtask_extras_btn_picture);
        final ImageView previewPicture = view.findViewById(R.id.createtask_extras_preview_picture);
        final Button btnUnsetPicture = view.findViewById(R.id.createtask_extras_btn_remove_picture);

        final RadioGroup radioGroupVisibility = view.findViewById(R.id.createtask_rg_visibility);
        final RadioButton radioButtonPublic = view.findViewById(R.id.createtask_rb_public);
        final RadioButton radioButtonGroup = view.findViewById(R.id.createtask_rb_group);
        final TextView messageVisibilityNoGroup = view.findViewById(R.id.createtask_visibility_nogroup_msg);

        final TextView messageSelectedDateTime = view.findViewById(R.id.createtask_display_datetime);
        final Button btnSetDate = view.findViewById(R.id.createtask_btn_date);
        final Button btnSetTime = view.findViewById(R.id.createtask_btn_time);

        final Button btnCancel = view.findViewById(R.id.createtask_cancel);
        final Button btnOk = view.findViewById(R.id.createtask_ok);

        userAccountViewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), new Observer<LoggedInUser>() {

            @Override
            public void onChanged(LoggedInUser loggedInUser) {
                if (loggedInUser.getUserGroup() == null) {
                    radioButtonGroup.setVisibility(View.GONE);
                    radioButtonPublic.toggle();
                } else {
                    messageVisibilityNoGroup.setVisibility(View.GONE);
                    radioButtonPublic.toggle();
                }
            }
        });

        newTaskViewModel.currentDateLiveData.observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                System.out.println("Data updated in date view modell");
                messageSelectedDateTime.setText("UPDADADADAD");
                SimpleDateFormat shortDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String selectedDateTxt = "Scheduled:";
                messageSelectedDateTime.setText(selectedDateTxt + " " + shortDate.format(date));
            }
        });

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(fm, "timePicker");
                }
        });

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(fm, "datePicker");
                }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    /**
     * The date picker fragment
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Get viewmodel for data storage
            NewTaskViewModel newTaskViewModel = new ViewModelProvider(requireActivity(), new NewTaskViewModelFactory())
                    .get(NewTaskViewModel.class);

            Date currentDate = newTaskViewModel.currentDateLiveData.getValue();
            if (currentDate == null) {
                currentDate = new GregorianCalendar(year,month,day).getTime();
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.set(year,month,day);
                currentDate = cal.getTime();
            }
            System.out.println("Date was set: " + currentDate.toString());
            newTaskViewModel.currentDateLiveData.setValue(currentDate);
        }
    }

    /**
     *  The time picker fragment class
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Get viewmodel for data storage
            NewTaskViewModel newTaskViewModel = new ViewModelProvider(requireActivity(), new NewTaskViewModelFactory())
                    .get(NewTaskViewModel.class);

            Date currentDate = newTaskViewModel.currentDateLiveData.getValue();
            Calendar cal = Calendar.getInstance();
            // Get the current date before writing time, if exsistent
            if (currentDate != null) {
                cal.setTime(currentDate);
            }
            cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
            cal.set(Calendar.MINUTE,minute);
            currentDate = cal.getTime();
            System.out.println("Time was set: " + currentDate.toString());
            newTaskViewModel.currentDateLiveData.setValue(currentDate);
        }
    }




}
