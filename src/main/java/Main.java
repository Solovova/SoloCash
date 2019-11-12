import db.DBMain;
import db.RecordMoves;
import wss.WSS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main( String[] args ) throws InterruptedException , IOException {
        WSS s = new WSS( 8887 );
        s.start();
        System.out.println( "ChatServer started on port: " + s.getPort() );

        BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
        while ( true ) {
            String in = sysin.readLine();
            s.broadcast( in );
            if( in.equals( "exit" ) ) {
                s.stop(1000);
                break;
            }
        }
    }
}
