package com.example.cz2006trial.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.DecimalDigitsInputFilter;
import com.example.cz2006trial.DownloadFileManager;
import com.example.cz2006trial.ImageDatabaseManager;
import com.example.cz2006trial.R;
import com.example.cz2006trial.controller.GoalController;
import com.example.cz2006trial.controller.UserProfileController;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

/**
 * This fragment is used to update user profile information to Firebase.
 */
public class EditProfileFragment extends Fragment {

    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CAMERA = 1;

    private ImageView profilePhoto;
    private TextView usernameTextView;
    private TextView emailTextView;
    private EditText DOBTextView;
    private EditText heightTextView;
    private EditText weightTextView;
    private EditText BMITextView;
    private ImageView updateProfileButton;
    private TextView totalDistanceTextView;
    private TextView todayTargetTextView;
    private boolean photoChange = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        profilePhoto = view.findViewById(R.id.profile);
        usernameTextView = view.findViewById(R.id.username);
        emailTextView = view.findViewById(R.id.email);
        DOBTextView = view.findViewById(R.id.DOB);
        heightTextView = view.findViewById(R.id.height);
        weightTextView = view.findViewById(R.id.weight);
        BMITextView = view.findViewById(R.id.BMI);
        updateProfileButton = view.findViewById(R.id.updateProfileButton);
        totalDistanceTextView = view.findViewById(R.id.totalDistance);
        todayTargetTextView = view.findViewById(R.id.todayTarget);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
        sdf.setTimeZone(tz);
        java.util.Date curDate = new java.util.Date();
        String dateStr = sdf.format(curDate);
        Date date = GoalController.convertStringToDate(dateStr);

        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime(date);

        final DatePickerDialog.OnDateSetListener dob = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                DOBTextView.setText(sdf.format(myCalendar.getTime()));
            }

        };

        DOBTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dob, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });


        heightTextView.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        weightTextView.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        BMITextView.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission()) {
                    requestPermission();
                    editPhoto();
                }
                else
                    requestPermission();
            }
        });

        displayProfile();

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                if (photoChange && hasPhoto(profilePhoto)) {
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message) {
                            //DownloadFileManager.downloadFile(getContext(), "profilePhoto", ".jpg", Environment.DIRECTORY_DOWNLOADS, message[0]);
                            //Toast.makeText(getContext(), message[0], Toast.LENGTH_SHORT).show();
                        }
                    }, "update", profilePhoto);
                    Toast.makeText(getContext(), "User Profile Updated", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                } else if (photoChange && !hasPhoto(profilePhoto)) {
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message) {
                            //Toast.makeText(getContext(), message[0], Toast.LENGTH_SHORT).show();
                        }
                    }, "delete", profilePhoto);
                    Toast.makeText(getContext(), "User Profile Updated", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "User Profile Updated", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 225:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted, Now you can edit profile photo.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Permission Denied, You cannot edit profile photo.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void updateProfile() {
        String username = usernameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String DOB = null;
        double height = 0;
        double weight = 0;
        double BMI = 0;

        if (!DOBTextView.getEditableText().toString().equals(""))
            DOB = DOBTextView.getEditableText().toString();

        if (!heightTextView.getEditableText().toString().equals(""))
            height = Double.parseDouble(heightTextView.getEditableText().toString());

        if (!weightTextView.getEditableText().toString().equals(""))
            weight = Double.parseDouble(weightTextView.getEditableText().toString());

        if (!BMITextView.getEditableText().toString().equals(""))
            BMI = Double.parseDouble(BMITextView.getEditableText().toString());

        DatabaseManager.updateProfileData(username, email, DOB, height, weight, BMI);
    }

    public void displayProfile() {
        DatabaseManager.getProfileData(new DatabaseManager.ProfileDatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg) {
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else if (errorMsg[1] != null)
                    Toast.makeText(getContext(), errorMsg[1], Toast.LENGTH_LONG).show();
                else {
                    usernameTextView.setText(stringArgs.get(0));
                    emailTextView.setText(stringArgs.get(1));
                    usernameTextView.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.VISIBLE);
                    if (stringArgs.get(2) != null)
                        DOBTextView.setText(stringArgs.get(2));
                    DOBTextView.setHint("Please input your date of birth");
                    if (doubleArgs[0] != 0)
                        heightTextView.setText("" + doubleArgs[0]);
                    heightTextView.setHint("Please input your height in cm");
                    if (doubleArgs[1] != 0)
                        weightTextView.setText("" + doubleArgs[1]);
                    weightTextView.setHint("Please input your weight in kg");
                    if (doubleArgs[2] != 0)
                        BMITextView.setText("" + doubleArgs[2]);
                    BMITextView.setHint("Please input your BMI");
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message) {
                            Toast.makeText(getContext(), message[0], Toast.LENGTH_SHORT).show();
                        }
                    }, "retrieve", profilePhoto);
                    DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
                        @Override
                        public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                            double totalDistance = 0.0;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                            TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
                            sdf.setTimeZone(tz);
                            java.util.Date curDate = new java.util.Date();
                            String date = sdf.format(curDate);
                            for (Goal goal : goals) {
                                totalDistance += goal.getDistance();
                                if (goal.getDate().equals(date)) {
                                    if (goal.getTarget() > 0)
                                        todayTargetTextView.setText(Math.round(goal.getDistance() * 10) / 10.0 + "/" + goal.getTarget() + " km");
                                }

                            }
                            totalDistanceTextView.setText(Math.round(totalDistance * 10) / 10.0 + " km");
                        }
                    }, null);
                }
            }
        });
    }

    /**
     * This function checks if the image resource currently attached to an ImageView is the same as
     * that set in the XML.
     *
     * @param profilePhoto The ImageView whose contents are to be checked
     * @return returns true if the image resource inside the ImageView was replaced with another;
     * else returns false
     */
    private boolean hasPhoto(ImageView profilePhoto) {
        boolean result = true;
        Drawable.ConstantState constantState;
        constantState = getResources()
                .getDrawable(R.drawable.ic_profile_pic, getContext().getTheme())
                .getConstantState();

        if (profilePhoto.getDrawable().getConstantState() == constantState) {
            result = false;
        }

        return result;
    }


    public void editPhoto() {

        //The list of edit photo options. Have to use hardcoded string since this is a
        //CharSequence[] and R.string values can't be used since they are int indexes
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Remove Photo"};

        //creating the alert dialog to show when the edit photo button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning: Any changes will delete current photo permanently when updating");
        builder.setItems(
                //if the profile photo is set, show the option to remove the photo
                //else show only the first two options.
                (hasPhoto(this.profilePhoto) ? items : Arrays.copyOfRange(items, 0, 2)),
                new DialogInterface.OnClickListener() {

                    /**
                     * OnClick handler for each of the menu items
                     * @param dialog the menu from which the user selected an item
                     * @param item the menu item that the user clicked on
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"),
                                    SELECT_FILE);
                        } else if (items[item].equals("Remove Photo")) {
                            profilePhoto.setImageResource(R.drawable.ic_profile_pic);
                            photoChange = true;
                            dialog.dismiss(); //dismiss the dialog when an option is selected
                        }
                    }
                });
        builder.show(); //finally, show this dialog upon button click
    }

    /**
     * The function that is called when the user returns to EditProfile after having selected a
     * file or taken a photo from the camera app.
     *
     * @param requestCode To determine if the user has returned from the camera app or selected a file
     * @param resultCode  success or error
     * @param data        data received from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**if a bitmap was not fetched successfully, there's no point in attempting to
         * parse the data.
         */
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                //if the user took a photo using the camera, save that file to external storage and
                //set that image to the profilePhoto ImageView
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (thumbnail != null) {
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    /*File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    profilePhoto.setImageBitmap(thumbnail);
                    photoChange = true;
                } else {
                    Toast.makeText(getContext(), "Image capture error", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == SELECT_FILE) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(getContext(), selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 120;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    profilePhoto.setImageBitmap(bm);
                    photoChange = true;
                }
            }
        }
    }
}
