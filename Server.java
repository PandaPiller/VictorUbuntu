public class Server{
    String Type; 
    int ID;
    String State;
    int bootupTime;
    double hourlyRate;
    int coreNo;
    int memory;
    int diskSize;

    //Parameters taken from Section 8 of ds-sim User Guide by Young Lee, Young Kim and Jayden Kim
    public Server(String type, String id, String state, String bootup, String rate, String cores, String mem, String disk) {
        this.Type = type;
        this.ID = Integer.parseInt(id);
        this.State = state;
        this.bootupTime = Integer.parseInt(bootup);
        this.hourlyRate = Double.parseDouble(rate);
        this.coreNo = Integer.parseInt(cores);
        this.memory = Integer.parseInt(mem);
        this.diskSize = Integer.parseInt(disk);
    }

    public static Server fromString(String line){
        String[] split = line.split(" ");
        var server = new Server(line, line, line, line, line, line, line, line);
        server.ID = Integer.parseInt(split[1]);
        server.Type = split[2];
        server.bootupTime = Integer.parseInt(split[3]);
        server.hourlyRate = Integer.parseInt(split[4]);
        server.coreNo = Integer.parseInt(split[5]);
        server.memory = Integer.parseInt(split[6]);
        server.diskSize = Integer.parseInt(split[7]);

        return server;
    }
}