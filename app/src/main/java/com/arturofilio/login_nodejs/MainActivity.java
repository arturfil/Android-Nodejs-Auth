package com.arturofilio.login_nodejs;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arturofilio.login_nodejs.Retrofit.IMyService;
import com.arturofilio.login_nodejs.Retrofit.RetrofilClient;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.Field;

public class MainActivity extends AppCompatActivity {

    TextView mCreateAccount;
    EditText mEmail, mUsername, mPassword;
    Button mButton;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Service
        Retrofit retrofitClient = RetrofilClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        // Init View
        mEmail = (EditText)findViewById(R.id.edt_email);
        mPassword = (EditText)findViewById(R.id.edt_password);
        mButton = (Button) findViewById(R.id.login_btn);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(mEmail.getText().toString(),mPassword.getText().toString());
            }
        });
        mCreateAccount = (TextView)findViewById(R.id.txt_link);
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields must be filled properly", Toast.LENGTH_SHORT).show();
            return;
        }

        compositeDisposable.add(iMyService.loginUser(email,password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    Toast.makeText(MainActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                }
            })
        );
    }
}
