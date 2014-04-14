
public class LiteralPool {
	private int size;
	private String literal;
	
	LiteralPool(){
		size = 0;
		literal = new String();
	}
	
	LiteralPool(String lit){
		literal = lit;
		size = 0;
		if (literal.charAt(1)=='x')
			if (literal.length()%2 == 1)
				System.out.println("Odd number of characters in the literal");
			else
				size = (literal.length() - 2) / 2 - 1;
		if (literal.charAt(1)=='c')
			size = literal.length() - 4;
	}
	
	public int getSize(){
		return size;
	}
	
	public String getLit(){
		return literal;
	}
}
