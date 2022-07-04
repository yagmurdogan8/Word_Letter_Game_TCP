import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

//@authors Betulnaz Hayran, Batuhan Kesikbas & Yagmur Dogan

public class client {
    public static void main(String[] args) throws IOException {

        Socket socket = null; //clientla server arasinda kopru baglanti kurmak icin
        String serverIncoming; //serverdan gelen kelime
        String clientOutgoing; //clienttan giden kelime

        ArrayList<String> words = new ArrayList<>();
        try {
            socket = new Socket("localhost", 5005);
        } catch (Exception e) {
            System.err.println("Attention: Port Error!");
        }
        //once clienti calistirinca hata aliyoduk o yuzden burayi try catche aldik
        //portlar farkli olunca da oluyordu


        //Creates a new PrintWriter, with automatic line flushing, from an existing OutputStream.
        //var olan outputstreamden otomatik bi satir flushing ile yeni bi printwriter olusturur

        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

        /*
         The getOutputStream() method of Java Socket class returns an output stream for the given socket.
         getOutputStream metodu verilen socket icin bir output streami dondurur.
        */

        Scanner sc = new Scanner(new InputStreamReader(socket.getInputStream()));
        /*
        The getInputStream() returns an input stream for reading bytes from this socket.
        socket.getInputStream reads data from the specific "client socket" you created.
        Burada bufferreader da olurdu
        */

        System.out.println("\nWrite a word and press enter to start the game!" +
                " You will have 15 seconds to respond to every word.");

        Scanner data = new Scanner(new InputStreamReader(System.in));
        //clienttan kullanicin girdigi kelimeyi okuyor

        clientOutgoing = data.nextLine(); //clienttan servera gidecek kelime okunur

        while (true) {
            if (clientOutgoing.isEmpty() || clientOutgoing.contains(" ")) {
                System.out.println("You have entered an empty string. Please enter another word.");
                clientOutgoing = data.nextLine();
            }
            else if(clientOutgoing.length() < 2){
                System.out.println("You word must at least 2 character long. Please enter another word.");
                clientOutgoing = data.nextLine();
            }

           else if (clientOutgoing.matches(".*\\d.*")) {
                System.out.println("Your word must not contain any numbers.");
                clientOutgoing = data.nextLine();
            }
           else if ( clientOutgoing.matches(".*[A-Z].*")){
                System.out.println("Your word must be in lowercase.");
                clientOutgoing = data.nextLine();
            }
           else
               break;
        }

        pw.println(clientOutgoing); //clientin yazdigi kelime serverda yazilir
        words.add(clientOutgoing); //kelimeyi arrayde tutuyor

        System.out.println("Please wait for server to respond...");

        String endOfWord;
        String beginningOfWord;
        while ((serverIncoming = sc.nextLine()) != null) {

            words.add(serverIncoming);
            if (serverIncoming.equals("Server entered a wrong word. GAME OVER. YOU WON!") ||
                    serverIncoming.equals("Server couldn't respond on time. GAME OVER. YOU WON!")) {
                System.out.println(serverIncoming);
                System.exit(0);
            }
            System.out.println("The word that comes from Server = " + serverIncoming);
            System.out.println("Enter a word that will send to Server:");

            double startTime = (double) System.currentTimeMillis();
            String clientOutgoing2 = data.nextLine();
            //2. bi clientoutgoing ekledik ki ayni kelime birden cok kez yazilmasin ve zaman sinirini kontrol edebilmek icin
            double endTime = (double) System.currentTimeMillis();
            double estimatedTime = endTime - startTime;
            double time = estimatedTime / 1000;


            if (time > 15.0) {
                System.out.println("You respond in " + time + " seconds. GAME OVER. YOU LOST!");
                pw.println("Client couldn't respond on time. YOU WON!");
                System.exit(0);
            }

            while (true) {
                if (clientOutgoing2.isEmpty() || clientOutgoing2.contains(" ")) {
                    System.out.println("You have entered an empty string. Please enter another word.");
                    clientOutgoing2 = data.nextLine();
                }
                else if(clientOutgoing2.length() < 2){
                    System.out.println("You word must at least 2 character long. Please enter another word.");
                    clientOutgoing2 = data.nextLine();
                }
                else if (clientOutgoing2.matches(".*\\d.*")) {
                    System.out.println("Your word must not contain any numbers.");
                    clientOutgoing2 = data.nextLine();
                }
                else if ( clientOutgoing2.matches(".*[A-Z].*")){
                    System.out.println("Your word must be in lowercase.");
                    clientOutgoing2 = data.nextLine();
                }
                else if(words.contains(clientOutgoing2)){
                    System.out.println("This word has been used before! Please enter another word.");
                    clientOutgoing2 = data.nextLine();
                }
                else
                    break;
            }


            endOfWord = serverIncoming.substring(serverIncoming.length() - 2);
            //kelimenin son iki harfi bellekte tutuluyor enOfWord ile
            beginningOfWord = clientOutgoing2.substring(0, 2);

            if (beginningOfWord.equals(endOfWord)) {

                System.out.println("You entered a correct word. Waiting for Server's response.");

                pw.println(clientOutgoing2); //servera gonderiyor
                words.add(clientOutgoing2); //arraye ekliyor kelimeyi

            }
            else {

                System.err.println("You entered a wrong word. GAME OVER. YOU LOST!");

                pw.println("Client entered a wrong word. GAME OVER. YOU WON!"); //bu servera yazdiriyor
                System.exit(0);

            }

        }

        /*
        The close() method of Java Socket class closes the specified socket.
        Once the socket has been closed, it is not available for further networking use.
        If the socket has a channel, the channel is also closed.
        */
        pw.close(); //If you close the returned OutputStream then it will close the linked socket.
        sc.close(); //If you close the returned InputStream, then it will close the linked socket
        data.close();
        socket.close(); //baglantiyi kesmek icin
    }
}