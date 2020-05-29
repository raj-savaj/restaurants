package com.demos.param.ashtrestoant;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.demos.param.ashtrestoant.DB.DaoMaster;
import com.demos.param.ashtrestoant.DB.DaoSession;
import com.demos.param.ashtrestoant.DB.productList;
import  com.demos.param.ashtrestoant.DB.productListDao;
import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    public static final boolean ENCRYPTED = true;
    private DaoSession daoSession;
    private productListDao plistdao;
    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"rest_db"); //The users-db here is the name of our database.
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        plistdao=daoSession.getProductListDao();
        //getProduct();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void getProduct() {
        Session s=new Session(getApplicationContext());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

        Call<ArrayList<productList>> call = api.getProduct();
        call.enqueue(new Callback<ArrayList<productList>>() {
            @Override
            public void onResponse(Call<ArrayList<productList>> call, Response<ArrayList<productList>> response) {
                if (response.isSuccessful()) {
                    List<productList> productLists = response.body();
                    if (productLists.size()!=0)
                    {
                        plistdao.deleteAll();
                        plistdao.insertInTx(productLists);
                    }
                }
                else
                {
                    Log.e("Error",""+response.message());
                }

            }

            @Override
            public void onFailure(Call<ArrayList<productList>> call, Throwable t) {
                Toast.makeText(App.this, "IpAddress Is not match Please Manual Data Sync ", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
