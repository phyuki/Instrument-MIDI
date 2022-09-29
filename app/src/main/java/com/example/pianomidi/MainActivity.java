package com.example.pianomidi;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.illposed.osc.MessageSelector;
import com.illposed.osc.OSCMessageEvent;
import com.illposed.osc.OSCMessageListener;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MidiDriver.OnMidiStartListener {

    private int myPort = 7000;
    private OSCPortIn receiver;
    protected MidiDriver midi;
    private String msgListener = "/message/piano";
    private byte toneMidi;
    private Spinner spin;
    private ImageView gif;
    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gif = (ImageView) findViewById(R.id.listening);
        Glide.with(this).load(R.drawable.listening).into(gif);

        spin = (Spinner) findViewById(R.id.spin_inst);
        String[] inst = getResources().getStringArray(R.array.instruments);
        ArrayAdapter ad = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                inst);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(ad);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(inst[i]){
                    case("Piano"):
                        Log.d("INSTRUMENT", "PIANO");
                        msgListener = "/message/piano";
                        toneMidi = GeneralMidiConstants.ACOUSTIC_GRAND_PIANO;
                        break;
                    case("Guitar"):
                        Log.d("INSTRUMENT", "GUITAR");
                        msgListener = "/message/guitar";
                        toneMidi = GeneralMidiConstants.ELECTRIC_GUITAR_JAZZ;
                        break;
                    case("Voice"):
                        Log.d("INSTRUMENT", "VOICE");
                        msgListener = "/message/voice";
                        toneMidi = GeneralMidiConstants.SYNTH_VOICE;
                        break;
                    case("Drums"):
                        Log.d("INSTRUMENT", "DRUMS");
                        msgListener = "/message/drums";
                        toneMidi = GeneralMidiConstants.SYNTH_DRUM;
                        break;
                }

                sendMidi(MidiConstants.PROGRAM_CHANGE, toneMidi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("INSTRUMENT", "PIANO");
                msgListener = "/message/piano";
                toneMidi = GeneralMidiConstants.ACOUSTIC_GRAND_PIANO;
                sendMidi(MidiConstants.PROGRAM_CHANGE, toneMidi);
            }

        });

        // Create midi driver
        midi = MidiDriver.getInstance(this);

        MidiNoteNumber midiNote = new MidiNoteNumber();

        Button btnReceive = (Button) findViewById(R.id.rcvOSC);
        Button btnStop = (Button) findViewById(R.id.stop);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isListening){
                    midi.setReverb(ReverbConstants.CHAMBER);

                    try {
                        receiver = new OSCPortIn(myPort);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    OSCMessageListener listener = new OSCMessageListener() {
                        public void acceptMessage(OSCMessageEvent event) {
                            List<Object> notes = event.getMessage().getArguments();

                            if(!notes.isEmpty()) {

                                if(notes.get(0).toString().equals("0")) {
                                    for (int i = 1; i < notes.size(); i++) {
                                        int midiNumber = midiNote.midiNoteNumber(notes.get(i).toString());
                                        Log.d("0", notes.get(1).toString());
                                        sendMidi(MidiConstants.NOTE_OFF, midiNumber, 40);
                                    }
                                }

                                else{
                                    for (int i = 1; i < notes.size(); i++) {
                                        int midiNumber = midiNote.midiNoteNumber(notes.get(i).toString());
                                        Log.d("1", notes.get(1).toString());
                                        sendMidi(MidiConstants.NOTE_ON, midiNumber, 40);
                                    }
                                }

                            }
                        }
                    };

                    MessageSelector selectorVoice = new OSCPatternAddressMessageSelector(msgListener);

                    receiver.getDispatcher().addListener(selectorVoice, listener);

                    // NOTE You might want to use this code, in case you have bundles
                    //      with time-stamps in the future, which you still want
                    //      to process immediately.
                    //receiver.getDispatcher().setAlwaysDispatchingImmediately(true);
                    Log.d("OSC", msgListener);
                    isListening = true;
                    gif.setVisibility(View.VISIBLE);
                    receiver.startListening();
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OSC", msgListener);
                gif.setVisibility(View.INVISIBLE);
                if(isListening) receiver.stopListening();
                isListening = false;
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Start midi
        if (midi != null){
            midi.start();
        }

    }

    protected void onPause()
    {
        super.onPause();

        // Stop midi

        if (midi != null)
            midi.stop();
    }

    @Override
    public void onMidiStart(){
        sendMidi(MidiConstants.PROGRAM_CHANGE, toneMidi);
    }

    protected void sendMidi(int m, int n)
    {
        byte msg[] = new byte[2];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        midi.write(msg);

    }

    protected void sendMidi(int m, int n, int v)
    {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        midi.write(msg);
    }
}