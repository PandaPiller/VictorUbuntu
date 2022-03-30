public class Server{
    String Type; //serve
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
}