//package scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Parser {
	
	public static LinkedList<Instruction> scan(BufferedReader ptr) throws IOException{
		
		/*File file = new File(in);
		if (!file.exists()){
			System.out.println("File not found.");
			return null;
		}
				
		Scanner ptr = new Scanner(file);*/
		
		String currLine;
		String token;
		LinkedList<Instruction> scanned = new LinkedList<Instruction>();
		
		
		while((currLine = ptr.readLine()) != null){
			Instruction input = new Instruction(null, null, null, null, null);
			
			
			StringTokenizer st = new StringTokenizer(currLine);
			token = st.nextToken();
			
			if(token.equals("nop")){
				scheduler.nop++;
				continue;
			}
			input.setOpCode(getOp(token));
			
			token = st.nextToken();
			input.setInput1(token);
			
			if(token.contains(",")){
				input.setInput1(input.getInput1().substring(0, input.getInput1().length()-1)); //remove commas
				token = st.nextToken();
				input.setInput2(token);
			}
			
			if(st.hasMoreTokens()){
				token = st.nextToken();
			}
			
			if(st.hasMoreTokens()){
				token = st.nextToken();
				input.setOutput1(token);
			}
			
			if(st.hasMoreTokens()){
				input.setOutput1(input.getOutput1().substring(0, input.getOutput1().length()-1)); //remove commas
				token = st.nextToken();
				input.setOutput2(token);
			}
			

			
			scanned.add(input);
		}
		
		return scanned;	
	}
	
	private static OpCode getOp(String code){
		
		switch(code){
		
		case "addI":
			return OpCode.addI;
		case "add":
			return OpCode.add;
		case "subI":
			return OpCode.subI;
		case "sub":
			return OpCode.sub;
		case "mult":
			return OpCode.mult;
		case "div":
			return OpCode.div;
		case "load":
			return OpCode.load;
		case "loadI":
			return OpCode.loadI;
		case "loadAO":
			return OpCode.loadAO;
		case "loadAI":
			return OpCode.loadAI;
		case "store":
			return OpCode.store;
		case "storeAO":
			return OpCode.storeAO;
		case "storeAI":
			return OpCode.storeAI;
		case "output":
			return OpCode.output;
		case "nop":
			return OpCode.nop;
		}
		
		return null;
	}
	
	public static void printString(LinkedList<Instruction> output){
		
		String opCode, opPrint, input1, input2, output1, output2;
		
		for(int i = 0; i < output.size(); i++){
			opCode = output.get(i).getOpCode().code();
			opPrint = opCode + " ";
			input1 = output.get(i).getInput1() + " ";
			
			if(output.get(i).getInput2() == null){
				input2 = "";
			} else {
				input2 = output.get(i).getInput2() + " ";
			}
			
			if(output.get(i).getOutput1() == null){
				output1 = "";
			} else {
				output1 = output.get(i).getOutput1() + " ";
			}
			
			if(output.get(i).getOutput2() == null){
				output2 = "";
			} else {
				output2 = output.get(i).getOutput2() + " ";
			}
			
			
			
			if(opCode.compareTo("nop") != 0 && opCode.compareTo("load") != 0 && opCode.compareTo("store") != 0 && opCode.compareTo("output") != 0){
				System.out.println(opPrint + input1 + input2 + "=> " + output1 + output2);
			} else {
				System.out.println(opPrint + input1 + input2 + output1 + output2);
			}
		
		}
	}

}
