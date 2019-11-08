package wss;

import com.google.gson.Gson;
import db.DBMain;
import db.RecordAccounts;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class WSS extends WebSocketServer {

    public WSS(int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public WSS(InetSocketAddress address ) {
        super( address );
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake ) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        broadcast( conn + " has left the room!" );
        System.out.println( conn + " has left the room!" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        //broadcast( message );
        System.out.println( conn + ": " + message );

        GsonContainer gsonContainer = new Gson().fromJson(message, GsonContainer.class);
        if (gsonContainer.type!=null && gsonContainer.type.equals("RequestAccounts")) {
            System.out.println(gsonContainer.params);

            DBMain dbMain = new DBMain("cashflow");
            ResultSet rs = dbMain.dbPostgres.executeQuery("SELECT * FROM accounts");

            List<AccountAnsRequest> accountAnsRequest = new ArrayList();
            try {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    double balance = RecordAccounts.createExists(dbMain,id).getBalance();
                    accountAnsRequest.add(new AccountAnsRequest(id,name,balance));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(new Gson().toJson(new GsonContainer("RequestAccounts", new Gson().toJson(accountAnsRequest))));



            conn.send(new Gson().toJson(new GsonContainer("RequestAccounts", new Gson().toJson(accountAnsRequest))));
        }

    }
    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        broadcast( message.array() );
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}
