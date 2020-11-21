package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel.Model.Location;
import com.example.travel.Model.Packlist;
import com.example.travel.ViewHolder.LocationViewHolder;
import com.example.travel.ViewHolder.PacklistViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PackingListActivity extends AppCompatActivity {

    private  static final int STORAGE_CODE = 1000;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 16);

//Firebase
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db;
    List<DocumentSnapshot>myListOfDocuments;
    //live db
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Travel Info
    int save;
    String user_email, user_id;
    String userImage;
    String userDestination;
    String userBeginDate, userBeginMonth, userBeginYear;
    String userEndDate, userEndMonth, userEndYear;
    String userDay, userGender, userTransport,userTripReason;
    String[] userActivities;
    int[] array_day;

    ImageButton btnPdf;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_list);



        RelativeLayout relativeLayout= findViewById(R.id.relative_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        get_user();
        travelInfo();

        ImageView background = findViewById(R.id.user_imageView);
        TextView destination = findViewById(R.id.destination_textView);

        btnPdf = findViewById(R.id.pdf_imageButton);

        destination.setText(userDestination);
        Picasso.get().load(userImage).into(background);

        recyclerView = findViewById(R.id.recycler_item);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_CODE);
                    }
                    else{
                        savePdf();
                    }
                }
                else{
                    savePdf();
                }
            }
        });

    }

    private void savePdf() {
            db.collection(user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        Document mDoc = new Document();

                        final String mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(System.currentTimeMillis());
                        String mFilePath = Environment.getExternalStorageDirectory() +"/"+Environment.DIRECTORY_DOWNLOADS+"/"+"gsuzim"+mFileName+".pdf";

                        try{
                            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
                            mDoc.open();

                            mDoc.addAuthor("GSUZIM Team");

                            mDoc.add(new Paragraph("Report generated by: "+mFileName));
                            mDoc.add(new Paragraph(user_email, subFont));
                            mDoc.add(new Paragraph(" "));
                            mDoc.add(new Paragraph(userDestination+"\n",catFont));
                            mDoc.add(new Paragraph(userBeginDate+"/"+userBeginMonth+"/"+userBeginYear+
                                    " - "+userEndDate+"/"+userEndMonth+"/"+userEndYear+" / "+userDay+" DAY",catFont));
                            mDoc.add(new Paragraph(userGender.toUpperCase()+"/"+userTripReason.toUpperCase(),catFont));
                            final String transport;
                            if(userTransport.equals("0")){
                                transport = "plane";
                            }
                            else if(userTransport.equals("1")){
                                transport = "train";
                            }
                            else{
                                transport = "car/bus";
                            }
                            mDoc.add(new Paragraph(transport.toUpperCase(),catFont));
                            mDoc.add(new Paragraph(" "));
                            mDoc.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
                            mDoc.add(new Paragraph(" "));

                            boolean activityLength = false;

                            if(userActivities.length>0){
                                for(int a=0; a<userActivities.length; a++){
                                    if(userActivities[a]!= null){
                                        if(userActivities[a].equals("mountainClimb")){
                                            mDoc.add(new Paragraph("MOUNTAIN CLIMB",subFont));
                                        }
                                        if(userActivities[a].equals("survival")){
                                            mDoc.add(new Paragraph("SURVIVAL",subFont));
                                        }
                                        if(userActivities[a].equals("waterSport")){
                                            mDoc.add(new Paragraph("WATER SPORT",subFont));
                                        }
                                        activityLength = true;
                                    }
                                }

                            }
                            if(activityLength){
                                mDoc.add(new Paragraph(" ",catFont));
                                mDoc.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
                                mDoc.add(new Paragraph(" ",catFont));
                            }

                            boolean overQty = false;
                            int numberOfItem =1;
                            for(QueryDocumentSnapshot doc: task.getResult()){
                            String item = doc.getString("item");
                            int qty = doc.getLong("qty").intValue();
                            String quantity;

                            if(qty>=15){
                                quantity="x15";
                                overQty = true;
                            }
                            else if(qty==0){
                                quantity=" ";
                            }
                            else{
                                quantity = "x"+ Integer.toString(qty);
                            }

                            mDoc.add(new Paragraph(numberOfItem+")  "+item+" "+quantity,subFont));
                            numberOfItem++;
                           }

                            mDoc.add(new Paragraph(" "));
                            Paragraph paragraph = new Paragraph("----------------------------------------------------------------------------------------------------------------------------------");
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            mDoc.add(paragraph);
                            if(overQty){
                                mDoc.add(new Paragraph("Remark:"));
                                mDoc.add(new Paragraph("Your travel period are more than 15 day, laundry is needed in your trips."));
                            }

                            mDoc.add(new Paragraph(" "));
                            mDoc.add(new Paragraph(" "));
                            paragraph = new Paragraph("from LUGGAGE PACKING GUIDE Team");
                            paragraph.setAlignment(Element.ALIGN_BOTTOM);
                            mDoc.add(paragraph);
                            mDoc.close();

                            db.collection("Users").document(user_id).collection("History")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int hisId=0;
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot hisDoc : task.getResult()){
                                            hisId = Integer.parseInt(hisDoc.getId());
                                        }
                                        hisId = hisId+1;
                                        Map<String,Object> newRecord = new HashMap<>();
                                        newRecord.put("FileName", mFileName);
                                        newRecord.put("Destination", userDestination);
                                        newRecord.put("Period", userBeginDate+"/"+userBeginMonth+"/"+userBeginYear+
                                                " - "+userEndDate+"/"+userEndMonth+"/"+userEndYear);
                                        newRecord.put("Day", userDay);
                                        newRecord.put("Gender", userGender);
                                        newRecord.put("TripReason", userTripReason);
                                        newRecord.put("Transportation", transport);

                                        if(userActivities.length>0){
                                            for(int a=0; a<userActivities.length; a++){
                                                if(userActivities[a]!= null){
                                                    if(userActivities[a].equals("mountainClimb")){
                                                        newRecord.put("Activity_"+a, "MOUNTAIN CLIMB");
                                                    }
                                                    if(userActivities[a].equals("survival")){
                                                        newRecord.put("Activity_"+a, "SURVIVAL");
                                                    }
                                                    if(userActivities[a].equals("waterSport")){
                                                        newRecord.put("Activity_"+a, "WATER SPORT");
                                                    }
                                                }
                                            }

                                        }

                                        db.collection("Users").document(user_id).collection("History").document(Integer.toString(hisId))
                                                .set(newRecord)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                    }
                                }
                            });
                            save++;

                            Toast.makeText(PackingListActivity.this, mFileName+".pdf\n is saved to\n"+mFilePath, Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e){
                            Toast.makeText(PackingListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:
            {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePdf();
                }
                else{
                    Toast.makeText(PackingListActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void travelInfo(){

        Intent travelInfoIntent = getIntent();

        userDestination = travelInfoIntent.getStringExtra("userDestination");
        userImage = travelInfoIntent.getStringExtra("locationImage");

        userBeginDate = travelInfoIntent.getStringExtra("userBeginDate");
        userBeginMonth = travelInfoIntent.getStringExtra("userBeginMonth");
        userBeginYear = travelInfoIntent.getStringExtra("userBeginYear");

        userEndDate = travelInfoIntent.getStringExtra("userEndDate");
        userEndMonth = travelInfoIntent.getStringExtra("userEndMonth");
        userEndYear = travelInfoIntent.getStringExtra("userEndYear");

        userDay = travelInfoIntent.getStringExtra("userDay");

        userGender = travelInfoIntent.getStringExtra("userGender");


        userTransport = travelInfoIntent.getStringExtra("userTransport");

        userTripReason = travelInfoIntent.getStringExtra("userTripReason");

        userActivities = travelInfoIntent.getStringArrayExtra("userActivities");


    }

    public void set_weather(){
        boolean sameMonth= false;

        //beginDate
        int beginD = Integer.parseInt(userBeginDate);
        int beginM = Integer.parseInt(userBeginMonth);
        int beginY = Integer.parseInt(userBeginYear);
        //endDate
        int endD = Integer.parseInt(userEndDate);
        int endM = Integer.parseInt(userEndMonth);
        //Day

        //validate user month
        if(beginM == endM) {
            sameMonth = true;
            int betD = endD-beginD;
            array_day = new int[1];
            array_day[0] = betD;

        }
        if(!sameMonth) {

            Calendar cal = Calendar.getInstance();
            cal.set(beginY, beginM, beginD);
            int betM;

            //normal
            if (beginM < endM) {
                betM = endM - beginM + 1;
            } else {//begin>end
                betM = beginM - endM + 1;
            }
            array_day = new int[betM];

            for (int countM = 0; countM < betM; countM++) {
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                int tempM = cal.get(Calendar.MONTH);
                if (beginM == tempM) {
                    array_day[countM] = maxDay - beginD;
                } else if (endM == tempM) {

                    array_day[countM] = endD;

                } else {
                    array_day[countM] = maxDay;
                }
                cal.add(Calendar.MONTH, +1);
            }

        }
        String month = userBeginMonth;
        String des = userDestination.toLowerCase();


        int temM = Integer.parseInt(month);
        final int weatherCount=array_day.length;
        month = Integer.toString(temM);
        Log.i("Month", month);

        this.db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("Weather").document(des).collection("month").document(month);
        final String finalMonth = month;
        db.collection("Weather").document(des).collection("month")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String monthly = finalMonth;
                    String monthId;
                    String[] weather= new String[1];
                    int[] qty = new int[1];
                    int i=0;
                    String tempW;
                    int m = Integer.parseInt(monthly);
                    for(int w=weatherCount; w>0;w--){
                        Log.i("Month"+w, monthly);
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            monthId = doc.getId();
                            if(monthId.equals(monthly)){
                                if(i>0){
                                    tempW = doc.getString("weather_cat");

                                    if(tempW.equals(weather[i-1])){
                                        int tem = i-1;
                                        qty[tem]=qty[tem]+array_day[i];
                                        Log.i("Qty", Integer.toString(qty[tem]));
                                    }
                                    else{
                                        int z=weather.length +1;
                                        weather = Arrays.copyOf(weather, z);
                                        qty = Arrays.copyOf(qty,z);
                                        weather[i] = tempW;
                                        qty[i] = array_day[i];
                                        Log.i("Month", monthly);
                                        Log.i("Weather", weather[i]);
                                        Log.i("Qty", Integer.toString(qty[i]));
                                        i++;
                                    }
                                }
                                else{
                                    weather[i] = doc.getString("weather_cat");
                                    qty[i] = array_day[i];
                                    Log.i("Month", monthly);
                                    Log.i("Weather", weather[i]);
                                    Log.i("Qty", Integer.toString(qty[i]));
                                    i=i+1;
                                }

                            }
                        }
                        m++;
                        monthly = Integer.toString(m);
                    }

                    DocumentReference userRef = db.collection("PackList").document(user_id);
                    final String[] finalWeather = weather;
                    final int[] finalQty = qty;
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                final DocumentSnapshot docUser = task.getResult();

                                //add item
                                CollectionReference itemRef = db.collection("Stuff");
                                itemRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(int c = 0; c< finalWeather.length; c++){
                                                for (QueryDocumentSnapshot docItem : task.getResult()){
                                                    final String w = finalWeather[c];
//                                                    final int q = Integer.toString(finalQty[c]);
                                                    final int q = finalQty[c];
//                                                final String qty = Integer.toString(array_day[c]);

                                                        String gender = docUser.getString("gender");
                                                        String reason = docUser.getString("reason");
                                                        String transport = docUser.getString("transport");
                                                        final String id = docItem.getId();

                                                        boolean item_gender = docItem.getBoolean(gender);
                                                        boolean item_reason = docItem.getBoolean(reason);
                                                        boolean item_transport = docItem.getBoolean(transport);
                                                        Log.i("Bool", w);
                                                        boolean item_weather = docItem.getBoolean(w);
                                                        boolean activities=false;
                                                        boolean activities_1= false;
                                                        boolean activities_2= false;
                                                        boolean activities_3= false;

                                                        String item_name=docItem.getString("item");

                                                        boolean item_bool = true;


                                                        if(userActivities.length>0){
                                                            for(int a=0; a<userActivities.length; a++) {
                                                                Log.i("array", id);
                                                                if (userActivities[a] != null) {
                                                                    String activity = userActivities[a];
                                                                    Log.i("AC", activity);
                                                                    if (activity.equals("mountainClimb")) {
                                                                        activities_1 = docItem.getBoolean(activity);
                                                                    }
                                                                    else if (activity.equals("survival")) {
                                                                        activities_2 = docItem.getBoolean(activity);
                                                                    }
                                                                    else if (activity.equals("waterSport")) {
                                                                        activities_3 = docItem.getBoolean(activity);
                                                                    }
                                                                }
                                                            }
                                                            if(activities_1 || activities_2 || activities_3){
                                                                activities=true;
                                                            }
                                                        }


                                                        if(!item_reason){
                                                            item_bool =false;
                                                        }
                                                        if(!item_transport){
                                                            item_bool =false;
                                                        }
                                                        if(!item_weather){
                                                            item_bool =false;
                                                        }
                                                        if(activities){
                                                            item_bool=true;
                                                        }
                                                        if(!item_gender){
                                                            item_bool =false;
                                                        }



                                                        if(item_bool){
                                                            Log.i("Item:", item_name);
                                                            final Map<String,Object> newItem = new HashMap<>();
                                                            if(item_name.equals("Mineral Water") || item_name.equals("Towel")){
                                                                newItem.put("item",item_name);
                                                                newItem.put("qty",0);
                                                                newItem.put("weather", w);
                                                                newItem.put("weatherCount", c);
                                                            }
                                                            else{
                                                                newItem.put("item",item_name);
                                                                newItem.put("qty",q);
                                                                newItem.put("weather", w);
                                                                newItem.put("weatherCount", c);
                                                            }




                                                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                                            DocumentReference docIdRef = rootRef.collection(user_id).document(id);
                                                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            db.collection (user_id).document(id)
                                                                                    .update("qty", FieldValue.increment(q))
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Log.i("OldItem","Item add successful");
                                                                                        }
                                                                                    });
                                                                        } else {

                                                                            db.collection (user_id).document(id)
                                                                                    .set(newItem)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Log.i("Item","Item add successful");
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                        }
                                                }

                                            }
                                        }
                                    }
                                });
                            }
                            btnPdf.setVisibility(View.VISIBLE);
                            Toast.makeText(PackingListActivity.this,"Packing List Generated!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                } else {
                    Log.d("Weather", "get failed with ", task.getException());
                }
            }
        });



        for (int i = 0; i < array_day.length; i++) {
            Log.i("Array", "array " + i + " :" + array_day[i] + "\n");
        }
    }

    public void get_user(){
        FirebaseUser mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
        user_email = mFirebaseUser.getEmail();
        user_id = mFirebaseUser.getUid();
    }

    public  void get_packlist(){

        db = FirebaseFirestore.getInstance();

        FirebaseFirestore itemRef = FirebaseFirestore.getInstance();
        Query query = itemRef.collection(user_id).orderBy("qty", Query.Direction.DESCENDING);


        travelInfo();


        FirestoreRecyclerOptions<Packlist> options =
                new FirestoreRecyclerOptions.Builder<Packlist>()
                        .setQuery(query, Packlist.class)
                        .build();

        FirestoreRecyclerAdapter<Packlist, PacklistViewHolder> adapter =
                new FirestoreRecyclerAdapter<Packlist, PacklistViewHolder>(options) {
                    @NonNull
                    @Override
                    public PacklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.packlist_layout,parent, false);
                        PacklistViewHolder holder = new PacklistViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PacklistViewHolder holder,
                                                    int position, @NonNull Packlist model) {
                        final String itemName = model.getItem();

                        int item_int = model.getQty();
                        String qtyy;
                        if(item_int>=15){
                            qtyy="x 15";
                        }
                        else if(item_int==0 || itemName.equals("Mineral Water")){
                            qtyy=" ";
                        }
                        else{
                            qtyy = "x "+ item_int;
                        }
                        final String itemQty = qtyy;

                        holder.itemName.setText(itemName);
                        holder.itemQty.setText(itemQty);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void previous(View view) {

        if(save == 0){
            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
            myAlertBuilder.setTitle("Discard Pack lists");
            myAlertBuilder.setMessage("You didn't download the PDF, this pack list will be discard without saving.\n\nDo you sure want to exit without save?");
            myAlertBuilder.setPositiveButton("Exit", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
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
                            finish();
                        }
                    });
            myAlertBuilder.setNegativeButton( "Save & Exit", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            savePdf();
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
                            finish();
                        }
                    });
            myAlertBuilder.setNeutralButton( "Cancel", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            myAlertBuilder.show();
        }else{
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
            finish();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(save == 0){
            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
            myAlertBuilder.setTitle("Discard Pack lists");
            myAlertBuilder.setMessage("You didn't download the PDF, this pack list will be discard without saving.\n\nDo you sure want to exit without save?");
            myAlertBuilder.setPositiveButton("Exit", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
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
                            finish();
                        }
                    });
            myAlertBuilder.setNegativeButton( "Save & Exit", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            savePdf();
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
                            finish();
                        }
                    });
            myAlertBuilder.setNeutralButton( "Cancel", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            myAlertBuilder.show();
        }else{
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
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        save =0;
        set_weather();
        get_packlist();
    }

    public void filter(View view) {
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
        startActivity(new Intent(PackingListActivity.this, FilterActivity.class));
    }
}
