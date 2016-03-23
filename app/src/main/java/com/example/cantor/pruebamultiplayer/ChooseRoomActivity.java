package com.example.cantor.pruebamultiplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChooseRoomActivity extends Activity implements Observer{

    ChatApplication batApplication = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_room);

        //Intent intent = new Intent(this, AllJoynService.class);
        //this.startService(intent);

        //Las application son raras y hacen cosas raras, mejor tener un puntero por si acaso.
        //Y hacemos un checkin para avisarle de que eh, batApplication, queremos algo de ti
        batApplication = (ChatApplication)getApplication();
        batApplication.checkin();
        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        batApplication.addObserver(this);

        final ListView batList = (ListView)findViewById(R.id.ListViewRooms);
        //Set item click listener
        //El usuario entra en la sale que tiene nombre XXX
        //Joinchannel
        batList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = batList.getItemAtPosition(position).toString();
                batApplication.useSetChannelName(name);
                batApplication.useJoinChannel();
                Intent batIntent = new Intent(ChooseRoomActivity.this, ChatRoomActivity.class);
                startActivity(batIntent);
            }
        });


    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonCreate:
                EditText batEditTextRoomName = (EditText)findViewById(R.id.EditTextRoomName);
                String batName = batEditTextRoomName.getText().toString();
                //Set channel's name
                batApplication.hostSetChannelName(batName);
                //init
                batApplication.hostInitChannel();
                //Start doesn't mean that we are entering it
                batApplication.hostStartChannel();

                break;
            //Update ListView with the names of the rooms
            case R.id.buttonRefresh:
                ListView batList = (ListView)findViewById(R.id.ListViewRooms);
                ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
                batList.setAdapter(channelListAdapter);
                //Encontramos los nombres de las salas que hemos creado
                List<String> channels = batApplication.getFoundChannels();
                for (String channel : channels) {
                    //Buscamos el ultimo punto
                    //Hace un nombre compuesto con el package y mas cosas
                    //Estan separados por puntos y despues del ultimo esta el nombre de la sala
                    int lastDot = channel.lastIndexOf('.');
                    //Si no hay punto es que no es un nombre de la sala y pasamos de ello
                    if (lastDot < 0) {
                        continue;
                    }
                    //Cogemos las siguientes posiciones despues del ultimo punto
                    channelListAdapter.add(channel.substring(lastDot + 1));
                }
                channelListAdapter.notifyDataSetChanged();
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

                    Toast toast = Toast.makeText(ChooseRoomActivity.this, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
                default:
                    break;
            }
        }
    };

}
