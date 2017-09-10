package net.gumcode.matratrader.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gumcode.matratrader.models.Progress;
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
public class ColumnContentAdapter3 extends TableDataAdapter<Progress> {


    public ColumnContentAdapter3(Context context, List<Progress> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Progress progress = getRowData(rowIndex);
        View renderedView = null;

        TextView textView = new TextView(getContext());
        textView.setPadding(0, 20, 0, 20);
        Date date;
        SimpleDateFormat sdf;
        switch (columnIndex) {
            case 0:
                if (progress.entryTime != 0) {
                    date = new Date(TimeUnit.SECONDS.toMillis(progress.entryTime));
                    sdf = new SimpleDateFormat("HH:mm", Locale.US);
                    textView.setText(sdf.format(date));
                    textView.setGravity(Gravity.CENTER);
                    renderedView = textView;
                } else {
                    textView.setText("-");
                    textView.setGravity(Gravity.CENTER);
                    renderedView = textView;
                }
                break;
            case 1:
                if (progress.currentTime != 0) {
                    date = new Date(TimeUnit.SECONDS.toMillis(progress.currentTime));
                    sdf = new SimpleDateFormat("HH:mm", Locale.US);
                    textView.setText(sdf.format(date));
                    textView.setGravity(Gravity.CENTER);
                    renderedView = textView;
                } else {
                    textView.setText("-");
                    textView.setGravity(Gravity.CENTER);
                    renderedView = textView;
                }
                break;
            case 2:
                textView.setText("$" + Utils.roundUp(progress.bidPrice));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
        }

        return renderedView;
    }
}
