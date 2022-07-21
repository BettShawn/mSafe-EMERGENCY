package com.msafe.emergency;/*package com.msafe.emergency;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextInputLayout inputemail,inputpassword;
    Button btnlogin;
    TextView tvregister,tvforgotpassword;

    FirebaseAuth mfirebaseauth;

    private long backPressedTime;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputemail=findViewById(R.id.inputemail);
        inputpassword=findViewById(R.id.inputpassword);
        btnlogin=findViewById(R.id.btnlogin);
        tvregister=findViewById(R.id.tvregister);
        tvforgotpassword=findViewById(R.id.tvforgotpassword);

        mfirebaseauth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=inputemail.getEditText().getText().toString().trim();
                String password=inputpassword.getEditText().getText().toString().trim();
                if(email.isEmpty())
                {
                    inputemail.setError("Please enter email id");
                    inputemail.requestFocus();
                }
                else if(password.isEmpty())
                {
                    inputpassword.setError("Please enter password");
                    inputpassword.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty())
                {
                    Toast.makeText(Login.this, "Login failed....please fill up the details", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && password.isEmpty()))
                {
                    progressDialog=new ProgressDialog(Login.this);
                    progressDialog.setMessage("Logging in....please wait...");
                    progressDialog.show();

                    mfirebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Login Unsuccessful....please try again later", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(Login.this,CategoryActivity.class));
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Error Occured....please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Login.this, OTPverification.class);
                startActivity(i);
            }
        });

        tvforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Forgot_password_Activity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis())
        {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else
        {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }
}
*/