package com.rbyte.dragernesdal.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.main.MainDAO;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.main.model.MainDTO;
import com.rbyte.dragernesdal.ui.character.select.SelectViewModel;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private final static int UPDATE_TIMER = 10000;
    public static final String USER_ID_SAVESPACE = "currUserIDSave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.username);
        TextView userEmail = headerView.findViewById(R.id.userEmail);
        username.setText(getIntent().getStringExtra("username"));
        userEmail.setText(getIntent().getStringExtra("email"));
        Log.d("UserID",getIntent().getStringExtra("id"));
        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(USER_ID_SAVESPACE, Integer.parseInt(getIntent().getStringExtra("id")));
        editor.commit();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_char_skill,R.id.nav_char_magic,R.id.nav_char_inventory,R.id.nav_char_background,R.id.nav_char_select,R.id.nav_rules,R.id.nav_event,R.id.nav_createCharacterFragment,R.id.nav_chooseRaceFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        UpdaterThread updater = new UpdaterThread();
        updater.run();


    }



    /*@Override
    public void onBackPressed() { //TODO have int state for each fragment, and do different stuff according to state? if possible
        new AlertDialog.Builder(this)
                .setTitle("Log ud?")
                .setMessage("Er du sikker på at du vil logge ud?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        loginIntent.putExtra(getString(R.string.logout_command), true);
                        startActivity(loginIntent);
                        finish();
                    }})
                .setNegativeButton("Nej", null).show();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    HashMap<String, String> tableTimes = new HashMap<>();
    MainDAO dao = new MainDAO();

    class UpdaterThread extends Thread { //Thread for checking for if changes  and starting threads to update


        public UpdaterThread(){
            tableTimes.put("abilities", null);
            tableTimes.put("character", null);
            tableTimes.put("eventAttendancyList", null);
            tableTimes.put("events", null);
            tableTimes.put("inventory", null);
            tableTimes.put("krysling", null);
            tableTimes.put("magicschools", null);
            tableTimes.put("ownedabilities", null);
            tableTimes.put("ownedspelltiers", null);
            tableTimes.put("races", null);
            tableTimes.put("spellrelation", null);
            tableTimes.put("spells", null);
            tableTimes.put("user", null);
        }

        @Override
        public void run() {
            checks();
        }
    }

    private void checks(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() ->{
            Log.d("UpdaterThread","Started");
            HomeViewModel homeVM = HomeViewModel.getInstance();
            SelectViewModel selectCharVM = SelectViewModel.getInstance();
            while (true){
                try {
                    Thread.sleep(UPDATE_TIMER);
                    Log.d("UpdaterThread","Slept for: "+UPDATE_TIMER);
                    Log.d("UpdaterThread", tableTimes.toString());
                    for (String key : tableTimes.keySet()){
                        Result<MainDTO> res = dao.getAbilitiesByCharacterID(key);
                        if (res instanceof Result.Success){
                            String time = ((Result.Success<MainDTO>) res).getData().getInfo();
                            if (tableTimes.get(key) == null) tableTimes.put(key, time); //initial (Should not update, cause when repos init, they get.
                            else if (!tableTimes.get(key).equals(time)){
                                tableTimes.put(key, time);
                                //TODO start update of those repository
                                switch (key){
                                    case "ownedabilities":
                                        homeVM.updateCurrentAbilities();
                                        Log.d("UpdaterThread", "Running abilities");
                                        break;
                                    case "character":
                                        homeVM.updateCurrentCharacter();
                                        selectCharVM.updateCurrentCharacters();
                                        Log.d("UpdaterThread", "Running character");
                                        break;
                                    case "inventory":
                                        homeVM.updateCurrentMoney();
                                        Log.d("UpdaterThread", "Running inventory");
                                        break;
                                }
                            }
                        }
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
    }
}