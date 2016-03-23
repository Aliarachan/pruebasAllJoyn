package com.example.cantor.pruebamultiplayer;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class ChatRoomActivity extends Activity implements Observer{

    private ChatApplication batApplication = null;
    private ArrayAdapter<String> batAdapter;
    private Button batButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //Las application son raras y hacen cosas raras, mejor tener un puntero por si acaso.
        //Y hacemos un checkin para avisarle de que eh, batApplication, queremos algo de ti
        batApplication = (ChatApplication)getApplication();
        batApplication.checkin();
        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        batApplication.addObserver(this);


        batAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        ListView batMessages = (ListView) findViewById(R.id.ListViewMessages);
        batMessages.setAdapter(batAdapter);
        //cuando hacemos click en el boton
        batButton = (Button)findViewById(R.id.buttonSend);
        batButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //cogemos el texto
                EditText batEditText = (EditText)findViewById(R.id.EditTextMessage);
                String batString = batEditText.getText().toString();
                //Lo convertimos en mensaje
                batApplication.newLocalUserMessage(batString);
                //Limpiamos el editText
                batEditText.setText("");
            }
        });


    }

    public synchronized void update(Observable o, Object arg) {
        String qualifier = (String)arg;

        if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }

    //Añadimos el ultimo mensaje a la listview
    private void updateHistory() {
        //batAdapter.clear();
        List<String> messages = batApplication.getHistory();
        batAdapter.add(messages.get(messages.size() - 1));
        batAdapter.notifyDataSetChanged();
    }

    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_APPLICATION_QUIT_EVENT:
                {
                    finish();
                }
                break;
                case HANDLE_HISTORY_CHANGED_EVENT:
                {
                    updateHistory();
                    break;
                }
                case HANDLE_ALLJOYN_ERROR_EVENT:
                {
                    //alljoynError();
                    CharSequence text = "Error =(";
                    Toast toast = Toast.makeText(ChatRoomActivity.this, text, Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                default:
                    break;
            }
        }
    };
}
