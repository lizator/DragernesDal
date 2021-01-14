package com.rbyte.dragernesdal.data;

public class StringTimeFormatter {
    public StringTimeFormatter(){

    }
    public String format(String s){
        char[] dateStartArray = s.toCharArray();
        char[] tempStartArray = s.toCharArray();
        for(int i = 0; i < dateStartArray.length; i++) {
            if(dateStartArray[i] == 'T') tempStartArray[i] = ' ';
            if(i < 4) tempStartArray[i+6]=dateStartArray[i];

            if(i>7 && i < 10){ //Day
                if(i == 8){
                    tempStartArray[i-6] = '-'; //Seperate day with month
                    tempStartArray[i-8] = dateStartArray[i];
                }
                else {
                    tempStartArray[i-8] = dateStartArray[i];
                    tempStartArray[i-4] = '-'; //Seperate month with year
                }
            }
            if(i > 4 && i < 7){ //Month
                tempStartArray[i-2] = dateStartArray[i];
            }

        } String startDateString = new String(tempStartArray);
        startDateString = startDateString+":00";
        return startDateString;
    }

    public boolean equalDate(String s1, String s2){
        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();
        for(int i = 0; i<c1.length;i++){
            if(c1[i] == ' ') break;
            if(c1[i] != c2[i]) return false;
        }
        return true;
    }

    public String getDate(String s){
        char[] c1 = s.toCharArray();
        char[] c2 = "01-01-2020".toCharArray();
        for(int i = 0; i < c2.length; i++){
            c2[i] = c1[i];
        }
        return new String(c2);
    }

    public String getTime(String s){
        char[] c1 = s.toCharArray();
        char[] c2 = "00:00:00".toCharArray();
        boolean t = false;
        int j = 0;
        for(int i = 0; i < c1.length; i++){
            if(t){
                c2[j]=c1[i];
                j++;
            }
            else if(c1[i] == 'T') t = true;
        }
        return new String(c2);
    }
}
