package android.fr.runforall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class TempsFragment extends Fragment {

    DataBaseHandler bdd;
    ArrayList<Long> timestamp;
    ArrayList<Long> temps;
    double listSize = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_temps, container, false);

        bdd= new DataBaseHandler(getActivity());

        timestamp = bdd.getAllTimestamp();
        temps = bdd.getAllTemps();
        listSize = timestamp.size();

        LineChart chart = (LineChart)V.findViewById(R.id.chartTemps);

        //Liste des Entry contenant les distances parcourues
        ArrayList<Entry> tempsData = new ArrayList<>();

        //Définition des valeurs de l'axe des ordonnées et des l'abscisses
        ArrayList<String> xVals = new ArrayList<>();
        for (int i=0; i<listSize; i++){
            tempsData.add(new Entry(temps.get(i),i)); //ordronnées
            String dateTimestamp = bdd.calculDateTimestamp(timestamp.get(i)); //abscisse
            xVals.add(dateTimestamp);
        }

        LineDataSet setComp1 = new LineDataSet(tempsData, "Temps en secondes");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(ContextCompat.getColor(getContext(), R.color.rouge));

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        rightAxis.setEnabled(false);
        leftAxis.setStartAtZero(false);
        chart.setTouchEnabled(true);
        chart.setVisibleXRangeMaximum(4.0f);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        chart.setDescription("");
        chart.invalidate(); // refresh

        return V;
    }
}