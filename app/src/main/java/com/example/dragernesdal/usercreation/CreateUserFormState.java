package com.example.dragernesdal.usercreation;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class CreateUserFormState {
    @Nullable
    private Integer firstNameError;
    @Nullable
    private Integer lastNameError;
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer repeatPasswordError;
    @Nullable
    private Integer phoneError;
    @Nullable
    private Integer checkboxError;
    private boolean isDataValid;

    CreateUserFormState(@Nullable Integer firstNameError, @Nullable Integer lastNameError,
                        @Nullable Integer usernameError, @Nullable Integer passwordError,
                        @Nullable Integer repeatPasswordError, @Nullable Integer phoneError,
                        @Nullable Integer checkboxError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.repeatPasswordError = repeatPasswordError;
        this.phoneError = phoneError;
        this.checkboxError = checkboxError;
        this.isDataValid = false;
    }

    CreateUserFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.lastNameError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.repeatPasswordError = null;
        this.phoneError = null;
        this.checkboxError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    Integer getLastNameError() {
        return lastNameError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getRepeatPasswordError() {
        return repeatPasswordError;
    }

    @Nullable
    Integer getPhoneError() {
        return phoneError;
    }

    @Nullable
    Integer getCheckboxError() {
        return checkboxError;
    }


    boolean isDataValid() {
        return isDataValid;
    }
}
