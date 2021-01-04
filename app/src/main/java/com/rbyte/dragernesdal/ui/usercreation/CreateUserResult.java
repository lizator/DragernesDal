package com.rbyte.dragernesdal.ui.usercreation;

import androidx.annotation.Nullable;

import com.rbyte.dragernesdal.data.user.model.ProfileDTO;

public class CreateUserResult {
    @Nullable
    private ProfileDTO success;
    @Nullable
    private Integer error;


    public CreateUserResult(@Nullable Integer error) {
        this.error = error;
    }

    public CreateUserResult(@Nullable ProfileDTO user) {
        this.success = user;
    }

    @Nullable
    public ProfileDTO getSuccess() {
        return this.success;
    }

    @Nullable
    public Integer getError() {
        return this.error;
    }
}
