import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class server {

    public static void main(String[] args) throws IOException, NullPointerException {

        String clientIncoming; //clienttan gelen kelime
        ServerSocket serverSocket = null;
        Socket clientSocket;
        ArrayList<String> wordsMirror = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(5005);
        } catch (Exception e) {
            System.err.println("Attention: Port Error!");
            //server ve clientin port numaralari ayni degilse hata aliyoruz
        }


        System.out.println("\nServer is waiting for Client for connection....");
        System.out.println("You will have 15 seconds to respond to every word.");

        clientSocket = Objects.requireNonNull(serverSocket).accept();
        /*
        The accept() method of ServerSocket class is used to accept the incoming request to the socket.
        To complete the request, the security manager checks the host address, port number, and localport.
         */


        //Creates a new PrintWriter, with automatic line flushing, from an existing OutputStream.
        //var olan outputstreamden otomatik bi satir flushing ile yeni bi printwriter olusturur
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        /*
         The getOutputStream() method of Java Socket class returns an output stream for the given socket.
         getOutputStream metodu verilen socket icin bir output streami dondurur.
        */

        Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
         /*
        The getInputStream() returns an input stream for reading bytes from this socket.
        socket.getInputStream reads data from the specific "client socket" you created.
        Burada bufferreader da olurdu
        */

        Scanner data = new Scanner(new InputStreamReader(System.in));
        //serverdan kullanicinin girdigi kelimeyi okuyor

        while ((clientIncoming = in.nextLine()) != null) {

            String endOfWord;
            String beginningOfWord;

            wordsMirror.add(clientIncoming);

            if (clientIncoming.equals("Client entered a wrong word. GAME OVER. YOU WON!") || clientIncoming.equals("Client couldn't respond on time. YOU WON!")) {
                System.out.println(clientIncoming);
                System.exit(0);
            }

            System.out.println("The word that comes from Client = " + clientIncoming);
            System.out.println("Enter a word that will send to Client:");

            double startTime = (double) System.currentTimeMillis();
            String serverOutgoing = data.nextLine();
            double endTime = (double) System.currentTimeMillis();
            double estimatedTime = endTime - startTime;
            double time = estimatedTime / 1000;

            if (time > 15) {

                System.out.println("You respond in " + time + " seconds. GAME OVER. YOU LOST!");
                out.println("Server couldn't respond on time. GAME OVER. YOU WON!");
                System.exit(0);

            }

            while (true){
                if (serverOutgoing.isEmpty() || serverOutgoing.contains(" ")) {
                    System.out.println("You have entered an empty string. Please enter another word.");
                    serverOutgoing = data.nextLine();
                }
                else if(serverOutgoing.length() < 2){
                    System.out.println("You word must at least 2 character long. Please enter another word.");
                    serverOutgoing = data.nextLine();
                }

                else if (serverOutgoing.matches(".*\\d.*")) {
                    System.out.println("Your word must not contain any numbers.");
                    serverOutgoing = data.nextLine();
                }
                else if ( serverOutgoing.matches(".*[A-Z].*")){
                    System.out.println("Your word must be in lowercase.");
                    serverOutgoing = data.nextLine();
                }
                else if (wordsMirror.contains(serverOutgoing)) {
                    System.out.println("This word has been used before! Please enter another word.");
                    serverOutgoing = data.nextLine();
                }
                else
                    break;
            }

            endOfWord = clientIncoming.substring(clientIncoming.length() - 2); //kelimenin son iki harfi
            beginningOfWord = serverOutgoing.substring(0, 2); //kelimenin ilk iki harfi

            if (endOfWord.equals(beginningOfWord)) {

                System.out.println("You entered a correct word. Waiting for Client's response.");

                out.println(serverOutgoing); //clientin ekranina serverin girdigi kelimeyi yazdirir
                wordsMirror.add(serverOutgoing); //serverin girdigi kelimeyi arraylistte tutar
            }
            else {

                System.err.println("You entered a wrong word. GAME OVER. YOU LOST!");
                out.println("Server entered a wrong word. GAME OVER. YOU WON!");

                System.exit(0);

            }
        }

        out.close(); //If you close the returned OutputStream then it will close the linked socket.
        in.close(); //If you close the returned InputStream, then it will close the linked socket
        data.close();
        clientSocket.close(); //baglantiyi keser
        serverSocket.close(); //baglantiyi keser



    }
}