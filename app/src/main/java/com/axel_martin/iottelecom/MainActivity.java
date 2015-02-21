package com.axel_martin.iottelecom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.axel_martin.iottelecom.model.Data;
import com.axel_martin.iottelecom.model.Info;
import com.axel_martin.iottelecom.model.Measure;
import com.axel_martin.iottelecom.model.Model;
import com.axel_martin.iottelecom.utils.HttpGetAsyncTask;
import com.axel_martin.iottelecom.utils.ParserInfoAsyncTask;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    public static final int TEMPERATURE_FRAGMENT = 2;
    public static final int LIGHT_FRAGMENT = 3;
    public static final int HUMIDITY_FRAGMENT = 4;

    private Toolbar toolBar;
    private Model model;

    public final static int SETTINGS_CODE = 1000;
    public final static int SETTINGS_UNCHANGED = 1100;
    public final static int SETTINGS_CHANGED = 1200;

    private ValueFragment valueFragment;
    private FragmentManager fragmentManager;

    private boolean isRotate = false;
    private boolean isActivityResult = false;


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            model.setMeasureList((ArrayList<Measure>) bundle.getSerializable("RESULT"));
            Log.d("MEASURELIST", String.valueOf(model.getMeasureList().size()));
            UpdateTask updateTask = new UpdateTask();
            updateTask.execute();
            Intent intent2 = new Intent("com.axel_martin.iottelecom.MainActivity.FLUSH");
            sendBroadcast(intent2);
        }
    };

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            model = (Model) savedInstanceState.getSerializable("MODEL");
            isRotate  = true;
            Log.d("MAIN ACTIVITY", "restore model");
        } else {
            model = new Model();
        }

        setContentView(R.layout.toolbar_layout); //invoke the layout

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Intent serviceIntent = new Intent(this, DataService.class);
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        serviceIntent.putExtra(DataService.POLLING_REF, preferences.getInt("interval", 60000));
        serviceIntent.putExtra(DataService.CACHE_REF, preferences.getInt("cache", 10));
        serviceIntent.putExtra(DataService.IS_TEMPERATURE_ALERT_REF, preferences.getBoolean("isTemperatureEnable", false));
        serviceIntent.putExtra(DataService.MAX_TEMPERATURE_REF, preferences.getInt("maximumTemperature", -1));
        serviceIntent.putExtra(DataService.MIN_TEMPERATURE_REF, preferences.getInt("minimumTemperature", -1));
        serviceIntent.putExtra(DataService.IS_LIGHT_ALERT_REF, preferences.getBoolean("isLightEnable", false));
        serviceIntent.putExtra(DataService.LIGHT_REF, preferences.getInt("light", -1));
        serviceIntent.putExtra(DataService.SCHEDULDED_REF, preferences.getBoolean("isTimeEnable", false));
        serviceIntent.putExtra(DataService.START_TIME_REF, preferences.getString("startTime", ""));
        serviceIntent.putExtra(DataService.END_TIME_REF, preferences.getString("endTime", ""));
        serviceIntent.putExtra(DataService.MAIL_REF, preferences.getBoolean("isMailEnable", false));
        serviceIntent.putExtra(DataService.MAIL_ADDRESS_REF, preferences.getString("mailAddress", ""));
        serviceIntent.putExtra(DataService.SMS_REF, preferences.getBoolean("isSmsEnable", false));
        serviceIntent.putExtra(DataService.SMS_ADDRESS_REF, preferences.getString("telNumber", ""));
        startService(serviceIntent);



    }

    public void sendFirstFlush(){
        Intent intentFirstFlush = new Intent("com.axel_martin.iottelecom.MainActivity.FIRST");
        sendBroadcast(intentFirstFlush);
        Log.d("MAIN ACTIVITY","FIRST FLUSH SEND");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.axel_martin.iottelecom"));
        if(!(isRotate || isActivityResult)){
            Log.d("MAIN ACTIVITY", "UPDATE");
            updateInfo();
        }
        /*if(!isActivityResult){
            Log.d("MAIN ACTIVITY", "UPDATE");
            updateInfo();
        } else {
          isActivityResult = true;
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        if(position == 0){
            tx.replace(R.id.container, MainFragment.newInstance(position + 1, model)).commitAllowingStateLoss();
        } else {
            tx.replace(R.id.container,valueFragment.newInstance(position + 1, model)).commitAllowingStateLoss();
            try {
                valueFragment.update();
            }catch (Exception e){

            }

        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.Overview);
                break;
            case 2:
                mTitle = getString(R.string.Temperature);
                break;
            case 3:
                mTitle = getString(R.string.Light);
                break;
            case 4:
                mTitle = getString(R.string.Humidity);
                break;
        }
    }

    public void restoreActionBar() {
        toolBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_CODE); //start the activity of preferences
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGS_CODE){
            Log.d("MAIN ACTIVITY", "SETTINGS CODE");
            Log.d("MAIN ACTIVITY", "SETTINGS CODE "+String.valueOf(resultCode));
            if(resultCode == SETTINGS_CHANGED){
                Log.d("MAIN ACTIVITY", "SETTINGS CHANGED");
                updateServiceInfo();
            } else if(resultCode == SETTINGS_UNCHANGED){
                Log.d("MAIN ACTIVITY", "SETTINGS UNCHANGED");
            }
            isActivityResult = true;
        }
    }

    @Override
    public ActionBar getSupportActionBar() {
        // TODO Auto-generated method stub
        return super.getSupportActionBar();
    }

    public void updateSenderList(){
        for (int i = 0; i < model.getInfo().getSender().size(); i++) {
            boolean correspondance = false;
            for (int j = 0; j < model.getSenderList().size(); j++) {
                if (model.getSenderList().get(j).getId() == model.getInfo().getSender().get(i).getId()) {
                    model.getSenderList().get(j).setIpv6(model.getInfo().getSender().get(i).getIpv6());
                    model.getSenderList().get(j).setLat(model.getInfo().getSender().get(i).getLat());
                    model.getSenderList().get(j).setLon(model.getInfo().getSender().get(i).getLon());
                    model.getSenderList().get(j).setMac(model.getInfo().getSender().get(i).getMac());
                    correspondance = true;
                    break;
                } else {
                    correspondance = false;
                }
            }
            if (!correspondance) {
                model.getSenderList().add(model.getInfo().getSender().get(i));
            }
            correspondance = false;
        }
    }

    public void updateInfo(){
        HttpGetAsyncTask httpInfoTask = new HttpGetAsyncTask(this, HttpGetAsyncTask.INFO);
        httpInfoTask.execute("http://iotlab.telecomnancy.eu/rest/info/motes");
    }

    public void updateInfoToParse(String info){
        ParserInfoAsyncTask parserInfoTask = new ParserInfoAsyncTask(this);
        parserInfoTask.execute(info);
    }

    public void updateInfoFinishParsing(Info info){
        model.setInfo(info);
    }

    public void updateSenderListDatas(){
        for(int w=0; w<model.getMeasureList().size();w++) {
            for (int i = 0; i < model.getMeasureList().get(w).getData().size(); i++) {
                for (int j = 0; j < model.getSenderList().size(); j++) {
                    if (model.getSenderList().get(j).getIpv6() == model.getMeasureList().get(w).getData().get(i).getMote()) {
                        if (model.getSenderList().get(j).getDatalist() == null) {
                            model.getSenderList().get(j).setDatalist(new ArrayList<Data>());
                        }
                        model.getSenderList().get(j).getDatalist().add(model.getMeasureList().get(w).getData().get(i));
                    }
                }
            }
        }
    }

    public void updateFragments(){

        for(int i=0; i<fragmentManager.getFragments().size();i++){
            if(fragmentManager.getFragments().get(i) instanceof ValueFragment){
                ((ValueFragment) fragmentManager.getFragments().get(i)).update();
                Log.d("Update", "Update valueFrag");
            } else if(fragmentManager.getFragments().get(i) instanceof MainFragment) {
                ((MainFragment) fragmentManager.getFragments().get(i)).update();
                Log.d("Update", "Update mainFrag");
            }
        }
    }

    private class UpdateTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                if(model.getSenderList() != null) {
                    updateSenderList();
                    updateSenderListDatas();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateFragments();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("MODEL", model);
        super.onSaveInstanceState(outState);

    }

    public void updateServiceInfo(){
        Log.d("MAIN ACTIVITY", "UPDATE SERVICE");
        Intent intentUpdateInfo = new Intent("com.axel_martin.iottelecom.MainActivity.UPDATE");
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        intentUpdateInfo.putExtra(DataService.POLLING_REF, preferences.getInt("interval", 60000));
        intentUpdateInfo.putExtra(DataService.CACHE_REF, preferences.getInt("cache", 10));
        intentUpdateInfo.putExtra(DataService.IS_TEMPERATURE_ALERT_REF, preferences.getBoolean("isTemperatureEnable", false));
        intentUpdateInfo.putExtra(DataService.MAX_TEMPERATURE_REF, preferences.getInt("maximumTemperature", -1));
        intentUpdateInfo.putExtra(DataService.MIN_TEMPERATURE_REF, preferences.getInt("minimumTemperature", -1));
        intentUpdateInfo.putExtra(DataService.IS_LIGHT_ALERT_REF, preferences.getBoolean("isLightEnable", false));
        intentUpdateInfo.putExtra(DataService.LIGHT_REF, preferences.getInt("light", -1));
        intentUpdateInfo.putExtra(DataService.SCHEDULDED_REF, preferences.getBoolean("isTimeEnable", false));
        intentUpdateInfo.putExtra(DataService.START_TIME_REF, preferences.getString("startTime", ""));
        intentUpdateInfo.putExtra(DataService.END_TIME_REF, preferences.getString("endTime", ""));
        intentUpdateInfo.putExtra(DataService.MAIL_REF, preferences.getBoolean("isMailEnable", false));
        intentUpdateInfo.putExtra(DataService.MAIL_ADDRESS_REF, preferences.getString("mailAddress", ""));
        intentUpdateInfo.putExtra(DataService.SMS_REF, preferences.getBoolean("isSmsEnable", false));
        intentUpdateInfo.putExtra(DataService.SMS_ADDRESS_REF, preferences.getString("telNumber", ""));
        sendBroadcast(intentUpdateInfo);
    }

}
