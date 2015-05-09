package com.mygdx.game;

/**
 * Created by alecorleonis on 05-07-15.
 * from https://javadeveloperszone.wordpress.com/2013/04/20/java-tcp-chat-multiple-client/
 */
import java.net.*;
import java.io.*;

public class chatClient{
    static Socket clientSocket;
    public static void main(String argv[]) throws Exception {
        BufferedReader inFromUser =
                new BufferedReader(
                        new InputStreamReader(System.in));

        //System.out.println("Iniciando socket.");
        //System.out.flush();
        clientSocket = new Socket("localhost", 6789);

        //System.out.println("Iniciando server listener.");
        //System.out.flush();
        Thread t = new Thread(new MyServerListener());
        t.start();

        DataOutputStream outToServer =
                new DataOutputStream(
                        clientSocket.getOutputStream());

        System.out.println("Conectado.");
        System.out.flush();

        while (true) {
            //System.out.println("Ingrese una cadena a enviar: ");
            //System.out.flush();

            String mensaje = inFromUser.readLine();
            outToServer.writeBytes(mensaje + '\n');
            outToServer.flush();
            if (mensaje.equals("EXIT")) {
                break;
            }
        }
        clientSocket.close();
    }
}

class MyServerListener implements Runnable {
    @Override
    public void run() {
        BufferedReader inFromServer  = null;
        try {
            inFromServer = new BufferedReader(
                    new InputStreamReader(
                            chatClient.clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                //System.out.println("Esperando mensaje del server.");
                //System.out.flush();

                String mensaje_del_server = inFromServer.readLine();
                System.out.println("Recibido: " + mensaje_del_server);
                System.out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}