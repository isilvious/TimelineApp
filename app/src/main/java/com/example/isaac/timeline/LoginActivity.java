package com.example.isaac.timeline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {


    SharedPreferences sharedPref;
    CheckBox cb;
    TextView email;
    TextView password;
    TextView error;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cb = (CheckBox) findViewById(R.id.stay_logged);
        email = (TextView) findViewById(R.id.edtx_email);
        password = (TextView) findViewById(R.id.edtx_password);
        sv = (ScrollView) findViewById(R.id.sv_login);
        error = (TextView) findViewById(R.id.tx_login_error);
        error.setVisibility(View.INVISIBLE);

        sharedPref = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume(){
        super.onResume();

        //get Remember me pref
        if(sharedPref.getBoolean(getString(R.string.pref_stay_logged_in), false)){
            cb.setChecked(true);
            email.setText(sharedPref.getString(getString(R.string.pref_last_username), ""));
            password.setText(sharedPref.getString(getString(R.string.pref_last_password), ""));
        }
    }

    public void login(View view){
        error.setVisibility(View.INVISIBLE);

        //update stay_logged_in
        if(cb.isChecked()){
            sharedPref.edit().putBoolean(getString(R.string.pref_stay_logged_in), true).apply();
            sharedPref.edit().putString(getString(R.string.pref_last_username), email.getText().toString()).apply();
            sharedPref.edit().putString(getString(R.string.pref_last_password), password.getText().toString()).apply();

        }else{
            sharedPref.edit().putBoolean(getString(R.string.pref_stay_logged_in), false).apply();
            sharedPref.edit().putString(getString(R.string.pref_last_username), "").apply();
            sharedPref.edit().putString(getString(R.string.pref_last_password), "").apply();
        }

        //Check if email and password match. Password cannot be left blank
        LocalDB.openDB(view.getContext());
        if(LocalDB.emailExists(email.getText().toString())) {
            User user = LocalDB.getUser(email.getText().toString());
            if(user.getPassword().equals(password.getText().toString())) {
                sharedPref.edit().putString(getString(R.string.current_user_email), user.getEmail()).apply();
                LocalDB.closeDB();
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
            }else{
                error.setVisibility(View.VISIBLE);
                sv.scrollTo(0, sv.getBottom());
                LocalDB.closeDB();
                return;
            }
        }else{
            error.setVisibility(View.VISIBLE);
            sv.scrollTo(0, sv.getBottom());
            LocalDB.closeDB();
            return;
        }

        LocalDB.closeDB();
    }

    public void createNewUser(View view){
        Intent main = new Intent(this, CreateAccount.class);
        startActivity(main);
    }
}
