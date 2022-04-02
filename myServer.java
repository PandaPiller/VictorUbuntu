import java.io.*;
import java.net.*;

public class myServer {
	public static void main(String[] args) throws IOException{
		Socket s = null;
		ServerSocket ss = new ServerSocket(6666);
		try{
			s = ss.accept(); //establishes connection				
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //Input written from Server with Input Stream
			DataOutputStream out = new DataOutputStream(s.getOutputStream()); //Data will be outputted from Server with Output Stream
			
			String str = (String)in.readLine(); //Gathering Information from Client
			System.out.println("Received: " + str); //Server received Information - success
			
			out.write(("OK\n").getBytes());
			System.out.println("Sent: OK");
			
			str = (String)in.readLine();
			System.out.println("Received: " + str);
			str = (String)in.readLine();
			System.out.println("Welcome " + str + "!");
			
			out.write(("OK\n").getBytes());
			System.out.println("Sent: QUIT");
			
			str = (String)in.readLine();
			System.out.println("Received: " + str);
			
			out.write(("NONE\n").getBytes());
			System.out.println("Sent: NONE");

			str = (String)in.readLine();
			System.out.println("Received: " + str);
			
			out.write(("QUIT\n").getBytes());
			System.out.println("Sent: QUIT");
							
			s.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
		ss.close();
	
	}
}