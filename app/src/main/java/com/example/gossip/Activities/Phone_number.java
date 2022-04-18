package com.example.gossip.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


import com.example.gossip.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Phone_number extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(Phone_number.this);
        progressDialog.setTitle("Sending OTP");
        progressDialog.setMessage("We Have Sent OTP to your Mobile Number");


        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(Phone_number.this,MainActivity.class);

            startActivity(intent);
            finish();

        }

        binding.sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = binding.phoneNumber.getText().toString();
                if(phoneNumber.isEmpty()){
                    binding.phoneNumber.setError("Please Enter Your Number");
                }
                else{
                Intent intent = new Intent(Phone_number.this,otp.class);
                intent.putExtra("phoneNumber",binding.phoneNumber.getText().toString());
                startActivity(intent);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 2000);
            }
            }
        });
    }
}