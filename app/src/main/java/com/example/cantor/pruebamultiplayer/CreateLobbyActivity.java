package com.example.cantor.pruebamultiplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class CreateLobbyActivity extends AppCompatActivity implements Observer {

    private ChatApplication batApplication = null;
    private int batId;
    private boolean confirmed = false;
    private EditText batEditTextRoomName;
    private  Button batButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        batApplication = (ChatApplication)getApplication();
        batApplication.checkin();
        batApplication.addObserver(this);

        batEditTextRoomName = (EditText) findViewById(R.id.editTextLobbyName);
        batButton = (Button)findViewById(R.id.buttonConfirm);

        Bundle b = getIntent().getExtras();
        batId = b.getInt("tmpID");
        if (batId == 1){
            String batName = b.getString("roomName");
            confirmedRoom(batName);
            batButton.setFocusable(false);
        }

        batApplication.setHandler(mHandler);
        //Para cuando entra un usuario y ya hay otros antes:
        refreshUsersList();

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonConfirm:
                if (!confirmed) {
                    String batName = batEditTextRoomName.getText().toString();
                    //Set channel's name
                    batApplication.hostSetChannelName(batName);
                    //init
                    batApplication.hostInitChannel();
                    //Start doesn't mean that we are entering it
                    batApplication.hostStartChannel();
                    batApplication.refreshLobbies();
                    //Automatically join the channel
                    batApplication.useSetChannelName(batName);
                    batApplication.useJoinChannel();
                    confirmedRoom(batName);
                } else{
                    //Intent hacia el gameplay
                }

                break;
        }
    }

    private void confirmedRoom(String roomName){
        confirmed = true;
        batEditTextRoomName.setFocusable(false);
        batEditTextRoomName.setText(roomName);
        batButton.setText("PlayStart!");
    }

    public void onBackPressed(){

    }

    private void goBack(){
        if (batId == 0){
            //batApplication.closeRoom();
        }
        this.finish();
    }

    public synchronized void update(Observable o, Object arg) {
        String qualifier = (String)arg;

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }

    private void refreshUsersList(){
        ListView batList = (ListView)findViewById(R.id.listViewPlayers);
        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        batList.setAdapter(channelListAdapter);
        //Encontramos los nombres de los users que han entrado
        List<String> names = batApplication.getUsersName();
        for (String name : names) {
            channelListAdapter.add(name);
        }
        channelListAdapter.notifyDataSetChanged();
    }

    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;
    private static final int HANDLE_JOINED_MEMBER = 3;

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
                    CharSequence text = "Error create";

                    Toast toast = Toast.makeText(CreateLobbyActivity.this, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
                case HANDLE_JOINED_MEMBER:
                    refreshUsersList();
                default:
                    break;
            }
        }
    };
}
