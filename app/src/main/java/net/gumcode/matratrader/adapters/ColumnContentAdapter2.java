package net.gumcode.matratrader.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gumcode.matratrader.models.Progress;
import net.gumcode.matratrader.utilities.Utils;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class ColumnContentAdapter2 extends TableDataAdapter<Progress> {


    public ColumnContentAdapter2(Context context, List<Progress> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Progress progress = getRowData(rowIndex);
        View renderedView = null;

        TextView textView = new TextView(getContext());
        textView.setPadding(0, 20, 0, 20);
        switch (columnIndex) {
            case 0:
                textView.setText(Double.toString(progress.entrySpot));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 1:
                textView.setText(Double.toString(progress.currentSpot));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
            case 2:
                textView.setText(Utils.roundUp(progress.currentSpot - progress.entrySpot));
                textView.setGravity(Gravity.CENTER);
                renderedView = textView;
                break;
        }

        return renderedView;
    }
}
