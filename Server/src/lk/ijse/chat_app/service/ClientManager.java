package lk.ijse.chat_app.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClientManager implements Runnable{
    public static ArrayList<ClientManager>clientManagers=new ArrayList<>();
    private Socket localSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String clientUserName;

    public ClientManager(Socket localSocket) {
        try {
            this.localSocket=localSocket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(localSocket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(localSocket.getInputStream()));

            this.dataOutputStream=new DataOutputStream(localSocket.getOutputStream());
            this.dataInputStream=new DataInputStream(localSocket.getInputStream());
            System.out.println("**************");
            this.clientUserName=bufferedReader.readLine();
            System.out.println(clientUserName);
            clientManagers.add(this);
            broadCastMessage("* "+clientUserName+" has been connected");
        }catch (IOException e){
            close();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String receivedMessage;

        while (localSocket.isConnected()){
            try {
                /**Blocking operation*/
                receivedMessage=dataInputStream.readUTF();

                System.out.println("server : "+receivedMessage);

                if (receivedMessage.equals("iMg*->")){
                    System.out.println("image");
                    String username=dataInputStream.readUTF();

                    byte[] sizeAr = new byte[256];
                    dataInputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                    System.out.println("size := "+size);

                    byte[] imageAr = new byte[size];
                    dataInputStream.read(imageAr);

                    broadCastImage(sizeAr,imageAr,username);



                }else if(receivedMessage.equalsIgnoreCase("left")){
                    System.out.println("remove");
                    removeClientManager();
                }else {
                    System.out.println("text");
                    broadCastMessage(receivedMessage);
                }
            }catch (IOException e){
                e.printStackTrace();
                close();
                break;
            }
        }
    }

    private void broadCastImage(byte[] size,byte[] imageContent,String userName){
        for (ClientManager client : clientManagers) {
            try{
                if(!this.clientUserName.equals(client.clientUserName)){
                    BufferedOutputStream bufferedOutputStream=
                            new BufferedOutputStream(client.localSocket.getOutputStream());

                    client.dataOutputStream.writeUTF("iMg*->");
                    client.dataOutputStream.writeUTF(userName);

                    client.dataOutputStream.write(size);
                    client.dataOutputStream.write(imageContent);
                    client.dataOutputStream.flush();

                }
                System.out.println("img sent to client");
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Can't broadcast image");
                close();
            }
        }
    }

    private void broadCastMessage(String sentMessage){
        for (ClientManager client : clientManagers) {
            try{
                if(!this.clientUserName.equals(client.clientUserName)){

                    client.dataOutputStream.writeUTF(sentMessage);
                    client.dataOutputStream.flush();
                }
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Can't broadcast message");
                close();
            }
        }
    }

    public void removeClientManager(){
        clientManagers.remove(this);
        broadCastMessage("* "+clientUserName+" has been left");
    }


    private void close(){
        try {
            if (localSocket!=null){
                localSocket.close();
            }
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
            if (bufferedReader!=null){
                bufferedReader.close();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
