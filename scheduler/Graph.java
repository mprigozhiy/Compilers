//package scheduler;

import java.util.LinkedList;


public class Graph {

		
		Instruction instr; //only looks for c
		LinkedList<Graph> nextInstrs; 
		LinkedList<Graph> prevInstrs; 
		int refCount;	
		int netLatency;
		int instrLatency;
		int wait = 0;
		int cycle;
		
		public Graph(Instruction instr, LinkedList<Graph> nextInstrs, LinkedList<Graph> prevInstrs, int refCount, int netLatency, int instrLatency, int wait){
			this.instr = instr;
			this.nextInstrs = nextInstrs;
			this.prevInstrs = prevInstrs;
			this.refCount = refCount;
			this.netLatency = netLatency;
			this.instrLatency = instrLatency;
			this.wait = wait;
		}

		public Instruction getInstr() {
			return instr;
		}

		public void setInstr(Instruction instr) {
			this.instr = instr;
		}

		public LinkedList<Graph> getNextInstrs() {
			return nextInstrs;
		}

		public void setNextInstrs(LinkedList<Graph> nextInstrs) {
			this.nextInstrs = nextInstrs;
		}

		public LinkedList<Graph> getPrevInstrs() {
			return prevInstrs;
		}

		public void setPrevInstrs(LinkedList<Graph> prevInstrs) {
			this.prevInstrs = prevInstrs;
		}

		public int getRefCount() {
			return refCount;
		}

		public void setRefCount(int refCount) {
			this.refCount = refCount;
		}

		public int getNetLatency() {
			return netLatency;
		}

		public void setNetLatency(int netLatency) {
			this.netLatency = netLatency;
		}

		public int getInstrLatency() {
			return instrLatency;
		}

		public void setInstrLatency(int instrLatency) {
			this.instrLatency = instrLatency;
		}

		public int getWait() {
			return wait;
		}

		public void setWait(int wait) {
			this.wait = wait;
		}


}
