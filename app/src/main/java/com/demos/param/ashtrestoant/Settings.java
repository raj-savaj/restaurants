package com.demos.param.ashtrestoant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Settings extends AppCompatActivity {
    Session s;
    EditText txtip,txtprintip;
    Button btnip,btnprinterip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");
        s=new Session(getApplicationContext());
        txtip=findViewById(R.id.txtip);
        txtprintip=findViewById(R.id.txtntip);
        btnip=findViewById(R.id.btnip);
        btnprinterip=findViewById(R.id.btnprinterip);
        txtip.setText(s.getBaseIp());
        txtprintip.setText(s.getPrinterIp());

        btnip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(Settings.this,SweetAlertDialog.WARNING_TYPE).setTitleText("Are you sure Want To Set IP ?")
                        .setCancelText("cancel")
                        .setConfirmText("Confirm")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                s.setIpAddress(txtip.getText().toString().replaceAll("/$",""));
                                Intent i=new Intent(getApplicationContext(),Alogins.class);
                                startActivity(i);
                            }
                        })
                        .show();
            }
        });

        btnprinterip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(Settings.this,SweetAlertDialog.WARNING_TYPE).setTitleText("Are you sure Want To Set Printer IP ?")
                        .setCancelText("cancel")
                        .setConfirmText("Confirm")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                s.setPrinterIp(txtprintip.getText().toString());
                                Intent i=new Intent(getApplicationContext(),Alogins.class);
                                startActivity(i);
                            }
                        })
                        .show();
            }
        });
    }
}
