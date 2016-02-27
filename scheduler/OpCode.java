//package scheduler;

public enum OpCode {
	addI("addI", 1), add("add", 1), subI("subI",1), sub("sub", 1), mult("mult", 3), div("div",3), load("load", 5),
	loadI("loadI", 1), loadAO("loadAO", 5), loadAI("loadAI", 5), store("store", 5),
	storeAO("storeAO", 5), storeAI("storeAI", 5), output("output", 1), nop("nop", 1);
	
	/*addI("addI", 1), add("add", 1), subI("subI",1), sub("sub", 1), mult("mult", 2), div("div",2), load("load", 3),
	loadI("loadI", 1), loadAO("loadAO", 3), loadAI("loadAI", 3), store("store", 3),
	storeAO("storeAO", 3), storeAI("storeAI", 3), output("output", 1), nop("nop", 1);*/
	
	
	private final String op;
	private final int latency;
	
	OpCode(String op, int latency){
		this.op = op;
		this.latency = latency;
	}
	
	public String code(){
		return op;
	}
	
	public int latency(){
		return latency;
	}
	
	
	public String toString(){
		return "The opcode is: " + op + "\nThe latency is: " + latency;
		
	}
	
}
