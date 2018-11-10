package com.example.vutuan.bkres_nb.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vutuan.bkres_nb.MainActivity;
import com.example.vutuan.bkres_nb.R;
import com.example.vutuan.bkres_nb.adapter.GraphAdapter;
import com.example.vutuan.bkres_nb.model.Graph;
import com.example.vutuan.bkres_nb.task.DownloadJSON;
import com.example.vutuan.bkres_nb.ultils.Constant;
import com.example.vutuan.bkres_nb.ultils.XuLyThoiGian;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    public HistoryFragment() {
        // Required empty public constructor
    }

    private static final String TITLE="TITLE";
    private static final String GRAPH="GRAPH";
    private static final String TIME="TIME";
    private static final String DEVICEID="DEVICEID";
    private static final String DATE_TIME="DATE_TIME";

    private TextView txtItemContent, txt_Time;
    private TextView txtTitle;
    private RecyclerView rvItemBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter;
//    private CustomGraphAdapter adapter;

    private DownloadJSON downloadJSON;
    String arr_thongso[] = {"Nhiệt độ", "Độ ẩm"};

    public String tmpNgayThangNam="";
    public String tmpSelectedDeviceId="";

    private Calendar calendar;
    private int year, month, day;
    ProgressDialog pDialog;
    String selectedDateTime = "";

    private View v;

    //All components of all graphs
    private ArrayList<Entry> entriesHumi=new ArrayList<>();
    private ArrayList labelsHumi = new ArrayList<String>();

    private ArrayList<Entry> entriesTemp=new ArrayList<>();
    private ArrayList labelsTemp = new ArrayList<String>();

    //All label of graph
    private String[] arrLabels=new String[]{"Nhiệt độ","Độ ẩm" };

    public static HistoryFragment newInstance(String title, int selectDeviceID){
        HistoryFragment df=new HistoryFragment();
        Bundle bundle=new Bundle();
        bundle.putString(TITLE,title);
        bundle.putInt(DEVICEID,selectDeviceID);
        df.setArguments(bundle);
        return df;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        v= LayoutInflater.from(getContext()).inflate(R.layout.fragment_history,container,false);

        initWidgets(v);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();

        if ( month < 9 ){
            if (day<9){
                selectedDateTime = "0" + day + "/" + "0"+ (month+1) +"/" + year;
            } else
                selectedDateTime = day + "/" + "0"+ (month+1) +"/" + year;
        } else{
            if (day<9){
                selectedDateTime = "0" + day + "/" +  (month+1) +"/" + year;
            } else
                selectedDateTime = day + "/" + (month+1) +"/" + year;
        }

        String title= MainActivity.selectedDevice;
        tmpSelectedDeviceId= String.valueOf(MainActivity.selectedDeviceId);

        pDialog = new ProgressDialog(getContext());

        txtTitle.setText("Thiết bị : "+title);
        txt_Time.setText("Ngày : "+ selectedDateTime);
        rvItemBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvItemBieuDoThongKe.setLayoutManager(manager);

        initGraph();

        //lay du lieu bieu do cua 12 thong so
        for (int i=1;i<=2;i++){
            getDataThongKe(i);
        }

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void initWidgets(View v){
        txtItemContent= (TextView) v.findViewById(R.id.txtItemContent);
        txtTitle= (TextView) v.findViewById(R.id.txtTitle);
        rvItemBieuDoThongKe=  v.findViewById(R.id.rvItemBieuDoThongKe);
        txt_Time = (TextView) v.findViewById(R.id.txt_time);
    }

    public void initGraph(){
        entriesHumi=new ArrayList<>();
        labelsHumi = new ArrayList<String>();

        entriesTemp=new ArrayList<>();
        labelsTemp = new ArrayList<String>();

        listGraph=new ArrayList<>();
        Graph gTemp=new Graph(arrLabels[0],entriesTemp,labelsTemp);
        Graph gHumi=new Graph(arrLabels[1],entriesHumi,labelsHumi);
        Graph [] arrGraph=new Graph[]{gTemp, gHumi};
        for (Graph g:arrGraph){
            listGraph.add(g);
        }


        adapter = new GraphAdapter( listGraph);
        rvItemBieuDoThongKe.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //lay du lieu ung voi tung thong so
    public void getDataThongKe(final int tempSelectThongSo) {
        String urlThongKe = Constant.URL + Constant.API_GET_DATA_PACKAGE + "date=" + tmpNgayThangNam +
                "&deviceid=" + tmpSelectedDeviceId + "&paramid=" + String.valueOf(tempSelectThongSo);
        Uri builder = Uri.parse(urlThongKe)
                .buildUpon()
                .build();

        downloadJSON = new DownloadJSON(getContext());


        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                try{
                    //JSONObject jsonObj = new JSONObject(msgData);
                    JSONArray jsonArray = new JSONArray(msgData);
                    if(jsonArray.length()==0){
//                        txtContent.setVisibility(View.VISIBLE);
                    }
                    else{
                        String nameGraph=arr_thongso[tempSelectThongSo-1];
                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList labels = new ArrayList<String>();

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            double value = objTmp.getDouble("value");
                            String time = objTmp.getString("time");
                            String[] words = time.split("T");
                            Log.d("time",words[0]+words[1]);
                            entries.add(new Entry(i,(float)value));
                            labels.add(words[1]);
                        }
                        Graph graph=new Graph(nameGraph,entries,labels);

                        Log.i("size graph", listGraph.size() + "");
                        if (listGraph.size() > 0) {
                            txtItemContent.setVisibility(View.GONE);
                        }

                        int index = -1;

                        for (int k = 0; k < arr_thongso.length; k++) {
                            if (nameGraph == arr_thongso[k]) {
                                index = k;
                            }
                        }
                        listGraph.set(index, graph);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
//                progress.dismiss();
                Log.i("Error", msgError);
            }
        });
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuTime){
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),myDateListener,year,month,day);
            datePickerDialog.show();
        }
        return true;
    }



    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    year = arg1; month = arg2; day = arg3;

                    if ( arg2 < 9 ){
                        tmpNgayThangNam =  "0"+ (arg2+1) +"/" + arg3 +"/" + arg1;

                        if (arg3<9){
                            selectedDateTime = "0" + arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                        } else
                            selectedDateTime = arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                    } else{
                        tmpNgayThangNam =  (arg2+1)+ "/" + arg3 +"/" +  arg1;

                        if (arg3<9){
                            selectedDateTime = "0" + arg3 + "/" +  (arg2+1) +"/" + arg1;
                        } else
                            selectedDateTime = arg3 + "/" +  (arg2+1) +"/" + arg1;
                    }

                    for (int i = 1 ; i <= 2 ; i++)
                        getDataThongKe(i);

                    txt_Time.setText("Ngày : "+ selectedDateTime);

                }
            };
}
