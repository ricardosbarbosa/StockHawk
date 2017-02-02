package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StockDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private LineChart mChart;

    private static final int CURSOR_LOADER_ID = 0;
    private Cursor mCursor;
    private ArrayList<String> dates = new ArrayList<String>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        Intent intent = getIntent();
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.string_symbol), intent.getStringExtra(getResources().getString(R.string.string_symbol)));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);

        mChart = (LineChart) findViewById(R.id.chart1);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1); // one hour

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int) value);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;

    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI,
                new String[]{ Contract.Quote.COLUMN_PRICE, Contract.Quote.COLUMN_HISTORY },
                Contract.Quote.COLUMN_SYMBOL + " = ?",
                new String[]{args.getString(getResources().getString(R.string.string_symbol))},
                null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        setData();
        mChart.invalidate();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setData() {

        ArrayList<Entry> values = new ArrayList<Entry>();

        mCursor.moveToFirst();
        String history = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

        String[] linhas = history.split("\n");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");

        for(int i = 0; i < linhas.length; i++) {
            String[] colunas = linhas[i].split(",");

            String priceString = colunas[1].trim();
            Float price = Float.parseFloat(priceString);
            String dateString = colunas[0].trim();
            long dateLong = Long.parseLong(dateString);
            dates.add(df.format(new Date(dateLong)));

            values.add(new Entry(i, price));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

}