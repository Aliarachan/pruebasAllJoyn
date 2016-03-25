package com.example.cantor.pruebamultiplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreateLobbyActivity extends AppCompatActivity implements Observer {

    private ChatApplication batApplication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        batApplication = (ChatApplication)getApplication();
        batApplication.checkin();
        batApplication.addObserver(this);

        Bundle b = getIntent().getExtras();
        int batId = b.getInt("tmpID");

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonConfirm:
                EditText batEditTextRoomName = (EditText)findViewById(R.id.EditTextRoomName);
                String batName = batEditTextRoomName.getText().toString();
                //Set channel's name
                batApplication.hostSetChannelName(batName);
                //init
                batApplication.hostInitChannel();
                //Start doesn't mean that we are entering it
                batApplication.hostStartChannel();
                batApplication.refreshLobbies();
                break;
        }
    }

    public synchronized void update(Observable o, Object arg) {
        String qualifier = (String)arg;

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }

    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;

    /**
     * El handler se suele definir aqui de forma implicita
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ALLJOYN_ERROR_EVENT:
                {
                    //alljoynError();
                    //We could put an error here
                    //Salta por ejemplo si intentamos crear una sala sin nombre (nombre vacio)
                    CharSequence text = "Error =(";

                    Toast toast = Toast.makeText(CreateLobbyActivity.this, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
                default:
                    break;
            }
        }
    };
}
