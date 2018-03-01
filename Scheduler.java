import java.util.*;  
import java.io.*;
public class Scheduler
{
	public static void main(String args[]) throws Exception
	{
			//Queue<Process> queue=new PriorityQueue<Process>();
			List<Process> queue=new ArrayList<Process>();
			String file0, file1;
			boolean is_verbose=false;
			if(args.length==2)
			{
				file0= args[0];
				file1=args[1];
			}
			else
			{	
				file0=args[0];
				file1=args[2];
				is_verbose=true;
			}

			Scanner file = new Scanner(new File(file0));
			Scanner randomFile = new Scanner(new File(file1));
			int noProcess= file.nextInt();
			System.out.printf("Original Input: %d\t", noProcess);
			for(int i=0;i<noProcess;i++)
			{
				//A,B,C, IO
				int a= file.nextInt();
				int b= file.nextInt();
				int c= file.nextInt();
				int io=file.nextInt();
				Process process =new Process(i, a, b, c, io);
				System.out.printf("%d %d %d %d \t",a,b,c,io);
				queue.add(process);
			}
			List<Process> queueToPass1 = new ArrayList<Process>();
			List<Process> queueToPass2 = new ArrayList<Process>();
			List<Process> queueToPass3 = new ArrayList<Process>();
			List<Process> queueToPass4 = new ArrayList<Process>();
			Iterator<Process> loop=queue.iterator();
			while(loop.hasNext()){
				Process temp = loop.next();
				queueToPass1.add(new Process(temp));
				queueToPass2.add(new Process(temp));
				queueToPass3.add(new Process(temp));
				queueToPass4.add(new Process(temp));
			}

			Collections.sort(queueToPass1,Process.ArrComp);
			loop=queueToPass1.iterator();
			int id1=0;
			Queue<Process> rqueue=new PriorityQueue<Process>();
			System.out.printf("\nSorted Input: %d\n", noProcess);
			while(loop.hasNext()){
				Process t=loop.next();
				t.id=id1++;
				System.out.printf("%d %d %d %d \t",t.arrival_time,t.max_burst,t.c,t.max_io);
				t.type="fcfs";
				rqueue.add(t);

			}
			System.out.printf("\n-------First Come First Serve-------------\n");
			FCFS1.run(rqueue, randomFile, is_verbose);
			System.out.printf("\n---------Round Robin Quantum 2----------\n");



			Collections.sort(queueToPass2,Process.ArrComp);
			loop=queueToPass2.iterator();
			id1=0;
			Queue<Process> rqueue2=new PriorityQueue<Process>();
			while(loop.hasNext()){
				Process t=loop.next();
				t.id=id1++;
				t.type="fcfs";
				rqueue2.add(t);

			}

			// System.out.printf("---%d--", rqueue2.size());
			randomFile.close();
			randomFile = new Scanner(new File(file1));
			RRA.run(rqueue2, randomFile, is_verbose);
			System.out.printf("\n---------Uni Programmed----------\n");

			Collections.sort(queueToPass3,Process.ArrComp);
			loop=queueToPass3.iterator();
			id1=0;
			Queue<Process> rqueue3=new PriorityQueue<Process>();
			while(loop.hasNext()){
				Process t=loop.next();
				t.id=id1++;
				t.type="fcfs";
				rqueue3.add(t);

			}

			randomFile.close();
			randomFile = new Scanner(new File(file1));

			UniProgrammed.run(rqueue3, randomFile, is_verbose);

			Collections.sort(queueToPass4,Process.ArrComp);
			loop=queueToPass4.iterator();
			id1=0;
			Queue<Process> rqueue4=new PriorityQueue<Process>();
			while(loop.hasNext()){
				Process t=loop.next();
				t.id=id1++;
				t.type="psjf";
				rqueue4.add(t);

			}

			randomFile.close();
			randomFile = new Scanner(new File(file1));

   			System.out.printf("\n---------PSJF----------\n");
			PSJF.run(rqueue4, randomFile, is_verbose);
	}

}