package com.example.cantor.pruebamultiplayer;

/**
 * Created by Cantor on 28/03/2016.
 */
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface (name = "com.example.cantor.pruebamultiplayer")
public interface UserInterface {
    /*
     * The BusSignal annotation signifies that this function should be used as
     * part of the AllJoyn interface.  The runtime is smart enough to figure
     * out that this is a used as a signal emitter and is only called to send
     * signals and not to receive signals.
     */
    @BusSignal
    public void Chat(String str) throws BusException;

    @BusSignal
    public void sendInfoServer(Object obj) throws BusException;

    @BusSignal
    public void receiveInfoServer(Object obj) throws BusException;

    @BusProperty(annotation = BusProperty.ANNOTATE_EMIT_CHANGED_SIGNAL)
    public String getName() throws BusException;

    @BusProperty
    public void setName(String name) throws BusException;

    @BusSignal
    public void forceDisconnect() throws BusException;

    @BusSignal
    public void sendMessageRefreshPlayers() throws BusException;
}
