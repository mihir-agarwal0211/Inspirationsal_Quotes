package com.example.inspirequote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String AUTHOR_KEY = "author";
    public static final String QUOTE_KEY = "quote";
    public static final String TAG = "InspiringQuotes";
    EditText quote;
    EditText author;

    TextView textView;


    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    public void fetchQuote(View view){
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String qouteText = documentSnapshot.getString(QUOTE_KEY);
                    String authorText = documentSnapshot.getString(AUTHOR_KEY);
                    textView.setText("\"" + qouteText + "\" --" + authorText );
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String qouteText = documentSnapshot.getString(QUOTE_KEY);
                    String authorText = documentSnapshot.getString(AUTHOR_KEY);
                    textView.setText("\"" + qouteText + "\" --" + authorText );
                } else if(e!=null){
                    Log.w(TAG, "Got an exception",e);
                }
            }
        });
    }

    public void saveQuote (View view) {

        if(author.getText().toString().isEmpty() || quote.getText().toString().isEmpty()){
            Toast.makeText(this, "please fill both the entries", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,Object> dataToSave = new HashMap<>();
        dataToSave.put(QUOTE_KEY,quote.getText().toString());
        dataToSave.put(AUTHOR_KEY,author.getText().toString());
        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Document has been saved!!");
                Toast.makeText(MainActivity.this, "Document has been saved!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Document was not saved",e);
                Toast.makeText(MainActivity.this, "Document was not saved", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quote = findViewById(R.id.editText);
        author = findViewById(R.id.editText2);
        textView = findViewById(R.id.textView);
    }
}
