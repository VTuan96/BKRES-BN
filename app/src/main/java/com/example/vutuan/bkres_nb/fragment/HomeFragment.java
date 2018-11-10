package com.example.vutuan.bkres_nb.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vutuan.bkres_nb.MainActivity;
import com.example.vutuan.bkres_nb.R;
import com.example.vutuan.bkres_nb.model.Datapackage;
import com.example.vutuan.bkres_nb.model.Device;
import com.example.vutuan.bkres_nb.task.DownloadJSON;
import com.example.vutuan.bkres_nb.ultils.Constant;
import com.example.vutuan.bkres_nb.ultils.XuLyThoiGian;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private TextView txtHumi, txtTemp, txtTime, txtDevice;

    public double Humi_Max, Humi_Min, Temp_Max, Temp_Min;
    MainActivity activity = new MainActivity();

    DownloadJSON downloadJSON;
    ArrayList<Device> listDevice;
    public static String selectedDevice = "";
    public String selectedImeiDevice = "";
    private Socket mSocket;
    final String TAG = "Socket IO";

    {
        try {
            IO.Options opts = new IO.Options();
            opts.path = "/socket.io 2.03 version new";
            mSocket = IO.socket("http://202.191.56.103:5525");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtHumi = view.findViewById(R.id.txtHumi);
        txtTemp = view.findViewById(R.id.txtTemp);
        txtDevice = view.findViewById(R.id.txtDevice);
        txtTime = view.findViewById(R.id.txtTime);

        mSocket.on("new message", onDataReceive);
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR,onConnectError);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        //get information of lake and device in lake
        downloadJSON = new DownloadJSON(getContext());
        listDevice = new ArrayList<>();
        getLakeAndDevice();

        return  view;
    }


    private void getLakeAndDevice() {
        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_ALL_DEVICE)
                .buildUpon()
                .build();
        downloadJSON = new DownloadJSON(getContext());

        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);

                try {
                    JSONArray jsonArray = new JSONArray(msgData);
                    for (int i = 0 ; i< jsonArray.length(); i++){
                        JSONObject jsonDeviceObj = jsonArray.getJSONObject(i);
                        int IdDevice = jsonDeviceObj.getInt("Id");
                        String NameDevice = jsonDeviceObj.getString("Name");
                        String ImeiDevice = jsonDeviceObj.getString("Imei");
                        String CreatedDateDevice = jsonDeviceObj.getString("CreatedDate");
                        String WarningNumberPhone = jsonDeviceObj.getString("WarningPhoneNumber");
                        String WarningMail = jsonDeviceObj.getString("WarningMail");
                        Device deviceObj = new Device(IdDevice, NameDevice, ImeiDevice, CreatedDateDevice, WarningNumberPhone, WarningMail);

                        deviceObj.toString();
                        listDevice.add(deviceObj);
                    }

                    if (listDevice.size()>0){
                        selectedDevice = listDevice.get(0).getName();
                        selectedImeiDevice = listDevice.get(0).getImei();
                        connectSocket(selectedImeiDevice);

                        updateLakeAndDevice(listDevice.get(0).getId());

//                        pDialog.setMessage("Đang tải...");
//                        pDialog.show();


                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                        //getDatapackageByDeviceName();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
                Log.i("Error", msgError);
            }
        });

    }

    private void updateLakeAndDevice(int DeviceId) {
        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_NEWEST_PARAM + DeviceId)
                .buildUpon()
                .build();
        downloadJSON = new DownloadJSON(getContext());

        downloadJSON.GetJSON2(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);
                try{
                    JSONObject jsonObject = new JSONObject(msgData);
                    String TimePackage = jsonObject.getString("TimePackage");
                    double Hum = jsonObject.getDouble("Hum");
                    double Temp = jsonObject.getDouble("Temp");

                    //get settings of data
                    getPreferences();
                    //check current data and data on settings
                    setColorTextWarning(Hum,Humi_Min,Humi_Max,txtHumi);
                    setColorTextWarning(Temp,Temp_Min,Temp_Max,txtTemp);

                    txtHumi.setText(String.valueOf(Hum));
                    txtTemp.setText(String.valueOf(Temp));
                    txtDevice.setText("Thiết bị : " +listDevice.get(0).getName());
                    txtTime.setText("Cập nhật : " + TimePackage.replace("T"," "));

                    MainActivity.selectedDeviceId = listDevice.get(0).getId();
                    MainActivity.selectedDevice = listDevice.get(0).getName();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {

            }
        });
    }

    private void connectSocket(String imei){
        mSocket.emit("authentication", imei);
        mSocket.emit("join", imei);
        mSocket.on("new message", onDataReceive);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeOut);
        mSocket.connect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getContext()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getContext()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "disconnected");

                        Toast.makeText(getContext(),
                                "Disconnect", Toast.LENGTH_SHORT).show();
                        mSocket.connect();
                        connectSocket(selectedImeiDevice);

                    }
                });
            }
        }
    };

    private Emitter.Listener onTimeOut = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getContext()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Time out");
                        Toast.makeText(getContext(),
                                "Time Out", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getContext()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, args[0].toString());
//                    Toast.makeText(getApplicationContext(),
//                            "Lỗi cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private Emitter.Listener onDataReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getContext()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = args[0].toString();
                        Log.i("Socket IO data", data);

                        try {
                            JSONObject jsonObj = new JSONObject(data);

                            Log.i("IMEI DEVICE RECEIVE", selectedImeiDevice);
                            String deviceImei = jsonObj.getString("Device_IMEI");
                            Log.i("IMEI DEVICE SERVER", deviceImei);

                            if(deviceImei.compareTo(selectedImeiDevice) == 0){
                                String Time_Package = jsonObj.getString("Datetime_Packet");
                                double Humi          = jsonObj.getDouble("Hum");
                                double Temp         = jsonObj.getDouble("NhietDo");
                                String NgayTao      = jsonObj.getString("Datetime_Packet");

                                Datapackage datapackage = new Datapackage(-1, -1, Time_Package, Temp, Humi, NgayTao);
                                updateView(datapackage);

                                //get settings of data
                                getPreferences();
                                //check current data and data on settings
                                setColorTextWarning(Humi,Humi_Min,Humi_Max,txtHumi);
                                setColorTextWarning(Temp,Temp_Min,Temp_Max,txtTemp);


                            } else{
                                mSocket.connect();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
    };

    private void updateView(Datapackage datapackage) {
        txtTemp.setText(datapackage.getTemp()+ "");
        txtHumi.setText(datapackage.getHumi() + "");
        txtDevice.setText("Thiết bị : " + listDevice.get(0).getName());
        txtTime.setText("Cập nhật lúc : " + XuLyThoiGian.StringToDatetimeString(datapackage.getTime_Package()));

        MainActivity.selectedDeviceId = listDevice.get(0).getId();
        MainActivity.selectedDevice = listDevice.get(0).getName();
    }

    //change color of text parameter if it's out of range
    private void setColorTextWarning(double param, double min, double max, TextView txtParam){
        activity = (MainActivity) getActivity();
        if (activity != null && isAdded()) {
            if (param > max || param < min) {
                txtParam.setTextColor(Color.RED);
            } else {
                txtParam.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
            }
        }
    }


    //get data on settings
    private void getPreferences(){
        if (getContext() != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Humi_Max = preferences.getFloat(SettingFragment.KEY_HUMI_MAX, Constant.DEFAULT_HUMI_MAX);
            Humi_Min = preferences.getFloat(SettingFragment.KEY_HUMI_MIN, Constant.DEFAULT_HUMI_MIN);

            Temp_Max = preferences.getFloat(SettingFragment.KEY_TEMP_MAX, Constant.DEFAULT_TEMP_MAX);
            Temp_Min = preferences.getFloat(SettingFragment.KEY_TEMP_MIN, Constant.DEFAULT_TEMP_MIN);
        }
    }

    //check limit of parameter
    private boolean checkParameter(double parameter, double min, double max){
        if (parameter < min || parameter > max){
            return true; //if out of range
        }
        return false; //if in range
    }
}
