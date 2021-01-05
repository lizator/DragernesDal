package com.rbyte.dragernesdal.data.ability;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbilityRepositoryTest {

    @Test
    public void getRaceAbilities() {
        AbilityRepository repo = AbilityRepository.getInstance();
        Result<List<AbilityDTO>> res = repo.getTypeAbilities("kamp");
        assertTrue(true);
    }
}