package com.example.cantor.pruebamultiplayer;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class JoinLobbyActivity extends AppCompatActivity implements Observer{

    private ChatApplication batApplication = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);

        batApplication = (ChatApplication)getApplication();
        batApplication.checkin();
        batApplication.addObserver(this);

        final ListView batList = (ListView)findViewById(R.id.listViewLobbies);
        //Set item click listener
        //El usuario entra en la sale que tiene nombre XXX
        //Joinchannel
        batApplication.setHandler(mHandler);
        batList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = batList.getItemAtPosition(position).toString();
                batApplication.useSetChannelName(name);
                batApplication.useJoinChannel();
                Intent batIntent = new Intent(JoinLobbyActivity.this, CreateLobbyActivity.class);
                batIntent.putExtra("tmpID", 1);
                batIntent.putExtra("roomName", name);
                startActivity(batIntent);
            }
        });
        //A lo mejor al entrar ya hay salas creadas
        updateLobbies();

    }

    public synchronized void update(Observable o, Object arg) {
        String qualifier = (String)arg;

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }

        if(qualifier.equals(ChatApplication.REFRESH_LOBBIES)){
            Message message = mHandler.obtainMessage(HANDLE_REFRESH_LOBBIES);
            mHandler.sendMessage(message);
        }

    }

    private void updateLobbies() {
        ListView batList = (ListView)findViewById(R.id.listViewLobbies);
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
    }


    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;
    private static final int HANDLE_REFRESH_LOBBIES = 7;

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
                    CharSequence text = "Error join";

                    Toast toast = Toast.makeText(JoinLobbyActivity.this, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
                case HANDLE_REFRESH_LOBBIES:
                    updateLobbies();
                default:
                    break;
            }
        }
    };

}
