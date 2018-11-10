package com.example.vutuan.bkres_nb;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.example.vutuan.bkres_nb.fragment.HistoryFragment;
import com.example.vutuan.bkres_nb.fragment.HomeFragment;
import com.example.vutuan.bkres_nb.fragment.SettingFragment;
import com.example.vutuan.bkres_nb.ultils.XuLyThoiGian;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

//    private TextView mTextMessage;
    private Fragment fragment = null;
    private FragmentManager manager = null;
    private FragmentTransaction transaction =  null;
    MenuItem menuItem = null;
    public static String tmpNgayThangNam = "";
    public static String selectedDateTime = "";
    public static int selectedDeviceId = 0;
    public static String selectedDevice = "";
    private Calendar calendar;
    private int year, month, day;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.mnuHome:
                    fragment = new HomeFragment();
                    fragment.onAttach(MainActivity.this);
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Giám sát hệ thống");
                    return true;
                case R.id.mnuHistory:
                    setTitle("Lịch sử");
                    fragment = new HistoryFragment();
                    fragment.onAttach(MainActivity.this);
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    return true;
                case R.id.mnuSetting:
                    fragment = new SettingFragment();
                    fragment.onAttach(MainActivity.this);
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Cấu hình cảnh báo");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        manager = getSupportFragmentManager();

        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

}
