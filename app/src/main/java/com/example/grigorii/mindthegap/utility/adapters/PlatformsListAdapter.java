package com.example.grigorii.mindthegap.utility.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.ArrivalBoard;
import com.example.grigorii.mindthegap.model.Station;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;


/**
 * Created by grigorii on 19/06/16.
 *
 * Adapter class for the listView in PlatformsListFragment. Allows to
 * connect data to the listView
 */
public class PlatformsListAdapter extends ArrayAdapter<ArrivalBoard> {

    // Data array
    private List<ArrivalBoard> mArrivalBoards;


    public PlatformsListAdapter(Context context, int resource, List<ArrivalBoard> arrivalBoards, Station station) {
        super(context, resource, arrivalBoards);
        mArrivalBoards = arrivalBoards;
    }

    /**
     * Method for adding elements of data array to the listView
     * @param position position of an element in the listView
     * @param convertView view where data should be added
     * @param parent parent viewGroup
     * @return convertView filled with data
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.fragment_platform_item, null);
        }

        ArrivalBoard board = getItem(position);
        if (board != null) {

            TextView lineName = (TextView) v.findViewById(R.id.line_name);
            TextView platformName = (TextView) v.findViewById(R.id.platform_name);
            View colorLine = v.findViewById(R.id.line_color);

            colorLine.setBackgroundColor(getColorByLineId(board.getLineId()));
            lineName.setText(WordUtils.capitalize(board.getLineId()));
            platformName.setText(board.getPlatformName());
        }
        return v;
    }

    /**
     * Called from ArrivalsLoader when all the data is loaded.
     * Allows to rebuild listView with new data
     * @param arrivalBoards new data array
     */
    public void setData(List<ArrivalBoard> arrivalBoards) {

        mArrivalBoards.clear();
        mArrivalBoards.addAll(arrivalBoards);

        //Notify adapter that the dataset changed
        notifyDataSetChanged();
    }

    /**
     * Method for getting color based on lineId
     * @param id line id
     * @return color
     */
    private int getColorByLineId(String id) {

        switch (id) {
            case "central":
                return Color.RED;
            case "circle":
                return Color.YELLOW;
            case "district":
                return Color.parseColor("#1B5E20");
            case "jubilee":
                return (Color.GRAY);
            case "northern":
                return Color.BLACK;
            case "piccadilly":
                return Color.BLUE;
            case "bakerloo":
                return Color.parseColor("#795548");
            case "victoria":
                return Color.CYAN;
            case "hammersmith-cyty":
                return Color.parseColor("#F48FB1");
            case "metropolitan":
                return Color.MAGENTA;
            default:
                return Color.WHITE;
        }
    }
}
