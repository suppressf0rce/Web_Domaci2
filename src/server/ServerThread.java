package server;

import com.google.gson.Gson;
import model.Contact;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerThread implements Runnable {

    private List<Contact> addressBook;
    private Socket socket;

    public ServerThread(List<Contact> addressBook, Socket socket) {
        this.addressBook = addressBook;
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            Gson gson = new Gson();

            //Sending welcome message
            dout.writeUTF("Welcome to the Address Book Server :)");
            dout.flush();

            mainLoop:
            while (true) {

                //Reading code from the client
                int result = dis.readInt();

                switch (result) {

                    case 1:
                        //Add contact code
                        String contactInfo = dis.readUTF();
                        Contact contact = gson.fromJson(contactInfo, Contact.class);
                        addressBook.add(contact);
                        System.out.println("Contact added");

                        break;

                    case 2:
                        //List contacts code
                        String contactList = gson.toJson(addressBook);
                        dout.writeUTF(contactList);
                        dout.flush();

                        break;

                    case 3:
                        //DC Code
                        System.out.println("Client disconnected");
                        socket.close();
                        break mainLoop;

                    default:
                        dout.writeUTF("Unrecognized command!");
                        dout.flush();
                        break;
                }

            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
