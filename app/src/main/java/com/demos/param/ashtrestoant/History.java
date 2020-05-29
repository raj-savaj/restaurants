package com.demos.param.ashtrestoant;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.demos.param.ashtrestoant.Adapter.History_Adapter;
import com.demos.param.ashtrestoant.Adapter.History_Order_Item;
import com.demos.param.ashtrestoant.DB.DaoSession;
import com.demos.param.ashtrestoant.DB.Order;
import com.demos.param.ashtrestoant.Services.WifiCommunication;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import org.greenrobot.greendao.database.Database;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class History extends AppCompatActivity implements History_Adapter.ItemClickListen {

    DaoSession daoSession;
    List<Order> orderList;
    RecyclerView rcvhis;
    History_Adapter history_adapter;
    Dialog dialog;
    Dialog dialog_detail;

    private SwitchDateTimeDialogFragment dateTimeFragment;
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    TextView txtsdate,txtedate;
    Button dbtnok,dbtncan;
    boolean chksdate=false,chkedate=false;
    Calendar calendar;
    Database db;

    TextView tabelname,tot_Amount;
    ImageView prtintbtn;
    RecyclerView rcy_dialog;

    int connFlag=0;
    History.revMsgThread revThred = null;
    WifiCommunication wfComm = null;
    Session s;
    String BILL = "";
    String[] bill=new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        s=new Session(getApplicationContext());
        rcvhis=findViewById(R.id.rcv_his);
        getSupportActionBar().setTitle("Order History");
        rcvhis.setLayoutManager(new LinearLayoutManager(this));
        orderList=new ArrayList<>();
        history_adapter=new History_Adapter(orderList,this);
        daoSession = ((App) getApplication()).getDaoSession();
        db = ((App) getApplication()).getDaoSession().getDatabase();
        Cursor cursor = db.rawQuery("SELECT sum(QTY*PRICE) as total,TBNAME,OID,TIME FROM `ORDER` GROUP BY OID ORDER BY OID DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date="";
            try {
                Date d = dateFormat.parse(cursor.getString(3));
                DateFormat dateFormatdb = new SimpleDateFormat("hh:mm a \ndd-MM-yyyy");
                date=String.valueOf(dateFormatdb.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Order order=new Order(cursor.getString(2),cursor.getString(1),cursor.getInt(0),date);
            orderList.add(order);
            cursor.moveToNext();
        }
        cursor.close();
        rcvhis.setAdapter(history_adapter);
        history_adapter.notifyDataSetChanged();
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {

        }

        dialog = new Dialog(History.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_date);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog_detail= new Dialog(History.this);
        dialog_detail.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_detail.setContentView(R.layout.dialog_order);
        dialog_detail.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);;


        txtsdate = (TextView) dialog.findViewById(R.id.text_sdate);
        txtedate=(TextView)dialog.findViewById(R.id.text_edate);
        dbtnok=(Button)dialog.findViewById(R.id.okbtn);
        dbtncan=(Button)dialog.findViewById(R.id.canbtn);

        tabelname=(TextView) dialog_detail.findViewById(R.id.tabelname);
        rcy_dialog=(RecyclerView)dialog_detail.findViewById(R.id.rcy_dialog);
        rcy_dialog.setLayoutManager(new LinearLayoutManager(this));
        prtintbtn=(ImageView)dialog_detail.findViewById(R.id.prtintbtn);
        tot_Amount=(TextView)dialog_detail.findViewById(R.id.tot_Amount);

        calendar=Calendar.getInstance();
        txtsdate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(!chksdate) {
                   dateTimeFragment.startAtCalendarView();
                   dateTimeFragment.setDefaultDateTime(calendar.getTime());
                   dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
                   chksdate=true;
               }

           }
       });

        txtedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chkedate)
                {
                    dateTimeFragment.startAtCalendarView();
                    dateTimeFragment.setDefaultDateTime(calendar.getTime());
                    dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
                    chkedate=true;
                }

            }
        });

        dbtncan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dbtnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtsdate.getText().equals("") && !txtedate.getText().equals(""))
                {
                    String sql="delete  from `ORDER` where `TIME` between '"+txtsdate.getText().toString()+"' AND '"+txtedate.getText().toString()+"'";
                    db.execSQL(sql);
                    RefreshData();
                }
                dialog.dismiss();
            }
        });

        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                if(chksdate)
                {
                    chksdate=false;
                    txtsdate.setText(myDateFormat.format(date));
                }
                if(chkedate)
                {
                    chkedate=false;
                    txtedate.setText(myDateFormat.format(date));
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                if(chksdate)
                {
                    chksdate=false;
                }
                if(chkedate)
                {
                    chkedate=false;
                }
            }

            @Override
            public void onNeutralButtonClick(Date date) {

            }
        });

        prtintbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfComm = new WifiCommunication(mHandler);
                wfComm.initSocket(s.getPrinterIp(), 9100,bill);
                dialog_detail.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.delete:
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void RefreshData()
    {
        orderList.clear();
        Cursor cursor = db.rawQuery("SELECT sum(QTY*PRICE) as total,TBNAME,OID,TIME FROM `ORDER` GROUP BY OID ORDER BY OID DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date="";
            try {
                Date d = dateFormat.parse(cursor.getString(3));
                DateFormat dateFormatdb = new SimpleDateFormat("hh:mm a \ndd-MM-yyyy");
                date=String.valueOf(dateFormatdb.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Order order=new Order(cursor.getString(2),cursor.getString(1),cursor.getInt(0),date);
            orderList.add(order);
            cursor.moveToNext();
        }
        history_adapter.notifyDataSetChanged();
    }

    @Override
    public void orderItemClick(View v, int position) {

        Order o=orderList.get(position);
        tabelname.setText(o.getTbname());
        tot_Amount.setText(""+o.getPrice());

        bill[0]=o.getTbname();

        List<Order> itemlist=new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM `ORDER`WHERE OID ='"+o.getOid()+"'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Order order=new Order(cursor.getString(0), cursor.getInt(1), cursor.getString(2),cursor.getInt(3),cursor.getString(4), cursor.getInt(5),  cursor.getInt(6), cursor.getString(7), cursor.getString(8));
            BILL = BILL + String.format("%-35s %-7s %7s %7s\n", ""+o.getName()+"", ""+o.getQty()+"", ""+o.getPrice()+"", ""+o.getQty()*o.getPrice()+"");
            itemlist.add(order);
            cursor.moveToNext();
        }
        bill[1]=BILL;
        bill[2]=String.valueOf(o.getPrice());

        History_Order_Item historyOrderItem=new History_Order_Item(itemlist);
        rcy_dialog.setAdapter(historyOrderItem);
        dialog_detail.show();
        cursor.close();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    connFlag = 0;
                    revThred = new History.revMsgThread();
                    revThred.start();
                    Intent i=new Intent(History.this,Productview.class);
                    startActivity(i);
                    finish();
                    return;
                case 1:
                    Toast.makeText(History.this, "Disconnect the WIFI-printer successful", Toast.LENGTH_SHORT).show();
                    revThred.interrupt();
                    return;
                case 2:
                    connFlag = 0;
                    Toast.makeText(History.this, "Connect the WIFI-printer error", Toast.LENGTH_SHORT).show();
                    return;
                case 4:
                    connFlag = 0;
                    Toast.makeText(History.this, "Send Data Failed,please reconnect", Toast.LENGTH_SHORT).show();
                    revThred.interrupt();
                    return;
                case 6:
                    if (((((byte) Integer.parseInt(msg.obj.toString())) >> 6) & 1) == 1) {
                        Toast.makeText(History.this, "The printer has no paper", Toast.LENGTH_LONG).show();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };

    class revMsgThread extends Thread {
        revMsgThread() {
        }

        public void run() {
            try {
                Message msg = new Message();
                while (true) {
                    int revData = wfComm.revByte();
                    if (revData != -1) {
                        msg = mHandler.obtainMessage(6);
                        msg.obj = Integer.valueOf(revData);
                        mHandler.sendMessage(msg);
                    }
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("wifi调试","eror"+e.getMessage());
            }
        }
    }

}
