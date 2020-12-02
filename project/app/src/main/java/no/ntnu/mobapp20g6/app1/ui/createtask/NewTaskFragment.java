package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.ui.account.UserAccountViewModel;
import no.ntnu.mobapp20g6.app1.ui.account.UserAccountViewModelFactory;

import static android.app.Activity.RESULT_OK;

public class NewTaskFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 777;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Got intent in new task fragment");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (newTaskViewModel.setImageUriPathAfterCaptureIntent()) {
                System.out.println("got picture");
            } else {
                System.out.println("picture is null");
            }
        } else {
            Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            newTaskViewModel.deleteImageFileAfterCapture();
        }
    }


    /**
     *  Cleanup and delete image if taken
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        newTaskViewModel.stopGetGpsPosition();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        super.onViewCreated(view, savedInstanceState);

        final EditText fieldTaskTitle = view.findViewById(R.id.createtask_field_title);
        final EditText fieldTaskDescr = view.findViewById(R.id.createtask_field_description);
        final EditText fieldTaskParticipants = view.findViewById(R.id.createtask_field_participants);

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
        final Snackbar snackbar = Snackbar.make(view,null,Snackbar.LENGTH_LONG);

        // Init the state for the UI
        displayDateInUi(newTaskViewModel.getCurrentDateLiveData().getValue());
        newTaskViewModel.initGps(getContext(),getActivity());
        displayLocationInUiFromStateString(newTaskViewModel.getCurrentLocationSetStateLiveData().getValue());
        displayPictureInUi(newTaskViewModel.getCurrentImageBitmapUriLiveData().getValue());


        /**
         *  STATEFUL UI ELEMENTS
         */
        userAccountViewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser loggedInUser) {
                if (loggedInUser.getUserGroup() == null) {
                    radioButtonGroup.setVisibility(View.GONE);
                } else {
                    messageVisibilityNoGroup.setVisibility(View.GONE);
                }
                radioButtonPublic.toggle();
            }
        });


        newTaskViewModel.currentImageBitmapUriLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String bitmapPath) {
                displayPictureInUi(bitmapPath);
            }
        });

        newTaskViewModel.getCurrentLocationLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                System.out.println("LOCATION DATA UPDATED");
                newTaskViewModel.onGpsResultUpdateSetState(location, null);

            }
        });


        newTaskViewModel.getCurrentDateLiveData().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                System.out.println("Data updated in date view modell");
                displayDateInUi(date);
            }
        });
         //UPDATE UI WHEN GPS-STATE IS WRITTEN TO (CAN BE WRITTEN FROM GPS-LIVEDATA OR BY USER BUTTON PRESS
        newTaskViewModel.getCurrentLocationSetStateLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                System.out.println("UI LOCATION STATE UPDATED => " + s);
                displayLocationInUiFromStateString(s);

            }
        });

        /**
         *  BUTTONS
         */
        // BUTTONS LOCATION SET
        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTaskViewModel.onButtonPressRunGpsBasedOnSetState();
            }
        });
        // BUTTONS LOCATION REMOVE
        btnUnsetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTaskViewModel.removeGpsAndLiveData();
            }
        });

        // BUTTONS PIC SET
        btnSetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        // BUTTONS PIC REMOVE
        btnUnsetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTaskViewModel.currentImageBitmapUriLiveData.setValue(null);
            }
        });

        // BUTTONS DATE/TIME PICKERS
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

        /**
         *  Create a new task
         */
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement input validation as of account/user page
                AtomicBoolean errorOccurred = new AtomicBoolean(false);
                Long participants;
                btnCancel.setEnabled(false);
                btnOk.setEnabled(false);
                snackbar.setText("Please wait...");
                snackbar.show();
                if (radioButtonGroup.isChecked()) {
                    System.out.println("GROUYP TASK");
                }
                try {
                    participants = new Long(fieldTaskParticipants.getText().toString());
                } catch (NumberFormatException e) {
                    participants = null;
                }
                newTaskViewModel.createTask(fieldTaskTitle.getText().toString(),fieldTaskDescr.getText().toString(),participants,radioButtonGroup.isChecked(),(result) -> {
                    if (result instanceof Result.Success && ((Result.Success) result).getData() != null) {
                        Task createdTask = (Task) ((Result.Success) result).getData();
                        if (newTaskViewModel.isLocationSet()) {
                            newTaskViewModel.attachLocationToTask(createdTask, (addTaskResult) -> {
                                if (addTaskResult instanceof Result.Success) {
                                    if (((Result.Success) addTaskResult).getData() == null) {
                                        //we got null == error
                                        errorOccurred.set(true);
                                    } else {
                                        System.out.println("Location added OK");
                                    }
                                }
                            });
                        }

                        if (newTaskViewModel.currentImageBitmapUriLiveData.getValue() != null) {
                            //TODO: Implement in viewmodel
                            newTaskViewModel.attachImageToTask(createdTask, newTaskViewModel.currentImageBitmapUriLiveData.getValue(), (pictureResult) -> {
                                if (pictureResult instanceof Result.Success) {
                                    if (((Result.Success) pictureResult).getData() != null) {
                                        newTaskViewModel.deleteImageFileAfterCapture();
                                        System.out.println("Image added OK");
                                    } else {
                                        errorOccurred.set(true);
                                    }
                                } else {
                                    errorOccurred.set(true);
                                }
                            });
                        } else {
                            System.out.println("Image not set");
                        }
                        //navController.navigate(R.id.action_nav_account_to_nav_login);
                        if (errorOccurred.get()) {
                            snackbar.setText("Error, try again").setTextColor(Color.YELLOW);
                        } else {
                            snackbar.setText("Task created OK").setTextColor(Color.GREEN);
                        }
                        snackbar.show();
                    } else {
                        System.out.println("Failure");
                        snackbar.setText("Unable to create task").setTextColor(Color.RED);
                        snackbar.show();
                    }
                });
                btnCancel.setEnabled(true);
                btnOk.setEnabled(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldTaskParticipants.setText("");
                fieldTaskDescr.setText("");
                fieldTaskTitle.setText("");
                fieldTaskTitle.requestFocus();
                newTaskViewModel.currentDateLiveData.setValue(null);
                newTaskViewModel.deleteImageFileAfterCapture();
                newTaskViewModel.currentImageBitmapUriLiveData.setValue(null);
            }
        });
    }


    /**
     *  Prettify a date object and display it in a selected displayElement
     * @param date the date object to format and display
     */
    private void displayDateInUi(Date date) {
        TextView displayElement = getView().findViewById(R.id.createtask_display_datetime);
        String selectedDateTxt = "Scheduled:";

        if (date== null) {
            displayElement.setText("No date scheduled");
            displayElement.setTextColor(Color.GRAY);
        } else {
            SimpleDateFormat shortDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            displayElement.setText(selectedDateTxt + " " + shortDate.format(date));
        }
    }

    private void displayLocationInUiFromStateString(String state) {
        Button btnSetLocation = getView().findViewById(R.id.createtask_extras_btn_location);
        Button btnUnsetLocation = getView().findViewById(R.id.createtask_extras_btn_remove_location);
        if (state == null) {
            btnUnsetLocation.setVisibility(View.GONE);
            btnSetLocation.setVisibility(View.GONE);
        } else {
            switch (state) {
                case "ready":
                    System.out.println("ready gps");
                    btnSetLocation.setText("Set location from GPS");
                    btnSetLocation.setEnabled(true);
                    btnSetLocation.setVisibility(View.VISIBLE);
                    btnUnsetLocation.setVisibility(View.GONE);
                    break;
                case "aquire":
                    System.out.println("aquiring gps");
                    btnSetLocation.setText("Getting GPS position fix");
                    btnSetLocation.setEnabled(false);
                    newTaskViewModel.startGpsFailureTimer();
                    break;
                case "set":
                    System.out.println("setting gps");
                    btnSetLocation.setEnabled(true);
                    btnSetLocation.setVisibility(View.GONE);
                    btnUnsetLocation.setVisibility(View.VISIBLE);
                    break;
                case "denied":
                    System.out.println("denied gps");
                    btnSetLocation.setVisibility(View.VISIBLE);
                    btnUnsetLocation.setVisibility(View.GONE);
                    btnSetLocation.setText("Permission denied, try again ?");
                    break;
                case "failed":
                    System.out.println("timeout gps");
                    btnSetLocation.setEnabled(false);
                    btnSetLocation.setVisibility(View.VISIBLE);
                    btnUnsetLocation.setVisibility(View.GONE);
                    btnSetLocation.setText("Unable to get GPS, restart app");
                    break;
                case "timeout":
                    System.out.println("ready gps, failed last time");
                    btnSetLocation.setText("Failed, try again ?");
                    btnSetLocation.setEnabled(true);
                    btnSetLocation.setVisibility(View.VISIBLE);
                    btnUnsetLocation.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    }

    private void displayPictureInUi(String pictureFilePath) {
        Button btnSetPic = getView().findViewById(R.id.createtask_extras_btn_picture);
        ImageView picHolder = getView().findViewById(R.id.createtask_extras_preview_picture);
        Button btnRemovePic = getView().findViewById(R.id.createtask_extras_btn_remove_picture);
        if (pictureFilePath!= null) {
            btnSetPic.setVisibility(View.GONE);
            btnRemovePic.setVisibility(View.VISIBLE);
            Bitmap image = BitmapFactory.decodeFile(pictureFilePath);
            picHolder.setImageBitmap(image);
            picHolder.setVisibility(View.VISIBLE);
        } else {
            btnSetPic.setVisibility(View.VISIBLE);
            btnRemovePic.setVisibility(View.GONE);
            picHolder.setVisibility(View.GONE);
            picHolder.setImageBitmap(null);

        }
    }

    private void dispatchTakePictureIntent() {
        System.out.println("start picture intent");
        newTaskViewModel.startImageCaptureIntent(REQUEST_IMAGE_CAPTURE,this, getContext());
        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //try {
        //    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        //} catch (ActivityNotFoundException e) {
        //    // display error state to the user
        //}
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
            // Get viewmodel for retreiveing the stored date
            NewTaskViewModel newTaskViewModel = new ViewModelProvider(requireActivity(), new NewTaskViewModelFactory())
                    .get(NewTaskViewModel.class);

            Date currentDate = newTaskViewModel.getCurrentDateLiveData().getValue();
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

