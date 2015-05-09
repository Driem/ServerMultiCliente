package com.mygdx.game;

/**
 * Created by alecorleonis on 05-07-15.
 * from https://javadeveloperszone.wordpress.com/2013/04/20/java-tcp-chat-multiple-client/
 */
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class chatServer {

    static ArrayList<Socket> client_sockets;

    public static void main(String argv[]) throws Exception {

        client_sockets = new ArrayList<Socket>();

        ServerSocket server_socket = new ServerSocket(6789);

        Responder responder = new Responder();
        while (true)
        {
            System.out.println("Esperando que se conecte un cliente.");
            System.out.flush();
            Socket client_socket = server_socket.accept();
            System.out.println("Se ha conectado un cliente.");
            System.out.flush();
            client_sockets.add(client_socket);

            //System.out.println("Iniciando thread de servidor..");
            //System.out.flush();
            Thread t = new Thread(new MyServer(responder, client_socket));
            t.start();

        }
    }
}

class MyServer implements Runnable {

    Responder responder;
    Socket client_socket;

    public MyServer(Responder responder, Socket client_socket) {
        this.responder = responder;
        this.client_socket = client_socket;
    }

    @Override
    public void run()
    {
        BufferedReader inFromClient =
                null;
        try {
            inFromClient = new BufferedReader(
                    new InputStreamReader(
                            client_socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (responder.responderMethod(inFromClient)) {
            try {
                //System.out.println("Durmiendo 2s.");
                //System.out.flush();
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            client_socket.close();
        } catch (IOException ex) {
        }
    }

}

class Responder {

    synchronized public boolean responderMethod(BufferedReader inFromClient) {
        try {

            if(!inFromClient.ready())
            {
                return true;
            }


            String clientSentence = inFromClient.readLine();

            if (clientSentence == null) {
                return false;
            }

            System.out.println("Recibido: " + clientSentence);
            //System.out.flush();

            for(int i=0; i<chatServer.client_sockets.size();i++)
            {
                //System.out.println("Enviando a cliente #" + i);
                DataOutputStream data_output_stream =
                        new DataOutputStream(
                                chatServer.client_sockets.get(i).getOutputStream());
                data_output_stream.writeBytes(clientSentence +"\n");
                data_output_stream.flush();
            }
            return true;

        } catch (SocketException e) {
            System.out.println("Disconnected");
            System.out.flush();System.out.flush();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}