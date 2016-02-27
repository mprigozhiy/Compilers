//package scheduler;

public class Instruction{
	
	String index;
	private OpCode opCode;
	private String input1;
	private String input2;
	private String output1;
	private String output2;
	

	Instruction(OpCode opCode, String input1, String input2, String output1, String output2){
		this.opCode = opCode;
		this.input1 = input1;
		this.input2 = input2;
		this.output1 = output1;
		this.output2 = output2;
	}
	
	
	public OpCode getOpCode() {
		return opCode;
	}

	public void setOpCode(OpCode opCode) {
		this.opCode = opCode;
	}

	public String getInput1() {
		return input1;
	}

	public void setInput1(String input1) {
		this.input1 = input1;
	}

	public String getInput2() {
		return input2;
	}

	public void setInput2(String input2) {
		this.input2 = input2;
	}

	public String getOutput1() {
		return output1;
	}

	public void setOutput1(String output1) {
		this.output1 = output1;
	}

	public String getOutput2() {
		return output2;
	}

	public void setOutput2(String output2) {
		this.output2 = output2;
	}

	public String toString(){
		/*if(this.index.equals(" ")){
			this.index = "init";
		}
		
		if(this.index.equals("1")){
			this.index = "output";
		}*/
		
		String output = this.opCode.code() + " " + this.input1;// + " " + this.input2 + " => " + this.output1 + " " + this.output2;
		if(this.input2 != null){
			output = output.concat(", " + input2 + " => ");
		} else if(this.output1 != null){
			output = output.concat(" => ");
		}
		
		if(this.output1 != null){
			output = output.concat(output1);
		}
		
		if(this.output2 != null){
			output = output.concat(", " + output2);
		}
		output = output.concat("\n");
		
		//System.out.print(output);
		return output;
	}
}
