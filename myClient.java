import java.io.*;
import java.net.*;
import java.util.*;

public class myClient {
    public static void main(String[] args) {
        try {
            //Init
            Socket s = new Socket("localhost", 50000);
            //DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            //Init Handshake
            out.write(("HELO").getBytes());
            String str = (String)in.readLine();
            System.out.println("Received: " + str);
            //out.flush();

            //Authy
            String username = System.getProperty("user.name");
            out.write(("AUTH "+username).getBytes());
            str = in.readLine();
            System.out.println("Received: " + str);
            //out.flush();

            //Ready

            out.write(("REDY").getBytes());
            str = in.readLine();
            System.out.println("Received: " + str);
            //out.flush();

             // get all server info to find largest 
            out.write(("GETS All\n").getBytes());
            //out.flush();

            str = "" + in.readLine();
            System.out.println("Server says: " + str);

            out.write(("OK\n").getBytes());
            //out.flush();

            str = "" + in.readLine();
            System.out.println("Server says: " + str);

            out.write(("OK\n").getBytes());
            //out.flush();

            int NoOfServers = Integer.parseInt(str.split(" ")[1]);
            List<Server> serverList = new LinkedList<>();

            for (int i = 0; i < NoOfServers; i++){
                str = (String)in.readLine();
                String[] splitString = str.split(" ");
                serverList.add(new Server(splitString[0], splitString[1], splitString[2], splitString[3], splitString[4], splitString[5], splitString[6], splitString[7]));
            }

            out.write(("OK\n").getBytes());
            System.out.println("Sent: OK");

            str = (String) in.readLine();
            System.out.println("Received: " +str);

            //Ascending Server Sorting
            serverList.sort(new Comparator<Server>(){
                public int compare(Server a, Server b){
                    return b.coreNo - a.coreNo;
                }
            });
            
            Server largest = serverList.get(0);

            //Assign 1st job to the largest server
            out.write(("REDY\n").getBytes());
            System.out.println("Sent: REDY");

            str = (String) in.readLine();
            System.out.println("Received: " + str);

            int jobID = Integer.parseInt(str.split(" ")[2]);

            String command = "SCHD " + jobID + " " + largest.Type + " " + largest.ID + "\n";
			out.write(command.getBytes());
			System.out.print("Sent: " + command);

			str = (String)in.readLine();
			System.out.println("Received: " + str);




            out.write(("QUIT").getBytes());
            str = in.readLine();
            System.out.println("Received: " + str);
            out.close();
            s.close();
        } catch (Exception e) {
                System.out.println(e);
        }
    }
}