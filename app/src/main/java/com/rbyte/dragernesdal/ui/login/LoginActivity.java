package com.rbyte.dragernesdal.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.ui.main.MainActivity;
import com.rbyte.dragernesdal.ui.usercreation.*;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private LoginHandler loginHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) { //main
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText usernameEditText = findViewById(R.id.create_username);
        final EditText passwordEditText = findViewById(R.id.create_password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final TextView createUserTV = findViewById(R.id.tv_create_profile);
        final TextView forgotPassTV = findViewById(R.id.tv_forgot_password);
        loginHandler = new LoginHandler(getApplicationContext());
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    loginHandler.setUser(loginResult.getSuccess().getEmail(), loginResult.getSuccess().getPassHash());
                    updateUiWithUser(loginResult.getSuccess());
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    myIntent.putExtra("username",loginResult.getSuccess().getDisplayName());
                    myIntent.putExtra("email",loginResult.getSuccess().getEmail());
                    myIntent.putExtra("id",loginResult.getSuccess().getId()+"");
                    startActivity(myIntent);
                    finish();
                }
                setResult(Activity.RESULT_OK);
            }
        });

        //Checks to see if logging out
        Intent intent = getIntent();
        if (intent.getBooleanExtra(getString(R.string.logout_command), false)) loginHandler.clear();

        createUserTV.setOnClickListener(new View.OnClickListener() { //linking to create user activity
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, CreateUserActivity.class);
                startActivity(myIntent);
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));

                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                        /*loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());*/
                    LoginThread thread = new LoginThread(usernameEditText.getText().toString(), passwordEditText.getText().toString(), loginViewModel, false);
                    thread.start();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                    /*loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());*/
                LoginThread thread = new LoginThread(usernameEditText.getText().toString(), passwordEditText.getText().toString(), loginViewModel, false);
                thread.start();
            }
        });


        if (loginHandler.getEmail().length() == 0) {
            //all should be set before this is checked, if an error happens in autologin all is still useable
            //TODO implement buttons should start not pressable and be set pressable here and in loginformstate.observer
        } else {
            loadingProgressBar.setVisibility(View.VISIBLE);
                    /*loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());*/
            LoginThread thread = new LoginThread(loginHandler.getEmail(), loginHandler.getPasshash(), loginViewModel, true);
            thread.start();
        }
    }



    class LoginThread extends Thread {
        private String email;
        private String pass;
        private LoginViewModel vm;
        private boolean autologin;

        public LoginThread(String email, String pass, LoginViewModel vm, boolean autologin) {
            this.email = email;
            this.pass = pass;
            this.vm = vm;
            this.autologin = autologin;
        }

        @Override
        public void run() {
            vm.login(email, pass, autologin);
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = String.format(getString(R.string.welcome), model.getDisplayName());
        // TODO : initiate successful logged in experience
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}