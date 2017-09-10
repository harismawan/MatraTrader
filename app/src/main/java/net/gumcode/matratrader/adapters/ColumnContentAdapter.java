package net.gumcode.matratrader.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.models.Report;
import net.gumcode.matratrader.utilities.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class ColumnContentAdapter extends TableDataAdapter<Report> {


    public ColumnContentAdapter(Context context, List<Report> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Report report = getRowData(rowIndex);
        View renderedView = null;

        TextView textView = new TextView(getContext());
        textView.setPadding(0, 20, 0, 20);
        switch (columnIndex) {
            case 0:
                Date date = new Date(TimeUnit.SECONDS.toMillis(report.market));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\nHH:mm", Locale.US);
                textView.setText(sdf.format(date));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 1:
                textView.setText("$" + Utils.roundUp(report.buy));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 2:
                textView.setText("$" + Utils.roundUp(report.sell));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 3:
                if (report.sell > report.buy) {
                    textView.setText("+$" + Utils.roundUp(report.sell - report.buy));
                    textView.setTextColor(getResources().getColor(R.color.op));
                    textView.setGravity(Gravity.CENTER);
                } else if (report.sell < report.buy) {
                    textView.setText("-$" + Utils.roundUp(report.buy - report.sell));
                    textView.setTextColor(Color.RED);
                    textView.setGravity(Gravity.CENTER);
                } else if (report.sell == report.buy) {
                    textView.setText("$" + Utils.roundUp(report.sell));
                    textView.setGravity(Gravity.CENTER);
                }
                renderedView = textView;
                break;
        }

        return renderedView;
    }
}
