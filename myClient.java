import java.io.*;
import java.net.*;
 
public class myClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost",50000);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            
            out.write(("HELO\n").getBytes());
            System.out.println("Sent: HELO");
            
            String str = in.readLine();
            System.out.println("Received: " + str);
            
            String username = System.getProperty("Victor");
            out.write(("AUTH " + username + "\n").getBytes());
            System.out.println("Sent: AUTH");
            
            str = (String)in.readLine();
            System.out.println("Received: " + str);
            
            out.write(("REDY\n").getBytes());
            System.out.println("Sent: REDY");
 
            str = (String)in.readLine();
            System.out.println("Received: " + str);
            
            out.write(("QUIT\n").getBytes());
            System.out.println("Sent: QUIT");
            
            str = (String)in.readLine();
            System.out.println("Received: " + str);
            
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

