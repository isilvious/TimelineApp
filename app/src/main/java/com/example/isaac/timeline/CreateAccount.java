package com.example.isaac.timeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class CreateAccount extends AppCompatActivity {

    TextView error;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        sv = (ScrollView) findViewById(R.id.sv_create_account);
        error = (TextView) findViewById(R.id.tx_creation_error);
        error.setVisibility(View.INVISIBLE);
    }

    public void createAccount(View view){
        error.setVisibility(View.INVISIBLE);

        TextView email = (TextView) findViewById(R.id.edtx_new_email);
        TextView username = (TextView) findViewById(R.id.edtx_new_username);
        TextView password1 = (TextView) findViewById(R.id.extx_new_password1);
        TextView password2 = (TextView) findViewById(R.id.edtx_new_password2);

        LocalDB.openDB(view.getContext());
        if(LocalDB.emailExists(email.getText().toString())){
            error.setText("Email already in use");
            error.setVisibility(View.VISIBLE);
            sv.scrollTo(0, sv.getBottom());
            LocalDB.closeDB();
            return;
        } else if(password1.getText().toString().equals(password2.getText().toString()) == false){
            error.setText("Passwords do not match");
            error.setVisibility(View.VISIBLE);
            sv.scrollTo(0, sv.getBottom());
            LocalDB.closeDB();
            return;
        }else if(email.getText().toString().equals("") ||
                username.getText().toString().equals("") ||
                password1.getText().toString().equals("")){
            error.setText("You cannot leave any fields blank");
            error.setVisibility(View.VISIBLE);
            sv.scrollTo(0, sv.getBottom());
            LocalDB.closeDB();
            return;
        }

        LocalDB.addUser(new User(
                username.getText().toString(),
                password1.getText().toString(),
                email.getText().toString()));

        LocalDB.closeDB();
        finish();
    }
}
