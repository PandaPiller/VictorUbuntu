import java.io.*;
import java.net.*;
import java.util.List;
import java.util.LinkedList;

public class myClient {
	public static void main(String[] args) {
		Socket s = null; //Create a new socket
		try {
			//   ./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -v all -n
			//Initiation
			int port = 50000; //Set Server Port == 50000
			s = new Socket("localhost", port); //Initialise Socket
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //Data will be read from Server with Input Stream
			// DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream()); //Output written to Server with Input Stream


			//Initial handshake	
			//Hello [HELO]		
			out.write(("HELO\n").getBytes());
			
			String str = (String)in.readLine();
			//Handshake finished
			
			//Authenticate [AUTH]
			String username = System.getProperty("user.name");
			out.write(("AUTH " + username + "\n").getBytes());
			
			str = (String)in.readLine();

			//Ready [REDY]
			out.write(("REDY\n").getBytes());

			str = (String)in.readLine();

			String job = String.valueOf(str);

			// First Capable (FC)
			while (!job.equals("NONE")) {
				String[] splStrings = job.split(" ");

				if (splStrings[0].equals("JOBN") || splStrings[0].equals("JOBP")){ // Hand JOBN and JOBP commands
					//Get Servers
					out.write(("GETS Capable " + splStrings[4] + " " + splStrings[5] + " " + splStrings[6] + "\n").getBytes());

					str = (String)in.readLine();

					out.write(("OK\n").getBytes());


					int ServerCount = Integer.parseInt(str.split(" ")[1]); //Get number of servers sent over DATA
					List<Server> serverList = new LinkedList<>();

					for (int i = 0; i < ServerCount; i++){ //Loop through until all servers have been recorded
						str = (String)in.readLine(); // Read input, which contains server data
						String[] splitString = str.split(" "); // Split data
						serverList.add(new Server(splitString[0], splitString[1], splitString[2], splitString[3], splitString[4], splitString[5], splitString[6], splitString[7]));
					}

					out.write(("OK\n").getBytes());
			
					str = (String)in.readLine();
					String command = "SCHD " + splStrings[2] + " " + serverList.get(0).Type + " " + serverList.get(0).ID + "\n";

					out.write(command.getBytes()); // Sends SCHD command to current server in the rotation

					str = (String)in.readLine(); // Get the OK command
				}

				out.write(("REDY\n").getBytes());

				str = (String)in.readLine();
				job = String.valueOf(str);
			}

			// Exit [QUIT]
			out.write(("QUIT\n").getBytes());
			
			str = (String)in.readLine();
			
			s.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
