package net.gumcode.binarymobile.menus.account;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gumcode.binarymobile.R;
import net.gumcode.binarymobile.adapters.ColumnContentAdapter;
import net.gumcode.binarymobile.adapters.ColumnHeaderAdapter;
import net.gumcode.binarymobile.models.Report;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SortableTableView tableView = (SortableTableView) findViewById(R.id.table);
        tableView.setHeaderAdapter(new ColumnHeaderAdapter(this, getResources().getStringArray(R.array.table_title)));

        List<Report> data = new ArrayList<>();
        data.add(new Report("Random100", 10, 11, 21));
        data.add(new Report("Random100", 15, 20, 26));
        data.add(new Report("Random100", 8, 10, 28));
        tableView.setDataAdapter(new ColumnContentAdapter(this, data));
    }
}
