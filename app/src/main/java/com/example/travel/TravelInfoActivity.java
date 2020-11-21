package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TravelInfoActivity extends AppCompatActivity {

    private TextView beginDate;
    private TextView endDate;
    private TextView dayCount;
    private DatePickerDialog calenderDialog;
    private DatePickerDialog endCalenderDialog;
    private ToggleButton  btnMale;
    private ToggleButton btnFemale;
    private ToggleButton btnLeisure;
    private ToggleButton btnBusiness;
    private ToggleButton btnWaterSport;
    private ToggleButton btnMountainClimb;
    private ToggleButton btnSurvival;
    private RadioGroup radioTransportGroup;
    private RadioButton radioTransport;

    private int d,m,y;
    private int ed,em,ey;
    private long days;
    private String userDestination;
    private String userGender;
    private String userTripReason;
    private String userBeginDay, userBeginMonth, userBeginYear;
    private String userEndDay, userEndMonth, userEndYear;
    private String userDay;
    private String[] userActivities= new String[3];
    private String locationImage;

    private FirebaseAuth mFirebaseAuth ;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_info);

        RelativeLayout relativeLayout= findViewById(R.id.relative_info_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();


        Intent locationIntent = getIntent();
        String locationName_string = locationIntent.getStringExtra("locationName");
        String locationImage_string = locationIntent.getStringExtra("locationImage");
        locationImage = locationImage_string;

        userDestination = locationName_string;

        ImageView locationImage = findViewById(R.id.location_imageView);
        ImageView dayImage = findViewById(R.id.day_imageView);
        ImageView transportationImage = findViewById(R.id.transportation_imageView);
        ImageView activityImage = findViewById(R.id.activity_imageView);


        btnMale = (ToggleButton) findViewById(R.id.male_toggleButton);
        btnFemale = (ToggleButton) findViewById(R.id.female_toggleButton);
        btnLeisure = (ToggleButton)findViewById(R.id.leisure_toggleButton);
        btnBusiness = (ToggleButton)findViewById(R.id.business_toggleButton);
        btnWaterSport = (ToggleButton)findViewById(R.id.waterSport_toggleButton);
        btnMountainClimb = (ToggleButton)findViewById(R.id.mountainClimb_toggleButton);
        btnSurvival = (ToggleButton)findViewById(R.id.survival_toggleButton);

        radioTransportGroup = (RadioGroup) findViewById(R.id.radioGenderGroup);

        Picasso.get().load(locationImage_string).into(locationImage);
        Picasso.get().load(locationImage_string).into(dayImage);
        Picasso.get().load(locationImage_string).into(transportationImage);
        Picasso.get().load(locationImage_string).into(activityImage);

        btnMale.setChecked(true);
        btnLeisure.setChecked(true);

        Toast.makeText(TravelInfoActivity.this, locationName_string, Toast.LENGTH_SHORT)
                .show();


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        String fd = df.format(c);
        Calendar b = Calendar.getInstance();
        d = b.get(Calendar.DAY_OF_MONTH);
        m = b.get(Calendar.MONTH);
        y = b.get(Calendar.YEAR);
        ed = b.get(Calendar.DAY_OF_MONTH);
        em = b.get(Calendar.MONTH);
        ey = b.get(Calendar.YEAR);


        beginDate = (TextView) findViewById(R.id.beginDate_textView);
        endDate = (TextView)findViewById(R.id.endDate_textView);
        dayCount = (TextView)findViewById(R.id.day_textView);

        beginDate.setText(fd);
        endDate.setText(fd);

        begin_date();
        endDateSet(d,m,y);

    //Date start
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_date();
            }
        });

        beginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                begin_date();
            }
        });
    //Date end

    //Gender start
        btnMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    btnFemale.setChecked(false);
                    userGender = "male";
                    Toast.makeText(TravelInfoActivity.this, userGender, Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    btnFemale.setChecked(true);
                }
            }
        });
        btnFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    btnMale.setChecked(false);
                    userGender = "male";
                    Toast.makeText(TravelInfoActivity.this, userGender, Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    btnMale.setChecked(true);
                }
            }
        });
    //Gender end

    //Trip Reason start
        btnBusiness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    btnLeisure.setChecked(false);
                    userTripReason = "business";
                    Toast.makeText(TravelInfoActivity.this, userTripReason, Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    btnLeisure.setChecked(true);
                }
            }
        });
        btnLeisure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    btnBusiness.setChecked(false);
                    userTripReason = "leisure";
                    Toast.makeText(TravelInfoActivity.this, userTripReason, Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    btnBusiness.setChecked(true);
                }
            }
        });
    //Trip Reason end

    //Activities start
        btnWaterSport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    userActivities[0]="waterSport";
                    //Toast.makeText(TravelInfoActivity.this, userActivities[0], Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    userActivities[0]="";
                }
            }
        });
        btnMountainClimb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    userActivities[1]="mountainClimb";
                    //Toast.makeText(TravelInfoActivity.this, userActivities[1], Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    userActivities[1]="";
                }
            }
        });
        btnSurvival.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    userActivities[2]="survival";
                    //Toast.makeText(TravelInfoActivity.this, userActivities[2], Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    userActivities[2]="";
                }
            }
        });
    //Activities end



    }

    public void previous(View view) {
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        myAlertBuilder.setTitle("Discard Changed");
        myAlertBuilder.setMessage("Do want to discard all the changes?");
        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
                        final String user_id=mFirebaseUser.getUid();
                        db = FirebaseFirestore.getInstance();
                        db.collection("PackList")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    db.collection("PackList").document(user_id).delete();
                                }
                            }
                        });

                        finish();
                    }
                });
        myAlertBuilder.setNegativeButton( "Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancel",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        myAlertBuilder.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        myAlertBuilder.setTitle("Discard Changed");
        myAlertBuilder.setMessage("Do want to discard all the changes?");
        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
                        final String user_id=mFirebaseUser.getUid();
                        db = FirebaseFirestore.getInstance();
                        db.collection("PackList")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    db.collection("PackList").document(user_id).delete();
                                }
                            }
                        });

                        finish();
                    }
                });
        myAlertBuilder.setNegativeButton( "Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancel",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        myAlertBuilder.show();
    }

    public void end_date(){
        Calendar endCalendar = Calendar.getInstance();
        int day = endCalendar.get(Calendar.DAY_OF_MONTH);
        int month = endCalendar.get(Calendar.MONTH);
        int year = endCalendar.get(Calendar.YEAR);
        endDateSet(day, month, year);
        //Begin date
        Calendar aimCalendar = Calendar.getInstance();
        aimCalendar.set(calenderDialog.getDatePicker().getYear(), calenderDialog.getDatePicker()
                .getMonth(), calenderDialog.getDatePicker().getDayOfMonth());

        long now=aimCalendar.getTimeInMillis();
        endCalenderDialog.getDatePicker().setMinDate(now);
        aimCalendar.add(Calendar.MONTH, +6);
        endCalenderDialog.getDatePicker().setMaxDate(aimCalendar.getTimeInMillis());
        endCalenderDialog.updateDate(ey, em, ed);
        endCalenderDialog.show();
    }
    public void endDateSet(int day, int month, int year){
        endCalenderDialog = new DatePickerDialog(TravelInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String mon = months(month);
                endDate.setText(dayOfMonth+"/"+mon+"/"+year);
            //Begin date
                Calendar beginCalendar = Calendar.getInstance();
                beginCalendar.set(calenderDialog.getDatePicker().getYear(), calenderDialog.getDatePicker()
                        .getMonth(), calenderDialog.getDatePicker().getDayOfMonth());

            //End date
                Calendar lastCalendar = Calendar.getInstance();
                lastCalendar.set(endCalenderDialog.getDatePicker().getYear(), endCalenderDialog.getDatePicker()
                        .getMonth(), endCalenderDialog.getDatePicker().getDayOfMonth());
                long beginM = beginCalendar.getTimeInMillis();
                long endM = lastCalendar.getTimeInMillis();
                long millis = endM-beginM;
                days = (millis / (60*60*24*1000));
                userDay = String.valueOf(days);
                dayCount.setText(String.format("%02d", days));

                ey = year;
                em = month;
                ed = dayOfMonth;
            }
        }, year, month, day);
    }

    public void begin_date(){
        Calendar mCalendar = Calendar.getInstance();
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);

        beginDateSet(day, month, year);

        calenderDialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
        mCalendar.add(Calendar.YEAR, +2);
        calenderDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis());
        calenderDialog.updateDate(y, m, d);
        calenderDialog.show();
    }

    public void beginDateSet(int day, int month, int year){
        calenderDialog = new DatePickerDialog(TravelInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String mon = months(month);
                beginDate.setText(dayOfMonth+"/"+mon+"/"+year);
                endDate.setText(dayOfMonth+"/"+mon+"/"+year);
                dayCount.setText("00");
                y = year;
                d = dayOfMonth;
                m = month;

                ey = year;
                ed = dayOfMonth;
                em = month;
            }
        }, year, month, day);
    }
    
    public String months(int month){
        String mon = null;
        switch(month+1){
            case 1:
                mon = "Jan";
                break;
            case 2:
                mon = "Feb";
                break;
            case 3:
                mon = "Mar";
                break;
            case 4:
                mon = "Apr";
                break;
            case 5:
                mon = "May";
                break;
            case 6:
                mon = "Jun";
                break;
            case 7:
                mon = "Jul";
                break;
            case 8:
                mon = "Aug";
                break;
            case 9:
                mon = "Sep";
                break;
            case 10:
                mon = "Oct";
                break;
            case 11:
                mon = "Nov";
                break;
            case 12:
                mon = "Dec";
                break;
        }
        return mon;
    }


    public void next(View view) {

        if(days == 0){
            Toast.makeText(TravelInfoActivity.this,"Please select at least 1 Day to proceed!", Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            //userDestination//
            //userBeginDay, userBeginMonth, userBeginYear
            userBeginYear = Integer.toString(calenderDialog.getDatePicker().getYear());
            userBeginMonth = Integer.toString(calenderDialog.getDatePicker().getMonth());
            userBeginDay = Integer.toString(calenderDialog.getDatePicker().getDayOfMonth());

            //userEndDay, userEndMonth, userEndYear
            userEndYear = Integer.toString(endCalenderDialog.getDatePicker().getYear());
            userEndMonth = Integer.toString(endCalenderDialog.getDatePicker().getMonth());
            userEndDay = Integer.toString(endCalenderDialog.getDatePicker().getDayOfMonth());

            //userDay//

            //userGender// userTransport// userReason//

            //userActivities
            int selectedId = radioTransportGroup.getCheckedRadioButtonId();
            int userTransport_int=0;
            radioTransport = (RadioButton) findViewById(selectedId);
            RadioButton btn1 = (RadioButton) findViewById(R.id.radioButton);
            RadioButton btn2 = (RadioButton) findViewById(R.id.radioButton2);
            RadioButton btn3 = (RadioButton) findViewById(R.id.radioButton3);

            String btn1_string = btn1.getText().toString();
            String btn2_string = btn2.getText().toString();
            String btn3_string = btn3.getText().toString();

            mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
            String userTransport = radioTransport.getText().toString();
            String transport="";
            //userTransport
            if(userTransport==btn1_string){
                userTransport_int=0;
                 transport = "plane";
            }
            else if(userTransport==btn2_string){
                userTransport_int=1;
                 transport = "train";
            }
            else if(userTransport==btn3_string){
                userTransport_int=2;
                 transport = "car_bus";
            }
            String user_email = mFirebaseUser.getEmail();
            final String user_id = mFirebaseUser.getUid();


            if(btnFemale.isChecked()){
                userGender = "female";
            }
            else{
                userGender = "male";
            }

            if(btnLeisure.isChecked()){
                userTripReason = "leisure";
            }
            else{
                userTripReason = "business";
            }

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> newItem = new HashMap<>();
            newItem.put("email",user_email);
            newItem.put("gender",userGender);
            newItem.put("reason", userTripReason);
            newItem.put("transport", transport);

            db.collection ("PackList").document(user_id)
                    .set(newItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Travel","Travel info add successful");
                        }
                    });

            db.collection(user_id)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection(user_id).document(document.getId()).delete();
                        }
                    } else {
                    }
                }
            });

            Intent intent = new Intent(TravelInfoActivity.this, PackingListActivity.class);
            intent.putExtra("userDestination",userDestination);
            intent.putExtra("userBeginDate",userBeginDay);
            intent.putExtra("userBeginMonth",userBeginMonth);
            intent.putExtra("userBeginYear",userBeginYear);
            intent.putExtra("userEndDate",userEndDay);
            intent.putExtra("userEndMonth",userEndMonth);
            intent.putExtra("userEndYear",userEndYear);
            intent.putExtra("userDay",userDay);
            intent.putExtra("userGender",userGender);
            intent.putExtra("userTransport",Integer.toString(userTransport_int));
            intent.putExtra("userTripReason",userTripReason);

            String[] uA_array = new String[0];
            if(btnWaterSport.isChecked()){
                uA_array = Arrays.copyOf(uA_array, uA_array.length+1);
                int l =uA_array.length;
                uA_array[l-1]= "waterSport";
            }
            if(btnMountainClimb.isChecked()){
                uA_array = Arrays.copyOf(uA_array, uA_array.length+1);
                int l =uA_array.length;
                uA_array[l-1]= "mountainClimb";
            }
            if(btnSurvival.isChecked()){
                uA_array = Arrays.copyOf(uA_array, uA_array.length+1);
                int l =uA_array.length;
                uA_array[l-1]= "survival";
            }
        //Activities array
            intent.putExtra("userActivities", uA_array);

        //Location
            intent.putExtra("locationImage", locationImage);
            startActivity(intent);

        }

    }

}
