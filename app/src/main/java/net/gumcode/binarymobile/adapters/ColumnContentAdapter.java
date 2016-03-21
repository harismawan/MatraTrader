package net.gumcode.binarymobile.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gumcode.binarymobile.models.Report;

import java.util.List;

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
                textView.setText(report.market);
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 1:
                textView.setText("$" + report.buy);
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 2:
                textView.setText("$" + report.sell);
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 3:
                textView.setText("$" + report.balance);
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
        }

        return renderedView;
    }
}
