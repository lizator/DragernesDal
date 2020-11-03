package com.example.dragernesdal.ui.usercreation;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.user.model.ProfileDTO;

public class CreateUserActivity extends AppCompatActivity {
    private CreateUserViewModel createUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        createUserViewModel = new CreateUserViewModel();
        //https://stackoverflow.com/questions/2479504/forcing-the-soft-keyboard-open - Opens keyboard on create. RequestFocus in activity_create_user.xml
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        final EditText firstnameEditText = findViewById(R.id.firstname);
        final EditText lastnameEditText = findViewById(R.id.lastname);
        final EditText usernameEditText = findViewById(R.id.create_username);
        final EditText passwordEditText = findViewById(R.id.create_password);
        final EditText passwordConfirmEditText = findViewById(R.id.passwordConfirm);
        final EditText phoneEditText = findViewById(R.id.phone);
        final CheckBox checkBox = findViewById(R.id.termsConfirm);
        final Button createButton = findViewById(R.id.create);

        createUserViewModel.getCreateUserFormState().observe(this, new Observer<CreateUserFormState>() {
            @Override
            public void onChanged(@Nullable CreateUserFormState createUserFormState) {
                if (createUserFormState == null) {
                    return;
                }
                createButton.setEnabled(createUserFormState.isDataValid());
                if (createUserFormState.getFirstNameError() != null) {
                    firstnameEditText.setError(getString(createUserFormState.getFirstNameError()));
                }
                if (createUserFormState.getLastNameError() != null) {
                    lastnameEditText.setError(getString(createUserFormState.getLastNameError()));
                }
                if (createUserFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(createUserFormState.getUsernameError()));
                }
                if (createUserFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(createUserFormState.getPasswordError()));
                }
                if (createUserFormState.getRepeatPasswordError() != null) {
                    passwordConfirmEditText.setError(getString(createUserFormState.getRepeatPasswordError()));
                }
                if (createUserFormState.getPhoneError() != null) {
                    phoneEditText.setError(getString(createUserFormState.getPhoneError()));
                }
                setResult(Activity.RESULT_OK);
            }
        });

        createUserViewModel.getStatus().observe(this, new Observer<CreateUserResult>() {

            @Override
            public void onChanged(@Nullable CreateUserResult result) {
                if (result == null) {
                    return;
                }
                if (result.getError() != null) {
                    showCreateFailed(result.getError());
                }
                if (result.getSuccess() != null) {
                    createSuccess(result.getSuccess());
                }
                setResult(Activity.RESULT_OK);
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
                int phonenumber = 0;
                String phone = phoneEditText.getText().toString();
                if (!phone.equals("")){
                    phonenumber = Integer.parseInt(phone);
                }
                createUserViewModel.createUserDataChanged(
                        firstnameEditText.getText().toString(),
                        lastnameEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordConfirmEditText.getText().toString(),
                        phonenumber,
                        checkBox.isChecked()
                );
            }
        };
        firstnameEditText.addTextChangedListener(afterTextChangedListener);
        lastnameEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmEditText.addTextChangedListener(afterTextChangedListener);
        phoneEditText.addTextChangedListener(afterTextChangedListener);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int phonenumber = 0;
                String phone = phoneEditText.getText().toString();
                if (!phone.equals("")){
                    phonenumber = Integer.parseInt(phone);
                }
                createUserViewModel.createUserDataChanged(
                        firstnameEditText.getText().toString(),
                        lastnameEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordConfirmEditText.getText().toString(),
                        phonenumber,
                        isChecked);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                    /*loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());*/
                createThread thread = new createThread(
                        firstnameEditText.getText().toString(),
                        lastnameEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        Integer.parseInt(phoneEditText.getText().toString()),
                        createUserViewModel);
                thread.start();
            }
        });

    }

    class createThread extends Thread {
        private ProfileDTO user;
        private CreateUserViewModel vm;

        public createThread(String firstName, String lastName,
                            String email, String pass,
                            int phone, CreateUserViewModel vm) {
            this.user = new ProfileDTO(0, firstName, lastName, email, phone, pass, "");
            this.vm = vm;
        }

        @Override
        public void run() {
            vm.createUser(user);
        }
    }

    private void createSuccess(ProfileDTO user) {
        String msg = getText(R.string.usercreation_success).toString();
        Toast.makeText(getApplicationContext(), String.format(msg, user.getEmail()), Toast.LENGTH_SHORT).show();
        finish();
    }


    private void showCreateFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}