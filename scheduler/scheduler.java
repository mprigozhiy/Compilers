//package scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class scheduler {
	
	public static int nop = 0;
	
	public static void main(String[] args) throws IOException {
			
		String input = args[0];
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		LinkedList<Instruction> inputList;
		
		
		inputList = Parser.scan(in); //parse file into a list
		
		if(!inputList.isEmpty()){
		//Parser.printString(inputList); //print just to test functionality (Will print to file later)
		
		LinkedList<Graph> instrList = createGraphNodes(inputList);
		
		Graph init = null;
		
		for(int i = 0; i < instrList.size(); i++){
			
			if(instrList.get(i).getInstr().getOpCode().code().equals("loadI") 
				&& instrList.get(i).getInstr().getOutput1().equals("r0")){
					init = instrList.get(i);
					break;
			}
		}
		
		
		instrMap(instrList); //produce a graph that takes care of dependencies and order
		addWeights(instrList);
		
		setInit(init, instrList);

		if(input.equals("-a")){
			weightedPath(instrList, init);
		} else if(input.equals("-b")){
			instrWeight(instrList, init);
		} else if(input.equals("-c")){
			fifo(instrList, init);
		} else {
			System.out.println("Invalid input.");
			System.exit(0);
		}
		
		
		} else {
			File file = new File("schedule.out");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
				while(nop > 0){
					output.write("nop\n");
					nop--;
			}
		}
	}
	
	
	
	private static Graph instrMap(LinkedList<Graph> inputList){

		//This will look for read-write (anti) dependencies 
		for(int i = 0; i < inputList.size(); i++){ //outter loop
			String input1 = inputList.get(i).getInstr().getInput1();
			String input2 = inputList.get(i).getInstr().getInput2();
			OpCode inputOp = inputList.get(i).getInstr().getOpCode();
			
			for(int j = i + 1; j < inputList.size(); j++){
				String output1 = inputList.get(j).getInstr().getOutput1();
				String output2 = inputList.get(j).getInstr().getOutput2();
				OpCode outputOp = inputList.get(j).getInstr().getOpCode();	
				
				if(input2 == null){ //loadI, store, storeAO, output, nop
					if (output1 == null){//Works!
						if(inputList.get(j).getInstr().getOpCode().code().equals("output")
							&& inputList.get(j).getInstr().getOpCode().code().equals("output")){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
						}
					} else if (output2 != null){ //storeAO condition
						//works
						if(input1.equals(output1) || input1.equals(output2) 
								&& !inputList.get(j).getInstr().getOpCode().code().equals("storeAI")){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
							//break;
						}
						
						
					} else if(output1 != null && output2 == null) { //output2 == null: loadI/store (Complete)
						if (input1.equals(output1)){ //works
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
							//break;
						}
					}
				} else { //input2 != null: loadAI, loadAO, mult, div, add, addI, subI
					if(output2 == null){  //loadAI, loadAO, mult, div, add, addI, subI (compare left of these to right of other)
						try{ //loadAI
							Integer.parseInt(input2); //is it an int?

							if(inputOp.code().equals("loadAI") && outputOp.code().equals("storeAI") 
									&& input1.equals(output1) && input2.equals(output2)){
								inputList.get(i).getNextInstrs().add(inputList.get(j));
								inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
								inputList.get(j).getPrevInstrs().add(inputList.get(i));
							}
							
						} catch(NumberFormatException nfe){ //loadAO, mult, div, add, addI, subI
							//output2
							if(input1.equals(output1)){ //one of the inputs matches
								inputList.get(i).getNextInstrs().add(inputList.get(j));
								inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
								inputList.get(j).getPrevInstrs().add(inputList.get(i));
								input1 = "temp";
								if(input1.equals("temp") && input2.equals("temp")){
									//break;
								}
							} else if(input2.equals(output1)){
								inputList.get(i).getNextInstrs().add(inputList.get(j));
								inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
								inputList.get(j).getPrevInstrs().add(inputList.get(i));
								input2 = "temp";
								if(input1.equals("temp") && input2.equals("temp")){
									//break;
								}
							}
						}
					} else {
						if(input1.equals(output1) && input2.equals(output2)){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
						} else if(input1.equals(output2) && input2.equals(output1)){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
						}
					}
				}		
			}
		}
		
		for(int i = 0; i < inputList.size(); i++){ //outter loop
			String output1 = inputList.get(i).getInstr().getOutput1();
			String output2 = inputList.get(i).getInstr().getOutput2();
			OpCode outputOp = inputList.get(i).getInstr().getOpCode();
			
			for(int j = i + 1; j < inputList.size(); j++){
				String input1 = inputList.get(j).getInstr().getInput1();
				String input2 = inputList.get(j).getInstr().getInput2();
				OpCode inputOp = inputList.get(j).getInstr().getOpCode();
				
				if(output2 == null){ //loadAI, loadAO, mult, div, add, addI, subI (compare right of curr to left of following)
					if (input2 != null){ // add r0, r4 => r5
						if(input1.equals(output1) || input2.equals(output1)) {
							////addI, add, subI, sub, mult, div
							try{
								Integer.parseInt(input2); //This takes care of loadAI
								if(input1.equals(output1) || input2.equals(output1)){
									inputList.get(i).getNextInstrs().add(inputList.get(j));
									inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
									inputList.get(j).getPrevInstrs().add(inputList.get(i));
									//break;
								}
								
							}catch(NumberFormatException nfe) { 
								if(input1.equals(output1) || input2.equals(output1) && !inputList.get(j).getInstr().getOpCode().equals(OpCode.loadAI)){					
									inputList.get(i).getNextInstrs().add(inputList.get(j));
									inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
									inputList.get(j).getPrevInstrs().add(inputList.get(i));
									//break;
								}
							}
							
						}
					} else { //input2 == null
						if (input1.equals(output1)){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
							//break;
						}
					}
				} else { //output2 != null
					if(input2 == null){ //input2
						try{
						if((Integer.parseInt(output2) + 1024) == Integer.parseInt(input1) 
							&& inputList.get(j).getInstr().getOpCode().code().equals("output")){ //storeAI
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
						} else if(output1.equals(input1) && output2.equals(input2)){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
						}
							

						} catch(NumberFormatException nfe) {//storeAO
							if(input1.equals(output1) || input1.equals(output2)){
								inputList.get(i).getNextInstrs().add(inputList.get(j));
								inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
								inputList.get(j).getPrevInstrs().add(inputList.get(i));
							}
						}
					} else {
						if(input1.equals(output1) && input2.equals(output2)
								&& inputList.get(j).getInstr().getOpCode().code().equals("loadAO")){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
							//break;
						}
						
						if(input1.equals(output2) && input2.equals(output1)
								&& inputList.get(j).getInstr().getOpCode().code().equals("loadAO")){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
							//break;
						}
						
						if(input1.equals(output1) && input2.equals(output2)
								&& inputList.get(j).getInstr().getOpCode().code().equals("loadAI")
								&& inputList.get(i).getInstr().getOpCode().code().equals("storeAI")){
							inputList.get(i).getNextInstrs().add(inputList.get(j));
							inputList.get(j).setRefCount(inputList.get(j).getRefCount() + 1);
							inputList.get(j).getPrevInstrs().add(inputList.get(i));
							//break;
						}
					}
				}
				
			}
			
			
			
		}
				
		return null;
	}

	private static LinkedList<Graph> createGraphNodes(LinkedList<Instruction> inputList){
		
		LinkedList<Graph> outputList = new LinkedList<Graph>();
		
		for(int i = 0; i < inputList.size(); i++){
			Graph entry = new Graph(inputList.get(i), new LinkedList<Graph>(),
					new LinkedList<Graph>(), 0, 0, inputList.get(i).getOpCode().latency(), 0);
			outputList.add(entry);
		}

		return outputList;
	}
	
	private static String convert(int input){
		String temp = " abcdefghijklmnopqrstuvwxyz1234";

			return temp.substring(input, input+1);
	}
	
	private static void addWeights(LinkedList<Graph> list){
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getInstr().getOpCode().code().equals("output")) {
				//System.out.println("Found my first output!: " + i);
					
				backtrace(list.get(i), 0);
			}
		}
	}
	
	private static void backtrace(Graph node, int netWeight){
		
		int newNet = netWeight + node.getInstrLatency();
		
		if(newNet > node.getNetLatency()){
			node.setNetLatency(newNet);
		}
		
		
		
		for(int i = 0; i < node.prevInstrs.size(); i++){
			backtrace(node.prevInstrs.get(i), node.getNetLatency());
		}
		
		
	}
	
	private static boolean backTrace2(Graph storeAI, Graph init){
		LinkedList<Boolean> set = new LinkedList<Boolean>();
		
		if(storeAI.equals(init)){
			return false;
		} else if(storeAI.prevInstrs.isEmpty()){
			return true;
		}
		
		for(int i = 0; i < storeAI.getPrevInstrs().size(); i++){
			set.add(backTrace2(storeAI.getPrevInstrs().get(i), init));
		}
		
		if(set.contains(false)){
			return false;
		}
		return true;
		
	}
	
	private static void setInit(Graph init, LinkedList<Graph> list){
		
		Graph storeAI = null;
		
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getInstr().getOpCode().code().equals("storeAI")){
				storeAI = list.get(i);
				if(backTrace2(storeAI, init)){
				init.getNextInstrs().add(storeAI);
				storeAI.refCount++;
				//init.setNetLatency(storeAI.getNetLatency() + init.getInstrLatency());
				storeAI.getPrevInstrs().add(init);
				}
			}
			
			if(list.get(i).getInstr().getOpCode().code().equals("loadAI")){
				storeAI = list.get(i);
				if(backTrace2(storeAI, init)){
				init.getNextInstrs().add(storeAI);
				storeAI.refCount++;
				//init.setNetLatency(storeAI.getNetLatency() + init.getInstrLatency());
				storeAI.getPrevInstrs().add(init);
				}
			}
		}
		
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getInstr().getOpCode().code().equals("storeAI") && storeAI == null){
				System.out.println("Hello");
				storeAI = list.get(i);
			} else if(list.get(i).getInstr().getOpCode().code().equals("storeAI")
					&& list.get(i).getNetLatency() > storeAI.getNetLatency()){
				storeAI = list.get(i);
			}
		}
		
		//init.getNextInstrs().add(storeAI);
		if(storeAI != null){
			init.setNetLatency(storeAI.getNetLatency() + init.getInstrLatency());
		}
		//storeAI.getPrevInstrs().add(init);
		
	}
	
	private static void weightedPath(LinkedList<Graph> list, Graph init) throws IOException{
		File file = new File("schedule.out");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		String toFile;
		LinkedList<Graph> ready = new LinkedList<Graph>();
		LinkedList<Graph> waiting = new LinkedList<Graph>();
		int cycle = 0;
		
		//initialize ready queue
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getRefCount() == 0){
				list.get(i).setWait(0);
				ready.add(list.get(i));
				//list.get(i).getInstr().printString();
			}
			
			if(list.get(i).getNetLatency() > cycle){
				cycle = list.get(i).getNetLatency(); //initialize countdown to
			}
		}
		
		
		//System.out.println("\n\n");
		int ct = 1;
		while(!waiting.isEmpty() || !ready.isEmpty()){//fix this
		Graph readyInstr = null;
		
		for(int i = 0; i < waiting.size(); i++){
			if(waiting.get(i).getWait() == 1 && waiting.get(i).refCount == 0){
				ready.add(waiting.get(i));
				waiting.remove(i);
				i--;
			} else {
				waiting.get(i).setWait(waiting.get(i).getWait() - 1);
			}
		}
		

		
		//retrieve instruction with highest priority
		if(ready.contains(init)){
			readyInstr = init;
		} else {
		for(int i = 0; i < ready.size(); i++){
			if(readyInstr == null || (ready.get(i).getNetLatency() > readyInstr.getNetLatency())){
				readyInstr = ready.get(i);
			}
		}}
		
		//print the instruction
		
		if(readyInstr != null){
			output.write(readyInstr.getInstr().toString());		
		
		for(int i = 0; i < readyInstr.getNextInstrs().size(); i ++){
			
			readyInstr.getNextInstrs().get(i).setRefCount(readyInstr.getNextInstrs().get(i).getRefCount() - 1);
			
			if(!waiting.contains(readyInstr.getNextInstrs().get(i))){
				waiting.add(readyInstr.getNextInstrs().get(i));
				readyInstr.getNextInstrs().get(i).setWait(readyInstr.getInstrLatency());
			} else {
				if(readyInstr.getNextInstrs().get(i).getWait() < readyInstr.getInstrLatency()){
					readyInstr.getNextInstrs().get(i).setWait(readyInstr.getInstrLatency());
				}
			}
			
		}
		
		ready.remove(readyInstr);
		} else if(nop > 0){
			output.write("nop\n");
			nop--;
		}
		//System.out.println(cycle);
		cycle--;
		//System.out.println(ct);
		ct++;
		
		//ready.remove(readyInstr);
		//b, b, b, b, b, t, f, f, t, f

		}
		while(nop > 0){
			output.write("nop\n");
			nop--;
		}
		output.close();	
		
	}
	
	private static void instrWeight(LinkedList<Graph> list, Graph init) throws IOException{
		File file = new File("schedule.out");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		LinkedList<Graph> ready = new LinkedList<Graph>();
		LinkedList<Graph> waiting = new LinkedList<Graph>();
		int cycle = 0;
		
		//initialize ready queue
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getRefCount() == 0){
				list.get(i).setWait(0);
				ready.add(list.get(i));
			//	list.get(i).getInstr().printString();
			}
			
			if(list.get(i).getNetLatency() > cycle){
				cycle = list.get(i).getNetLatency(); //initialize countdown to
			}
		}
		
		
		//System.out.println("\n\n");
		int ct = 1;
		while(!ready.isEmpty() || !waiting.isEmpty()){
		Graph readyInstr = null;
		
		for(int i = 0; i < waiting.size(); i++){
			if(waiting.get(i).getWait() == 1 && waiting.get(i).refCount == 0){
				ready.addFirst(waiting.get(i));
				waiting.remove(i);
				i--;
			} else {
				waiting.get(i).setWait(waiting.get(i).getWait() - 1);
			}
		}
		

		
		//retrieve instruction with highest priority

		for(int i = 0; i < ready.size(); i++){
			if(readyInstr == null || (ready.get(i).getInstrLatency() > readyInstr.getInstrLatency())){
				readyInstr = ready.get(i);
			} else if(ready.get(i).getInstrLatency() == readyInstr.getInstrLatency() && ready.get(i).getNetLatency() > readyInstr.getNetLatency()){
				readyInstr = ready.get(i);
			}
		}
		
		//print the instruction
		
		if(readyInstr != null){
			output.write(readyInstr.getInstr().toString());	
		
		for(int i = 0; i < readyInstr.getNextInstrs().size(); i ++){
			
			readyInstr.getNextInstrs().get(i).setRefCount(readyInstr.getNextInstrs().get(i).getRefCount() - 1);
			
			if(!waiting.contains(readyInstr.getNextInstrs().get(i))){
				waiting.add(readyInstr.getNextInstrs().get(i));
				readyInstr.getNextInstrs().get(i).setWait(readyInstr.getInstrLatency());
			} else {
				if(readyInstr.getNextInstrs().get(i).getWait() < readyInstr.getInstrLatency()){
					readyInstr.getNextInstrs().get(i).setWait(readyInstr.getInstrLatency());
				}
			}
			
		}
		
		ready.remove(readyInstr);
		} else if(nop > 0){
			output.write("nop\n");
			nop--;
		}
		//System.out.println(cycle);
		//System.out.println(ct);
		ct++;
		cycle--;

		//ready.remove(readyInstr);
		//b, b, b, b, b, t, f, f, t, f

		}
		
		while(nop > 0){
			output.write("nop\n");
			nop--;
		}
			output.close();
		
	}
	
	private static void fifo(LinkedList<Graph> list, Graph init) throws IOException{ //first in first out
		File file = new File("schedule.out");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		LinkedList<Graph> ready = new LinkedList<Graph>();
		LinkedList<Graph> waiting = new LinkedList<Graph>();
		int cycle = 0;
		
		//initialize ready queue
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getRefCount() == 0){
				list.get(i).setWait(1);
				ready.add(list.get(i));
				list.get(i).cycle = 0;
			//	list.get(i).getInstr().printString();
			}
			
			if(list.get(i).getNetLatency() > cycle){
				cycle = list.get(i).getNetLatency(); //initialize countdown to
			}
		}
		
		
		int cycleCt = 1;
		
		while(!ready.isEmpty()){
		Graph readyInstr = null;
		
		for(int i = 0; i < ready.size(); i++){
			if(ready.get(i).wait > 0){
				ready.get(i).wait--;
				if(ready.get(i).wait == 0){
					ready.get(i).cycle = cycle;
				}
			}
		}
		
		//retrieve instruction with highest priority
		if(!ready.isEmpty()){
			if(ready.contains(init)){
				readyInstr = init;
			} else {
			for(int i = 0; i < ready.size(); i++){
				if(readyInstr == null && ready.get(i).wait == 0){
					readyInstr = ready.get(i);			
				} else if(readyInstr == null && ready.get(i).wait != 0){
					continue;
				} else if(readyInstr.cycle == ready.get(i).cycle && readyInstr.netLatency < ready.get(i).netLatency ){
					readyInstr = ready.get(i);
				}
				
				}
			}
		}
		//print the instruction
		
		if(readyInstr != null){
			output.write(readyInstr.getInstr().toString());	
		
		for(int i = 0; i < readyInstr.getNextInstrs().size(); i ++){
			
			readyInstr.getNextInstrs().get(i).refCount--;
			
			if(!ready.contains(readyInstr.getNextInstrs().get(i)) && readyInstr.getNextInstrs().get(i).refCount == 0){
				ready.add(readyInstr.getNextInstrs().get(i));
				readyInstr.getNextInstrs().get(i).setWait(readyInstr.getInstrLatency());
			} else {
				if(readyInstr.getNextInstrs().get(i).getWait() < readyInstr.getInstrLatency()){
					readyInstr.getNextInstrs().get(i).setWait(readyInstr.getInstrLatency());
				}
			}
			
		}
		
		ready.remove(readyInstr);
		} else if(nop > 0){
			output.write("nop\n");
			nop--;
		}
		//System.out.println(cycle);
		cycle--;
		//System.out.println(cycleCt);
		cycleCt++;

		//ready.remove(readyInstr);
		//b, b, b, b, b, t, f, f, t, f

		}
		
		while(nop > 0){
			output.write("nop\n");
			nop--;
		}
		output.close();	
		
	}
	
}
