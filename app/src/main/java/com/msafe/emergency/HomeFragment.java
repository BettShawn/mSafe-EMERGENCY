package com.msafe.emergency;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.msafe.emergency.R;

public class HomeFragment extends Fragment {

    Fragment selectedFragment=null;


    RadioGroup rgcategory,rgcondition;
    RadioButton radioButtonCondition,radioButtonCategory;
    Button btnnext;

    String condition,category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home,container,false);

        btnnext=root.findViewById(R.id.btnnext);
        rgcategory = root.findViewById(R.id.rgcategory);
        rgcondition = root.findViewById(R.id.rgcondition);

        btnnext.setVisibility(View.INVISIBLE);
        btnnext.setEnabled(false);

        rgcategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.rbaccident:
                        btnnext.setEnabled(true);
                        break;

                    case R.id.rbpregwoman:
                        btnnext.setEnabled(true);
                        break;

                    case R.id.rboldpeople:
                        btnnext.setEnabled(true);
                        break;
                }
            }
        });

        rgcondition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.rblesscritical:
                        btnnext.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rbcritical:
                        btnnext.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rbverycritical:
                        btnnext.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedCondition = rgcondition.getCheckedRadioButtonId();
                int selectedCategory = rgcategory.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButtonCondition = root.findViewById(selectedCondition);
                condition= (String)radioButtonCondition.getText();

                // find the radiobutton by returned id
                radioButtonCategory = root.findViewById(selectedCategory);
                category= (String)radioButtonCategory.getText();

                Intent intent=new Intent(getContext(), HospitalActivity.class);
                intent.putExtra("Condition",condition);
                intent.putExtra("Category",category);
                Toast.makeText(getContext(), condition+"\n"+category, Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        return root;
}}
