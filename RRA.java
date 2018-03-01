import java.io.*;
import java.util.*;  
public class RRA
{
	public static void run(Queue<Process> inqueue, Scanner randomfile, boolean is_verbose)
	{
		//ready, running, stopped(IO)

		int clock=0;
		int todo=inqueue.size();
		int done=0;
		Queue<Process> readyqueue=new PriorityQueue<Process>();
		List<Process> io_table=new ArrayList<Process>();
		List<Process> finishedProcesses=new ArrayList<Process>();
		Process current_process=null;
		int waste_time=0;
		int job_blocked=0;
		while(done < todo)
		{	if(is_verbose==true)
			{
				System.out.printf("\nBefore Cycle: "+Integer.toString(clock)+"\t");
				for(int i=0;i<todo;i++)
				{
					for(Process p:readyqueue)
					{
						if(p.id==i)
						{	
							// if(p.initial_arrival_time>=clock)
							// 	System.out.printf("unstarted\t0\t");
							// else
								System.out.printf("ready\t0\t");
							break;
						}
					}
					for(Process p:inqueue)
					{
						if(p.id==i)
						{	
							// if(p.initial_arrival_time>=clock)
							// 	System.out.printf("unstarted\t0\t");
							// else
								System.out.printf("unstarted\t0\t");
							break;
						}
					}
					for(Process p:io_table)
					{
						if(p.id==i)
						{	
							int bt=p.into_queue_time-clock+1;
							System.out.printf("blocked\t%d\t", bt);
							break;
						}
					}
					for(Process p:finishedProcesses)
					{
						if(p.id==i)
						{	
							System.out.printf("terminated\t0\t");
							break;
						}
					}
					if(current_process!=null && current_process.id==i)
					{
						int bt=current_process.into_queue_time-clock+1;
						System.out.printf("running\t%d\t", bt);
					}
				}
				//System.out.printf("\n");
			}
			List<Process> to_r=new ArrayList<Process>();
			for(Process pp: inqueue)
			{
				if(pp.arrival_time==clock)
				{
					to_r.add(pp);
					readyqueue.add(pp);
				}
			}
			for(Process pp: to_r)
			{
				inqueue.remove(pp);
			}

			
			List<Process> to_remove=new ArrayList<Process>();
			for(Process p: io_table)
			{
				if(clock==p.into_queue_time)
				{
					to_remove.add(p);
				}
			}
			for(Process p: to_remove)
			{
				p.last_entry=clock;
				p.arrival_time=clock;
				readyqueue.add(p);
				io_table.remove(p);

			}
			if(current_process!=null)
			{
				if(clock==current_process.into_queue_time)
				{
					
					current_process.remaining_cpu_time-=(clock-current_process.last_started);
					if(current_process.remaining_cpu_time==0)
					{	
						current_process.finish_time=clock;
						finishedProcesses.add(current_process);
						done++;
					}
					else if(current_process.left_from_preempt==0)
					{
						int number=randomfile.nextInt();
						int iotime=current_process.get_iotime(number);
						//System.out.printf("IOT-%d-", number);
						current_process.into_queue_time=clock+iotime;
						current_process.total_io_time+=iotime;
						io_table.add(current_process);

					}
					else
					{
						
						current_process.arrival_time=clock;
						current_process.last_entry=clock;
						readyqueue.add(current_process);
					}
					current_process=null;
				}
			}
			if(current_process==null)
			{
				current_process=readyqueue.poll();
				if(current_process!=null && current_process.arrival_time>clock)
				{
					readyqueue.add(current_process);
					current_process=null;
				}
				if(current_process==null){
					clock++;waste_time++;
					if(io_table.size()>0)
						job_blocked++;
					continue;
				}
				current_process.wait_time+=clock-current_process.last_entry;
				int burst;
				if(current_process.left_from_preempt==0)
				{
					int number=randomfile.nextInt();
					burst=current_process.get_burst(number);
				}
				else
				{
					burst=current_process.left_from_preempt;
				}
				//System.out.printf("-PID:--%d-%d-%d-\n", current_process.id, current_process.left_from_preempt, burst);
				//System.out.printf("-%d-", number);
				if(burst>current_process.remaining_cpu_time)
					burst=current_process.remaining_cpu_time;
				if(burst>=2)
				{
					current_process.left_from_preempt=burst-2;
					burst=2;
				}
				else if(current_process.left_from_preempt!=0)
				{
					current_process.left_from_preempt=0;
				}
				current_process.into_queue_time=clock+burst;
				current_process.last_started=clock;

			}
			if(io_table.size()>0)
				job_blocked++;
			clock++;
		}
		int net_finish_time=0;
		float ata=(float)0;
		float aw=(float)0;
		for(Process p: finishedProcesses)
		{
			System.out.printf("\nProcess: "+Integer.toString(p.id));
			System.out.printf("\n(A, B, C , IO), %d, %d, %d, %d", p.initial_arrival_time, p.max_burst,  p.c, p.max_io);
			System.out.printf("\nFinishing time: "+Integer.toString(p.finish_time));
			if(p.finish_time>net_finish_time)
				net_finish_time=p.finish_time;
			p.tat=p.finish_time-p.initial_arrival_time;
			System.out.printf("\nTurn Around time: "+Integer.toString(p.tat));
			ata+=(float)p.tat;
			aw+=(float)p.wait_time;
			System.out.printf("\nTotal IO Time: "+Integer.toString(p.total_io_time));
			System.out.printf("\nWaiting time: "+Integer.toString(p.wait_time));
			System.out.printf("\n\n");
		}
		System.out.printf("SUMMARY DATA\n");
		float cpu=(float)(net_finish_time-(waste_time-1))/net_finish_time;
		float iou=(float)(job_blocked)/net_finish_time;
		System.out.printf("Finishing Time: %d\n", net_finish_time);		
		System.out.printf("CPU Utilization: %f\n", cpu);
		System.out.printf("I/O Utilization: %f\n", iou);
		System.out.printf("Throughput: %f\n", 100/(float)net_finish_time*(float)finishedProcesses.size());
		System.out.printf("Average Turn around T: %f\n", ata/finishedProcesses.size());
		System.out.printf("Average Wait T: %f\n", aw/finishedProcesses.size());
		System.out.printf("\n");


	}
}