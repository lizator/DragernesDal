package com.rbyte.dragernesdal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocalDateToStringTest {
    @Test
    public void dateIsCorrect(){
        String correctDate = "01-14-2021 16:20:00";

        char[] dateStartArray = "2021-01-14T16:20".toCharArray();
        char[] tempStartArray = "2021-01-14T16:20".toCharArray();
        for(int i = 0; i < dateStartArray.length; i++) {
            if(dateStartArray[i] == 'T') tempStartArray[i] = ' ';
            if(i < 4) tempStartArray[i+6]=dateStartArray[i];
            if(i > 4 && i < 7){
                tempStartArray[i-5] = dateStartArray[i];
            }
            if(i>7 && i < 10){
                if(i == 8){
                    tempStartArray[i-6] = '-';
                    tempStartArray[i-5] = dateStartArray[i];
                }
                else {
                    tempStartArray[i-5] = dateStartArray[i];
                    tempStartArray[i-4] = '-';
                }
            }

        } String startDateString = new String(tempStartArray);
        startDateString = startDateString+":00";
        System.out.println(startDateString);
        //System.out.println(startDateString);
    }
}
