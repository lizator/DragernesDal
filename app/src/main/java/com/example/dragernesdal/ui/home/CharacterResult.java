package com.example.dragernesdal.ui.home;

import androidx.annotation.Nullable;

import com.example.dragernesdal.data.character.model.CharacterDTO;

public class CharacterResult {
    @Nullable
    private CharacterDTO success;
    @Nullable
    private Integer error;

    CharacterResult(@Nullable Integer error) {
        this.error = error;
    }

    CharacterResult(@Nullable CharacterDTO success) {
        this.success = success;
    }

    @Nullable
    CharacterDTO getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
