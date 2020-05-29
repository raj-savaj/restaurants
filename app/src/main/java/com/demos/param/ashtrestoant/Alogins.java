package com.demos.param.ashtrestoant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demos.param.ashtrestoant.DB.DaoSession;
import com.demos.param.ashtrestoant.DB.productList;
import com.demos.param.ashtrestoant.DB.productListDao;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Alogins extends AppCompatActivity {

    EditText uname,pass;
    CoordinatorLayout loginsnack;
    ImageView login;

    DaoSession daoSession;
    private productListDao plistdao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_alogins);
        uname=findViewById(R.id.txtemail);
        pass=findViewById(R.id.txtpass);
        login=findViewById(R.id.btnlogin);

        FirebaseMessaging.getInstance().subscribeToTopic("Resto");

        daoSession = ((App) getApplication()).getDaoSession();
        plistdao=daoSession.getProductListDao();

        login.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                login.startAnimation(animFadein);
                Intent i=new Intent(getApplicationContext(),Settings.class);
                startActivity(i);
                return true;
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                login.startAnimation(animFadein);
                String unm=uname.getText().toString();
                String pas=pass.getText().toString();
                if(!unm.equals("") && !pas.equals("")) {
                    login(unm, pas);
                }
            }
        });
    }

    private void login(String email, String pass) {

        Session s=new Session(getApplicationContext());
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);
        final SweetAlertDialog process= new SweetAlertDialog(Alogins.this, SweetAlertDialog.PROGRESS_TYPE);
        process.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        process.setTitleText("Verifying...");
        process.setCancelable(false);
        process.show();

        Call<String> call=api.Login(email,pass);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    try
                    {
                        JSONObject sys=new JSONObject(response.body());
                        String status = sys.getString("status");
                        if(status.equals("1"))
                        {
                            process.cancel();

                            Gson gson = new Gson();
                            String json =sys.getJSONArray("product").toString();
                            Type type = new TypeToken<List<productList>>() {
                            }.getType();
                            List<productList> productLists =gson.fromJson(json,type);
                            if (productLists.size()!=0)
                            {
                                plistdao.deleteAll();
                                plistdao.insertInTx(productLists);
                            }
                            Intent i=new Intent(getApplication(),Productview.class);;
                            if(sys.getString("type").equals("p"))
                            {
                                i=new Intent(getApplication(),Parsel.class);
                            }
                            else if(sys.getString("type").equals("k"))
                            {
                                i=new Intent(getApplication(),KitchenScreen.class);
                            }
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            process.cancel();
                            new SweetAlertDialog(Alogins.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Username And Password Not Match")
                                    .show();
                        }
                    }catch (Exception e) {
                        Log.e("Error",""+e.getMessage());
                        process.cancel();
                        new SweetAlertDialog(Alogins.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Server Not Responding!")
                                .show();
                        e.printStackTrace();
                    }
                }
                else
                {
                    process.cancel();
                    new SweetAlertDialog(Alogins.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Invalid Url ")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                process.cancel();
                Toast.makeText(Alogins.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}



