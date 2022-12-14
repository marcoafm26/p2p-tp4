package com.za.networking.peertopeer;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

public class Peer {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((System.in)));
        System.out.println("> Insira o username e a porta para esse peer:");
        String[] setupValues = bufferedReader.readLine().split(" ");
        ServerThread serverThread = new ServerThread(setupValues[1]);
        serverThread.start();
        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);

    }

    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception {
        System.out.println("> Insira (separado por espaço) hostname:port#");
        System.out.println(" Peer para receber mensagem de (s para pular):");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");
        if (!input.equals("s")) for (int i = 0; i < inputValues.length; i++) {
            String[] address = inputValues[i].split(":");
            Socket socket = null;
            try {
                socket = new Socket(address[0], Integer.valueOf(address[1]));
                new PeerThread(socket).start();
            }catch (Exception e){
                if (socket != null) {
                    socket.close();
                }else {
                    System.out.println("Input invalido. Indo para a proxima etapa.");
                }
            }
        }
        communicate(bufferedReader,username,serverThread);
    }

    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) {
        try{
            System.out.println(">Agora voce pode conversar (e para sair, c para mudar)");
            boolean flag = true;
            while (flag){
                String message = bufferedReader.readLine();
                if (message.equals("e")){
                    flag = false;
                    break;
                }else if (message.equals("c")){
                    updateListenToPeers(bufferedReader,username,serverThread);
                }else{
                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                            .add("username",username)
                            .add("message",message)
                            .build());
                    serverThread.sendMessage(stringWriter.toString());
                }
            }
            System.exit(0);
        } catch (Exception e ){}
    }
}