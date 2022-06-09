package com.cloud;



import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class servHandler implements Runnable {

   BufferedInputStream inputStream ;
   BufferedOutputStream outputStream;
   List<String> serverFilesList;


    public servHandler(Socket socket) throws IOException {
        inputStream= new BufferedInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        serverFilesList = new ArrayList<>();
    }


    public void run() {
            try {

                DataInputStream din =new DataInputStream(inputStream);
                while(true){

                     String command = din.readUTF();
                     if (command.equals("/REFRESH")){
                         refresh();
                     }
                     if (command.startsWith("/SEND")){
                         String[] params = command.split(" ");
                         resiveFiles(Long.parseLong(params[1],10),params[2]);
                     }

                }

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Хрень какая то");
            }






    }

    private void resiveFiles(long kbBytes,String filename){
        byte[] b = new byte[1024];
        try{
            DataInputStream din = new DataInputStream(inputStream);
            String path = String.format("./server/serverDirectory/"+filename);
            File f= new File(path);
            if (f.exists()) {
                boolean isOriginal = false;
                int copynum=0;
                while (!isOriginal) {
                    path = String.format("./server/serverDirectory/" + copynum + filename);
                    f = new File(path);
                    if (!f.exists()) {
                        isOriginal = true;
                    }
                    copynum++;
                }
            }


            RandomAccessFile fw =new RandomAccessFile(f,"rw");
            int n;
            for (long i = 0; i <kbBytes ; i++) {
                n = din.read(b);
                fw.write(b,0,n);
                fw.skipBytes(n);

            }
            System.out.println("мы здесь");
            fw.close();



            DataOutputStream dout =new DataOutputStream(outputStream);
            dout.writeUTF("/OK");
            dout.flush();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void refresh(){
        getFilesList();
        //List<String> outList = new ArrayList<>(Arrays.asList("test","test","test","test"));
        try{
            ObjectOutputStream oout = new ObjectOutputStream(outputStream);
//            ByteArrayOutputStream ois = new ByteArrayOutputStream();
            //InputStream ois = new ObjectInputStream(outList);
            oout.writeObject(serverFilesList);
            oout.flush();
            //oout.close();

         /*   while (true){


            }*/

        }catch (Exception e){


        }
    }


    public void getFilesList() {

        try {
            File folder = new File("./server/serverDirectory");
            System.out.println(Arrays.asList(folder.list()));
            String[] files = folder.list();
            if (!serverFilesList.isEmpty()) {
                serverFilesList.clear();
            }
            for (String fileName : files) {
                serverFilesList.add(fileName);
            }

        } catch (NullPointerException e) {

            System.out.println("Каталог пользователя не найден");
        }


    }
}
