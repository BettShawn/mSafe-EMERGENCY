package com.msafe.emergency;/*package com.msafe.emergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextInputLayout inputname,inputemail,inputpassword,inputage,inputaddress,inputpin;
    private Button btnsubmit;

    DatabaseReference ref;
    FirebaseAuth mfirebaseauth;
    Member member;
    String phonenumber;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT>=21)
        {
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        inputname=findViewById(R.id.inputname);
        inputemail=findViewById(R.id.inputemail);
        inputpassword=findViewById(R.id.inputpassword);
        inputage=findViewById(R.id.inputage);
        inputaddress=findViewById(R.id.inputaddress);
        inputpin=findViewById(R.id.inputpin);
        btnsubmit=findViewById(R.id.btnsubmit);

        mfirebaseauth = FirebaseAuth.getInstance();
        member=new Member();
        phonenumber = getIntent().getStringExtra("phonenumber");
//        ref= FirebaseDatabase.getInstance().getReference().child("USERS");


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputname.getEditText().getText().toString().trim().length()==0)
                    inputname.setError("Name is required");
                else if (inputemail.getEditText().getText().toString().trim().length()==0)
                    inputemail.setError("Email is required");
                else if (inputpassword.getEditText().getText().toString().trim().length()==0)
                    inputpassword.setError("Password is required");
                else if(inputage.getEditText().getText().toString().trim().length()==0)
                    inputage.setError("Age is required");
                else if (inputaddress.getEditText().getText().toString().trim().length()==0)
                    inputaddress.setError("Address is required");
                else if (inputpin.getEditText().getText().toString().trim().length()==0)
                    inputpin.setError("Pic Code of your area is required");
                else {
                    progressDialog=new ProgressDialog(Register.this);
                    progressDialog.setMessage("Logging in....please wait...");
                    progressDialog.show();

                    CreateUserAndSaveData();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        finish();
        //startActivity(new Intent(Register.this, Login.class));
    }

    private void CreateUserAndSaveData() {

        mfirebaseauth.createUserWithEmailAndPassword(inputemail.getEditText().getText().toString(),inputpassword.getEditText().getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    saveData();
                    Toast.makeText(Register.this, "User Successfully Registered...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.dismiss();
                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Log.i("ERROR",e.getMessage());
                    Toast.makeText(Register.this, "User Registration Failed....Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData()
    {
        phonenumber = getIntent().getStringExtra("phonenumber");

        member.setName(inputname.getEditText().getText().toString().trim());
        member.setEmail(inputemail.getEditText().getText().toString().trim());
        member.setPassword(inputpassword.getEditText().getText().toString().trim());
        member.setAge(inputage.getEditText().getText().toString().trim());
        member.setAddress(inputaddress.getEditText().getText().toString().trim());
        member.setPin(inputpin.getEditText().getText().toString().trim());
        member.setContactnumber(phonenumber);

        ref= FirebaseDatabase.getInstance().getReference().child("USERS").child(mfirebaseauth.getCurrentUser().getUid());

        ref.setValue(member);

        FirebaseAuth.getInstance().signOut();
      //  startActivity(new Intent(Register.this, Login.class));

    }
}
*/