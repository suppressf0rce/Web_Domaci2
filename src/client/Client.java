package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Contact;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        System.out.println("Connecting to the server...");

        try {
            Socket socket = new Socket("localhost", 6666);
            System.out.println("Connected!");

            //IO Stream
            DataInputStream dis=new DataInputStream(socket.getInputStream());
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
            Gson gson = new Gson();

            //Server welcome message
            System.out.println(dis.readUTF());

            mainLoop:
            while (true) {
                System.out.println("-------------------------------------------");
                System.out.println("Please select one of the following options:");
                System.out.println("1 - To add new contact");
                System.out.println("2 - To print out all the contacts");
                System.out.println("3 - Exit");

                Scanner scanner = new Scanner(System.in);
                int result = scanner.nextInt();

                switch (result) {
                    case 1:
                        //Add contact code
                        System.out.println("-------------------------------------------");
                        System.out.println("Please enter the first name: ");
                        scanner = new Scanner(System.in);
                        String firstName = scanner.nextLine();
                        System.out.println("Please enter the last name: ");
                        String lastName = scanner.nextLine();
                        System.out.println("Please enter the phone number: ");
                        String phoneNumber = scanner.nextLine();

                        //Creating our contact
                        Contact contact = new Contact.Builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .phoneNumber(phoneNumber).build();

                        //Informing server that we want to add contact
                        dout.writeInt(result);
                        dout.flush();

                        //Sending contact info to server
                        String contactInfo = gson.toJson(contact);
                        dout.writeUTF(contactInfo);
                        dout.flush();

                        break;

                    case 2:
                        //List Contacts code
                        System.out.println("-------------------------------------------");

                        //Informing server that we want to list out the contacts
                        dout.writeInt(result);
                        dout.flush();


                        //Reading out contact list json file and converting it to list
                        String contactList = dis.readUTF();
                        Type listType = new TypeToken<List<Contact>>() {}.getType();
                        List<Contact> addressBook = gson.fromJson(contactList, listType);

                        //Printing out contacts
                        addressBook.forEach(System.out::println);

                        break;


                    case 3:
                        //Exit code
                        System.out.println("-------------------------------------------");

                        //Informing server that we want to disconnect
                        dout.writeInt(result);
                        dout.flush();

                        //Closing socket
                        socket.close();
                        break mainLoop;

                    default:
                        break;
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
