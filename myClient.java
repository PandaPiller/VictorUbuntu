import java.io.*;
import java.net.*;
import java.util.*;

public class myClient {
	public static void main(String[] args) {
		Socket s = null; //Create a new socket
		try {
			// Establish Sockets,
			int port = 50000; //Set Server Port == 50000
			s = new Socket("localhost", port); //Initialise Socket
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //Data will be read from Server with Input Stream
			// DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream()); //Output written to Server with Input Stream

			// Start of HandShake Protocol
			// Hello [HELO]
			out.write(("HELO\n").getBytes()); //Client sends "HELO" to Server
			System.out.println("Sent: HELO");

			String str = in.readLine(); //Gather Response from Server
			System.out.println("Received: " + str); //Client receives "HELO" response from Server

			// Authenticate [AUTH]
			String username = System.getProperty("user.name");
			out.write(("AUTH " + username + "\n").getBytes()); //Client sends "AUTH" to Server to authenticate username
			System.out.println("Sent: AUTH");
			str = in.readLine(); //Gather Response from Server
			System.out.println("Received: " + str); //Client receives "AUTH" response from Server 

			// Ready [REDY]
			out.write(("REDY\n").getBytes()); //Client sends "REDY" command for next job
			System.out.println("Sent: REDY");
			str = in.readLine(); //Gather Response from Server
			System.out.println("Received: " + str); //Client receives "REDY" response from Server 

			// Get Servers
			out.write(("GETS All\n").getBytes()); //Client sends "GETS" command to Server
			System.out.println("Sent: GETS All");

			str = in.readLine();
			System.out.println("Received: " + str); //Client receives Available Server Amount

			out.write(("OK\n").getBytes());
			System.out.println("Sent: OK");

			int numOfServers = Integer.parseInt(str.split(" ")[1]);
			List<Server> serverList = new LinkedList<>();

			for (int i = 0; i < numOfServers; i++) {
				str = in.readLine();
				String[] serverDataType = str.split(" ");
				//Server with its respective Data Type from Server Type to Disk is added to serverList
				serverList.add(new Server(serverDataType[0], serverDataType[1], serverDataType[2], serverDataType[3], serverDataType[4], serverDataType[5], serverDataType[6], serverDataType[7]));
			}

			out.write(("OK\n").getBytes());
			System.out.println("Sent: OK");
			str = in.readLine();
			System.out.println("Received: " + str);

			// Sort servers in ascending size or Update ServerList
			serverList.sort(new Comparator<Server>() {
				public int compare(Server a, Server b) { 
					return b.coreNo - a.coreNo;
				}
			});

			// Determine largest server type
			String largestServerType = serverList.get(0).Type;
			List<Server> largestServers = new LinkedList<Server>();


			for (Server server : serverList) { //Iterate all servers in severList
				if (server.Type.equals(largestServerType)) { 
					largestServers.add(server);
				} else
					break;
			}

			out.write(("REDY\n").getBytes());
			System.out.println("Sent: REDY");

			str = in.readLine();
			System.out.println("Received: " + str);
			int serverIndex = 0;

			// Assign jobs to the largest servers with Job Scheduler (Largest-Round-Robin)
			do {
				String[] ServerDataTypes = str.split(" "); //split the str into an array then save as ServerDataTypes Array

				if (ServerDataTypes[0].equals("JOBN") || ServerDataTypes[0].equals("JOBP")) { // Handling of Jobs 
					Server currentServer = largestServers.get(serverIndex);
					String JobID = ServerDataTypes[2];
					String JobSchedule = "SCHD " + JobID + " " + currentServer.Type + " " + currentServer.ID + "\n"; //Sceduling a Job

					out.write(JobSchedule.getBytes());
					System.out.print("Sent: " + JobSchedule);

					str = in.readLine(); //Server sends OK
					System.out.println("Received: " + str);

					serverIndex++; //Increment Server Index
					serverIndex = serverIndex % largestServers.size();
				}

				out.write(("REDY\n").getBytes());
				System.out.println("Sent: REDY");

				str = in.readLine();
				System.out.println("Received: " + str);
			} while (!str.equals("NONE"));

			// Exit
			out.write(("QUIT\n").getBytes()); //Client receives "QUIT" command
			System.out.println("Sent: QUIT");
			str = in.readLine();
			System.out.println("Received: " + str); //IF "QUIT" is read by client, connection to server is ended

			s.close(); //Ends connection to Server
		} catch (Exception e) { 
			System.out.println(e);
		}
	}
}
