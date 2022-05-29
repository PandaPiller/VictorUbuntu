// Importing necessary packages
import java.io.*;
import java.net.*;
import java.util.*;

public class myClientStage2 {
	public static void main(String[] args) {
        Socket s = null; //Create a new Socket
		try {
            //Initiation
            int port = 50000;
            s = new Socket("localhost", port);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //Data will be read from Server with Input Stream
            // DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream()); //Output written to Server with Input Stream
			String username = System.getProperty("user.name"); //Used to store the username of the system
			boolean retrieveAllServers = true; //boolean used to only retrieveBestServer all servers once throughout runtime
			List<String> allServers = new ArrayList<>(); //ArrayList used to contain all servers
			String str = ""; //Store the input stream

            //job Variables initialised to 0
			int jobID = 0;
            int jobCoreNo = 0;
            int jobMemory = 0;
            int jobDisks = 0; 

            //Initial Handshake
            //Hello [HELO]
			out.write(("HELO\n").getBytes());
            
			str = (String) in.readLine();
            //Handshake finished

            //Authenticate [AUTH]
			if (str.equals("OK")) {
				out.write(("AUTH " + username + "\n").getBytes());
			}

			str = (String) in.readLine();
			
			//while loop implemented to to keep running until the server send QUIT
			while (!(str.equals("QUIT"))) {
				out.write(("REDY\n").getBytes());
				str = (String) in.readLine();

				if (!(str.equals("NONE"))) {
					// job variable will store the current job
					String job = str;
					if(job.split(" ")[0].equals("JOBN")){ //"split(" ") will split data in readable output format 
						jobID = Integer.parseInt(str.split(" ")[2]); //  Store the ID of the current job
						jobCoreNo = Integer.parseInt(str.split(" ")[4]); // Store the number of cores required for the job
						jobMemory = Integer.parseInt(str.split(" ")[5]); // Store the memory required for the current job
						jobDisks = Integer.parseInt(str.split(" ")[6]);	 // Store the disk space required for the current job
					}
					
					// All Servers will be retrieved if and only during the client's first run
					if(retrieveAllServers) {
						out.write(("GETS All\n").getBytes());
						str = (String)in.readLine();
						int totalServerCount = Integer.parseInt(str.split(" ")[1]);
						out.write(("OK\n").getBytes());
						for(int i = 0; i < totalServerCount; i++) {
							str = (String)in.readLine();
							allServers.add(str);
						}
						out.write(("OK\n").getBytes());
						str = (String)in.readLine();
                        
						retrieveAllServers = false; //Once set to false, it ensures that we will only retrieve the servers once
					}

					// bestServer contains the best possible server for the job
					String[] bestServer = retrieveBestServer(jobCoreNo, jobMemory, jobDisks, str, out, in).split(" ");

					// Check if the job type is JOBN (Job Submission Info.), meaning jobs are only scheduled when required
					if (job.split(" ")[0].equals("JOBN")) {
						out.write(("SCHD " + jobID + " " + bestServer[0] + " " + bestServer[1] + "\n").getBytes());
						str = (String) in.readLine();
					}

					// Check if the job type is JCPL (Job Completion) and move up the waiting jobs to optimise turnaround time
					if(job.split(" ")[0].equals("JCPL")){
						List<String> jobsWaiting = new ArrayList<>(); // jobsWaiting contains all current waiting jobs

						for(int i = 0; i < allServers.size(); i++) {
							out.write(("LSTJ " + allServers.get(i).split(" ")[0] + " " + allServers.get(i).split(" ")[1] + "\n").getBytes()); //Job List for a server 
							str = (String)in.readLine();

                            int currentJobs = Integer.parseInt(str.split(" ")[1]); 	// currentJobs contains the number of current jobs on server

							out.write(("OK\n").getBytes());
							if(currentJobs != 0){
								for(int j = 0; j < currentJobs; j++) {
									str = (String)in.readLine();
									if(str.split(" ")[3].equals("-1")) {
										jobsWaiting.add(str); //add all waiting jobs to arrayList through the for loop
									}
								}
									out.write(("OK\n").getBytes());
							}
							str = (String)in.readLine();
							for(int j = 0; j < jobsWaiting.size(); j++) {
								// bestServer is the best server for the current job
                                bestServer = retrieveBestServer(Integer.parseInt(jobsWaiting.get(j).split(" ")[5]), Integer.parseInt(jobsWaiting.get(j).split(" ")[6]), Integer.parseInt(jobsWaiting.get(j).split(" ")[7]), str, out, in).split(" ");
								out.write(("MIGJ " + jobsWaiting.get(j).split(" ")[0] + " " + allServers.get(i).split(" ")[0] + " " + allServers.get(i).split(" ")[1] + " " + bestServer[0] + " " + bestServer[1] + "\n").getBytes());
                                str = (String)in.readLine();
                            }
							// Clearing all the jobs that are currently waitimg from the arraylist
                            jobsWaiting.clear();
						}
					}	
				} else {
					out.write("QUIT\n".getBytes());
					str = (String) in.readLine();
					System.out.println(str);
				}
			}
			out.flush(); // flush data output stream
			out.close(); // closing data output stream
			s.close(); // closing servver socket connection
		} catch (Exception e) {
			System.out.println(e); // printing error to console if there is any

		}
	}
	
    public static String[] getData(String str, DataOutputStream out, BufferedReader in){
		try{
			int serverCount = Integer.parseInt(str.split(" ")[1]); //variable will keep track of server amount
				out.write(("OK\n").getBytes());
				// servers string array will contain the server data
				String[] servers =  new String[serverCount];
				for(int i = 0; i < servers.length; i++) {
					str = (String)in.readLine();
					servers[i] = str;
				}
			return servers;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	// retrieveBestServer function will return the best server for the given job
	public static String retrieveBestServer(int jobCoreNo, int jobMemory, int jobDisks, String str, DataOutputStream out, BufferedReader in){
        try {
			out.write(("GETS Avail " + jobCoreNo + " " + jobMemory + " " + jobDisks + "\n").getBytes());
			str = (String)in.readLine();
			
			String[] servers = getData(str, out, in);

			if(servers.length > 0) {
				out.write(("OK\n").getBytes());
				str = (String)in.readLine();
			} else {
				str = (String)in.readLine();
			}
			
			// If no available servers are found, getting the server with least waiting time
			if(servers.length == 0) {
				out.write(("GETS Capable "+ jobCoreNo + " " + jobMemory + " " + jobDisks + "\n").getBytes());
				str = (String) in.readLine();
		
				// servers string array contains data on all available servers
				servers = getData(str, out, in);	

				out.write(("OK\n").getBytes());
				str = (String)in.readLine();

				String[] serverWaitTime = new String[servers.length];
				for(int i = 0; i < servers.length; i++) {
					out.write(("EJWT " + servers[i].split(" ")[0] + " " + servers[i].split(" ")[1] + "\n").getBytes());
					out.flush();
					str = (String)in.readLine();
					serverWaitTime[i] = str;
				}

				// selecting the last server by default
				int selectedLastServer = servers.length-1;
				
				for(int i = 0; i < servers.length; i++) {
					String[] server = servers[i].split(" ");
					if(Integer.parseInt(server[4]) >= jobCoreNo && Integer.parseInt(server[5]) >= jobMemory && Integer.parseInt(server[6]) >= jobDisks){
						if(Integer.parseInt(serverWaitTime[selectedLastServer]) > Integer.parseInt(serverWaitTime[i])) {
							selectedLastServer = i;
						}
					}
				}
				// Sending the best server back
				return servers[selectedLastServer];
			}
			return servers[0];
        } catch(Exception e) {
            System.out.println(e);
        }
        return null;
	}


}