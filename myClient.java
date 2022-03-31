import java.io.*;
import java.net.*;
import java.util.*;

public class myClient {
	public static void main(String[] args) {
		try {
			//Initiation
			Socket s = new Socket("localhost",50000);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
			//BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			

			//Init handshake			
			out.write(("HELO\n").getBytes());
			System.out.println("Sent: HELO");
			
			String str = in.readLine();
			System.out.println("Received: " + str);
			
			//Authenticate
			String username = System.getProperty("user.name");
			out.write(("AUTH " + username + "\n").getBytes());
			System.out.println("Sent: AUTH");
			
			str = in.readLine();
			System.out.println("Received: " + str);

			//Ready
			out.write(("REDY\n").getBytes());
			System.out.println("Sent: REDY");

			str = in.readLine();
			System.out.println("Received: " + str);
			
			//Get Servers
			out.write(("GETS All\n").getBytes());
			System.out.println("Sent: GETS All");

			str = in.readLine();
			System.out.println("Received: " + str);

			out.write(("OK\n").getBytes());
			System.out.println("Sent: OK");

			int numOfServers = Integer.parseInt(str.split(" ")[1]);
			List<Server> serverList = new LinkedList<>();

			for (int i = 0; i < numOfServers; i++){
				str = in.readLine();
				String[] split = str.split(" ");
				serverList.add(new Server(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7]));
			}

			out.write(("OK\n").getBytes());
			System.out.println("Sent: OK");
	
			str = in.readLine();
			System.out.println("Received: " + str);
			
			//Sort servers - ascending
			serverList.sort(new Comparator<Server>() {
				public int compare(Server a, Server b) {
					return b.coreNo - a.coreNo;
				}
			});

			Server largest = serverList.get(0);

			// Assign 1 job to the largest server.
			out.write(("REDY\n").getBytes());
			System.out.println("Sent: REDY");

			str = in.readLine();
			System.out.println("Received: " + str);

			int jobID = Integer.parseInt(str.split(" ")[2]);

			String command = "SCHD " + jobID + " " + largest.Type + " " + largest.ID + "\n";
			out.write(command.getBytes());
			System.out.print("Sent: " + command);

			str = in.readLine();
			System.out.println("Received: " + str);

			// Assign jobs to the largest servers (LRR)

           // Find servers for LRR
           send(out,"GETS All");
           String GETInfo = in.readLine();
           String[] getSplit = GETInfo.split(" ");
           int serverCount = Integer.getInteger(getSplit[1]);
           int mostCores = 0;
           System.out.println(getSplit[1] + " " + getSplit[2]);

           Server[] servers = new Server[serverCount];
           ArrayList<Server> LRRServers = new ArrayList<Server>();
           
           for (int i = 0; i < servers.length; i++) {
               String line = in.readLine();
               servers[i] = Server.fromString(line);

               if (servers[i].coreNo > mostCores) {
                   mostCores = servers[i].coreNo;
                   LRRServers.add(servers[i]);
                   System.out.println(servers[i].coreNo);
               }
               //Select first server-type only
           }

			// Exit
			out.write(("QUIT\n").getBytes());
			System.out.println("Sent: QUIT");
			
			str = in.readLine();
			System.out.println("Received: " + str);
			
			s.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

    public static void send(DataOutputStream out, String cmd){
        try{
            out.write((cmd + "\n").getBytes());
        } catch (Exception IOException) {
            System.out.println("IO Exception");
        }
    }
}