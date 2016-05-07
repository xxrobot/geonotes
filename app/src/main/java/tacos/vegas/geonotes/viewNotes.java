package tacos.vegas.geonotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class viewNotes extends AppCompatActivity {

    String[] colors = {"red","orange","yellow"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,colors);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
    }
}
