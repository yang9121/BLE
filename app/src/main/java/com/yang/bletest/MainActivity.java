package com.yang.bletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        init_view();

    }

    public ListView mlist;
    public String[] modes = new String[]{
            "Central Mode",
            "Peripheral Mode",
    };

    private void init_view()
    {
        mlist = (ListView)findViewById(R.id.list1);
        mlist.setAdapter(new modeAdapter(this, modes));
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
                    case 0:
                        startActivity(new Intent(MainActivity.this, central.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, peripheral.class));
                        break;
                    default:
                        break;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class modeAdapter extends BaseAdapter {

        private Context context;
        private String[] modes;

        modeAdapter(Context context, String[] modes) {
            this.context = context;
            this.modes = modes;
        }

        @Override
        public int getCount() {
            if (modes == null)
                return 0;
            return modes.length;
        }

        @Override
        public String getItem(int position) {
            if (modes == null)
                return null;
            return modes[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_mode, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_mode = (TextView) convertView.findViewById(R.id.text_list);
            }
            holder.txt_mode.setText(modes[position]);
            return convertView;
        }

        class ViewHolder {
            TextView txt_mode;
        }
    }


}
