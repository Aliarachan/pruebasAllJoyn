package com.example.cantor.pruebamultiplayer;

import android.os.Handler;
import android.os.Message;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

/**
 * Created by Cantor on 28/03/2016.
 */
public class User implements UserInterface, BusObject{
    private String name;
    @Override
    public void Chat(String str) throws BusException {

    }

    @Override
    public void sendInfoServer(Object obj) throws BusException {

    }

    @Override
    public void receiveInfoServer(Object obj) throws BusException {

    }

    @Override
    public String getName() throws BusException {
        return name;
    }

    @Override
    public void setName(String name) throws BusException {

    }

    @Override
    public void forceDisconnect() throws BusException {
    }

    private static final int HANDLE_JOINED_MEMBER = 3;

    @Override
    public void sendMessageRefreshPlayers() throws BusException {
        Handler handler = ChatApplication.getHandler();
        Message msg = handler.obtainMessage(HANDLE_JOINED_MEMBER);
        handler.sendMessage(msg);
    }

}
