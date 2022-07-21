package com.msafe.emergency;/*package com.msafe.emergency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class OTPverification extends AppCompatActivity {

    private TextInputLayout inputcontactnumber;
    private Spinner spinnercountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        spinnercountries = findViewById(R.id.spinnercountries);
        spinnercountries.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        inputcontactnumber = findViewById(R.id.inputcontactnumber);

        findViewById(R.id.btnsendotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinnercountries.getSelectedItemPosition()];

                String number = inputcontactnumber.getEditText().getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    inputcontactnumber.setError("Valid number is required");
                    inputcontactnumber.requestFocus();
                    return;
                }

                String phonenumber = "+" + code + number;

                Intent intent = new Intent(OTPverification.this, getotp.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, OTPverification.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }
}
*/