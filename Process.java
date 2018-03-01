import java.util.*;
import java.io.*;
public class Process implements Comparable<Process> {
	public int remaining_cpu_time;
	int arrival_time;
	int max_burst;
	int max_io;
	int id;
	String type=null;
	public int finish_time;
	int c;
	public int finish_iotime;
	public int last_started;
	public int into_queue_time;
	public int total_io_time=0;
	public int last_entry;
	int initial_arrival_time;
	int left_from_preempt=0;
	//stats stuff
	public int wait_time=0, tat;
	//stats stuff ends
	public Process(int id, int at, int b, int c, int io){
		this.id=id;
		this.arrival_time=at;
		this.max_burst=b;
		this.c=c;
		this.remaining_cpu_time=c;
		this.max_io=io;
		this.last_entry=at;
		this.initial_arrival_time=at;
	}

	public Process(Process ref){
		this.id=ref.id;
		this.arrival_time=ref.arrival_time;
		this.max_burst=ref.max_burst;
		this.c=ref.c;
		this.remaining_cpu_time=ref.remaining_cpu_time;
		this.max_io=ref.max_io;
		this.last_entry=ref.last_entry;
		this.initial_arrival_time=ref.initial_arrival_time;
	}

	public int compareTo(Process p)
	{
		if(type=="fcfs")
		{
			int val= (this.arrival_time - p.arrival_time);
			if(val!=0)
				return val;
			else
				return (this.id - p.id);
		}
		else
		{
			int val= (this.remaining_cpu_time - p.remaining_cpu_time);
			if(val!=0)
				return val;
			else
				return (this.id - p.id);	
		}
	}

	public int randomOS(int u,int x)
	{
		return 1+(x%u);
	}
	public int get_burst(int x)
	{
		int burst=randomOS(this.max_burst, x);
		if (burst>remaining_cpu_time)
			return remaining_cpu_time;
		else
			return burst;
	}
	public int get_iotime(int x)
	{
		int burst=randomOS(this.max_io, x);
		return burst;
	}

    public static final Comparator<Process> ArrComp = new Comparator<Process>(){
        public int compare(Process p1, Process p2) {
            return p1.arrival_time - p2.arrival_time;
        }
    };

}