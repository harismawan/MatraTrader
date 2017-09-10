package net.gumcode.matratrader.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.codecrafters.tableview.TableHeaderAdapter;

/**
 * Created by A. Fauzi Harismawan on 2/16/2016.
 */
public class ColumnHeaderAdapter extends TableHeaderAdapter {

    private String[] headers;

    public ColumnHeaderAdapter(Context context, String[] headers) {
        super(context, headers.length);
        this.headers = headers;
    }

    @Override
    public View getHeaderView(int columnIndex, ViewGroup parentView) {
        String title = headers[columnIndex];

        TextView textView = new TextView(getContext());
        textView.setTextSize(14);
        textView.setPadding(20, 20, 20, 20);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText(title);

        return textView;
    }
}
