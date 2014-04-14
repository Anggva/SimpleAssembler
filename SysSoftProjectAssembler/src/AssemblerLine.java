
public class AssemblerLine {
	private String label;
	private String mneumonic;
	private String type;
	private String operand;
	private String comment;	
	private int address;
	private int size;
	AssemblerLine(){
		label = new String();
		mneumonic = new String();
		type = new String();
		operand = new String();
		comment = new String();
		address = 0;
		size = 0;
	}
	AssemblerLine(String[] args){
		label = args[0];
		mneumonic = args[1];
		type = args[2];
		operand = args[3];
		comment = args[4];
		size = 0;
	}
	AssemblerLine(String comment){
		label = new String();
		mneumonic = new String();
		type = new String();
		operand = new String();
		this.comment = comment;
		address = 0;
		size = 0;
	}
	
	AssemblerLine(AssemblerLine t){
		label = t.label;
		mneumonic = t.mneumonic;
		type = t.type;
		operand = t.operand;
		comment = t.comment;
		address = t.address;
		size = t.size;
	}
	
	public void setValues(String[] args){
		label = args[0];
		mneumonic = args[1];
		type = args[2];
		operand = args[3];
		comment = args[4];
	}

	public int setAddr(int prevAddr, HashTable table){
		if (mneumonic.equals(""))
			size = 0;
		else if (mneumonic.equals("RESW"))
			size = 3*Integer.valueOf(operand);
		else
			size = table.retrieveSize(mneumonic);
		if (size == 99999){
			System.out.println("Error! Unknown assembler command!");
			address = prevAddr;
			return address;
		}
		address = prevAddr;
		int nextAddress = address+size;
		return nextAddress;
	}
	public String toString(){
		String result = "";
		if ((size == 0)&&(mneumonic.length() == 0)){
			result = "    " + label + " " 
					+ mneumonic + " " + type + operand + " " + comment;
		}
		else if (mneumonic.length() > 0){
			result = Integer.toHexString(address) + " " + label + " " 
					+ mneumonic + " " + type + operand + " " + comment;
		}
		return result;
		
	}
	
	public void addComment(String S){
		this.comment = S;
	}
	
	public boolean detectLiteral(){
		if ((!operand.equals(""))&&(operand.charAt(0) == '='))
			return true;
		else return false;
	}
	
	public LiteralPool createLiteral(){
		LiteralPool l = new LiteralPool(operand);
		return l;
	}
	
	public boolean detectLtorg(){
		if ((!mneumonic.equals(""))&&(mneumonic.equals("LTORG")))
			return true;
		else return false;
	}
	
	public int pushLiteral(LiteralPool lit, int prevAddress){
		label = lit.getLit();
		if (label.length() > 8)
			label = label.substring(0, 6) + "0";
		size = lit.getSize();
		mneumonic = "BYTE";
		operand = lit.getLit();
		comment = "";
		type = "";
		address = prevAddress;
		int nextAddress = address + size;
		return nextAddress;
		
	}
	

}
