public class Server{
    //Initialise different data types for Server ArrayList
    String Type; //Server[0]
    int ID; //Server[1]
    String State; //Server[2]
    int bootupTime; //Server[3]
    double hourlyRate; //Server[4]
    int coreNo; //Server[5]
    int Memory; //Server[6]
    int Disk; //Server[7]

    //Parameters taken from Section 8 of ds-sim User Guide by Young Lee, Young Kim and Jayden Kim
    public Server(String type, String id, String state, String bootup, String rate, String cores, String memory, String disk) {
        this.Type = type;
        this.ID = Integer.parseInt(id);
        this.State = state;
        this.bootupTime = Integer.parseInt(bootup);
        this.hourlyRate = Double.parseDouble(rate);
        this.coreNo = Integer.parseInt(cores);
        this.Memory = Integer.parseInt(memory);
        this.Disk = Integer.parseInt(disk);
    }
}