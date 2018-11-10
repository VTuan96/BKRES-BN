package com.example.vutuan.bkres_nb.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.vutuan.bkres_nb.R;
import com.example.vutuan.bkres_nb.ultils.Constant;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    public static SharedPreferences SP;
    public RangeSeekBar sbTemp, sbHumi;
    Button btnSave;

    public static double  Temp_Max, Temp_Min, Humi_Max, Humi_Min;

    public static final String SETTING_PREFERENCES = "SettingPrefer";
    public static final String KEY_TEMP_MAX = "TempMax";
    public static final String KEY_TEMP_MIN = "TempMin";
    public static final String KEY_HUMI_MAX = "HumiMax";
    public static final String KEY_HUMI_MIN = "HumiMin";


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        sbTemp = view.findViewById(R.id.sbTemp);
        sbHumi = view.findViewById(R.id.sbHumi);
        btnSave = view.findViewById(R.id.btnSave);

        SP = getActivity().getSharedPreferences(SETTING_PREFERENCES, Context.MODE_PRIVATE);
        loadDataFromSharePreferences();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataPreference();
            }
        });

        return view;
    }

    public void loadDataFromSharePreferences(){
        SP = PreferenceManager.getDefaultSharedPreferences(getContext());

        Temp_Max =  SP.getFloat(KEY_TEMP_MAX, Constant.DEFAULT_TEMP_MAX);
        Temp_Min =  SP.getFloat(KEY_TEMP_MIN, Constant.DEFAULT_TEMP_MIN);

        Humi_Max =  SP.getFloat(KEY_HUMI_MAX, Constant.DEFAULT_HUMI_MAX);
        Humi_Min =  SP.getFloat(KEY_HUMI_MIN, Constant.DEFAULT_HUMI_MIN);


        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.####", otherSymbols);


        sbTemp.setSelectedMaxValue(Temp_Max);
        sbTemp.setSelectedMinValue(Temp_Min);

        sbHumi.setSelectedMaxValue(Humi_Max);
        sbHumi.setSelectedMinValue(Humi_Min);


    }

    public void updateDataPreference(){


        float _Humi_Max = sbHumi.getSelectedMaxValue().floatValue();
        float _Humi_Min = sbHumi.getSelectedMinValue().floatValue();

        float _Temp_Max = sbTemp.getSelectedMaxValue().floatValue();
        float _Temp_Min = sbTemp.getSelectedMinValue().floatValue();



        SharedPreferences.Editor editor = SP.edit();
        editor.putFloat(KEY_TEMP_MAX, _Temp_Max);
        editor.putFloat(KEY_TEMP_MIN, _Temp_Min);

        editor.putFloat(KEY_HUMI_MAX, _Humi_Max);
        editor.putFloat(KEY_HUMI_MIN, _Humi_Min);


        editor.commit();
        Toast.makeText(getContext(),"Saved", Toast.LENGTH_SHORT).show();
    }

}
