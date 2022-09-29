package com.example.pianomidi;

public class MidiNoteNumber {

    public MidiNoteNumber(){}

    public int midiNoteNumber(String note){
        int pitch = 0, noteNumber = 0, accident = 0;
        int length = note.length();

        switch(note.charAt(0)){
            case('D'):
                noteNumber = 2;
                break;
            case('E'):
                noteNumber = 4;
                break;
            case('F'):
                noteNumber = 5;
                break;
            case('G'):
                noteNumber = 7;
                break;
            case('A'):
                noteNumber = 9;
                break;
            case('B'):
                noteNumber = 11;
                break;
        }

        if(length == 2) pitch = Integer.parseInt(note.substring(1,2));
        else pitch = Integer.parseInt(note.substring(2,3));

        if(length == 3){
            char accidentChar = note.charAt(1);
            if(accidentChar == '#') accident = 1;
            else accident = -1;
        }

        return noteNumber + accident + (pitch+1)*12;
    }

}
