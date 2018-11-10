package com.example.vutuan.bkres_nb.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vutuan.bkres_nb.R;
import com.example.vutuan.bkres_nb.fragment.SettingFragment;
import com.example.vutuan.bkres_nb.model.Graph;
import com.example.vutuan.bkres_nb.ultils.Constant;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {
    ArrayList<Graph> listGraph;
    Context context;

    public GraphAdapter(ArrayList<Graph> listGraph) {
        this.listGraph = listGraph;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bieu_do_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        context=parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Graph graph=listGraph.get(position);

        if (graph!=null){
            //set ten bieu do
            String nameGraph=graph.getName_graph();
            holder.txtTenBieuDo.setText(nameGraph);

            //set du lieu cho bieu do
            if (graph.getEntries().size()>0){
                int lineChart=1; // line chart in a chart ( default =1 line)
                double min=0; //min of line chart data
                double max=0; //max of line chart data

                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);

                if (nameGraph.equals("Nhiệt độ")){
                    min=preferences.getFloat(SettingFragment.KEY_TEMP_MIN, Constant.DEFAULT_TEMP_MIN);
                    max=preferences.getFloat(SettingFragment.KEY_TEMP_MAX,Constant.DEFAULT_TEMP_MAX);
                    lineChart=3; // 3 line chart
                } else if(nameGraph.equals("Độ ẩm")){
                    min=preferences.getFloat(SettingFragment.KEY_HUMI_MIN,Constant.DEFAULT_HUMI_MIN);
                    max=preferences.getFloat(SettingFragment.KEY_HUMI_MAX,Constant.DEFAULT_HUMI_MAX);
                    lineChart=3;
                }

                ArrayList<ILineDataSet> lineDataSets=new ArrayList<>();

                LineDataSet dataset = new LineDataSet(graph.getEntries(), "Current");
                dataset.setColors(Color.BLUE); //
                dataset.setCircleColor(Color.BLUE);
                dataset.setDrawFilled(false);

                if (lineChart==3){
                    ArrayList<Entry> entry1=new ArrayList<>(); //max entries
                    ArrayList<Entry> entry2=new ArrayList<>(); //min entries
                    ArrayList<Entry> tmp=graph.getEntries();

                    for (int i=0;i<tmp.size();i++){
                        entry1.add(new Entry(i, (float) max));
                        entry2.add(new Entry(i, (float) min));
                    }

                    LineDataSet dataset1 = new LineDataSet(entry1, "High Warning");
                    dataset1.setColors(Color.RED); //
                    dataset1.setCircleColor(Color.RED);
                    dataset1.setDrawFilled(false);
                    lineDataSets.add(dataset1);

                    LineDataSet dataset2 = new LineDataSet(entry2, "Low Warning");
                    dataset2.setColors(Color.YELLOW); //
                    dataset2.setCircleColor(Color.YELLOW);
                    dataset2.setDrawFilled(false);
                    lineDataSets.add(dataset2);

                    lineDataSets.add(dataset);

                } else
                    lineDataSets.add(dataset);

                IAxisValueFormatter formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (value<graph.getLabels().size() && value>=0) return graph.getLabels().get((int) value);
                        else return String.valueOf(value);
                    }

                };

                XAxis xAxis = holder.graph.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);

                holder.graph.setData(new LineData(lineDataSets));
                holder.graph.animateY(3000);
                holder.graph.invalidate();
            }

        }

    }

    @Override
    public int getItemCount() {
        return listGraph.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTenBieuDo;
        public LineChart graph;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTenBieuDo= (TextView) itemView.findViewById(R.id.txt_TenBieuDo);
            graph= (LineChart) itemView.findViewById(R.id.graph);
        }
    }
}
