import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Pr3 {
	public static AssemblerLine [] deleteEmpty(AssemblerLine [] table){
		int newSize = table.length;
		for (int i = table.length - 1; i>=0; i--)
			if (table[i]==null){
				newSize--;
			}
		AssemblerLine [] newTable = new AssemblerLine [newSize];
		for (int i = 0; i < newSize; i++)
			newTable[i] = new AssemblerLine(table[i]);
		return newTable;
				
	}
	
	public static AssemblerLine [] shiftAndAdd (AssemblerLine [] table, int n, int start){
		int newSize = table.length + n;
		AssemblerLine [] newTable = new AssemblerLine [newSize];
		for (int i = 0; i < start; i++){
			newTable[i] = new AssemblerLine(table[i]);
		}
		for (int i = newSize - 1; i >= start + n; i--){
			newTable[i] = new AssemblerLine(table[i - n]);
		}
		for (int i = start; i < start + n; i++)
			newTable[i] = new AssemblerLine();
		return newTable;
	}
	
	public static AssemblerLine [] add (AssemblerLine [] table, int n){
		int newSize = table.length + n;
		AssemblerLine [] newTable = new AssemblerLine [newSize];
		for (int i = 0; i < table.length; i++){
			newTable[i] = new AssemblerLine(table[i]);
		}
		for (int i = table.length; i < newSize; i++){
			newTable[i] = new AssemblerLine();
		}
		return newTable;
	}
	
	public static String trimTrail (String S){
		if (S.length()>0){
			int l = S.length()-1;
			while (S.charAt(l) == ' ')
				l--;
			S = S.substring(0, l+1);
		}
		return S;
	}
	
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	public static int setStart(boolean check, String word, int addr){
		if (word.equals("START"))
			if (check)
				return addr;
			else
				System.out.println("More than one START instruction in the file");
		return 0;
			
	}
	
	public static String parseString (String line, int start, int end){
		StringBuilder newString = new StringBuilder ("");
		for (int i = start; i < end; i++){
			if ((line.charAt(i) == ' ')&&(start < 31)){
				break;
			}
			else 
				newString.append(line.charAt(i));
		}
		String result = newString.toString();
		return result;
	}
	
	public static int fileSize (String fileName){
		int size = 0;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while (reader.readLine() != null){
				size++;
			}
			reader.close();
		}
		catch (IOException E){
			System.out.println("File not found");
		}
		return size;
	}
	
	public static void parse (String fileName){
		int size = fileSize(fileName);
		AssemblerLine[] table = new AssemblerLine[size];
		int startAddr = 0;
		int startLine = 0;
		boolean check = true;
		try{
			String line = new String();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			int k = 0;
			int e = 0;
			HashTable labels = new HashTable();
			labels.createSimpleTable(size);
			while ((line = reader.readLine()) != null){
				String[] split = new String[5];
				line = trimTrail(line);
				if (line.equals("")){
					size--;
					continue;
				}
				if (line.length() > 80){
					table[k] = new AssemblerLine("Error, input line is too long");
					k++;
					continue;
				}	
				if (line.charAt(0)=='.')
				{
					table[k] = new AssemblerLine(line);
					k++;
					continue;
				}
				if (line.length()<4){
					k++;
					continue;
				}
				split[0] = parseString(line, 0, 7);
				labels.useSimpleTable(split[0]);
				if ((line.length()>7)&&(line.charAt(9)=='+'))
					e = 1;
				if (line.length()<16)
					split[1] = parseString(line, 10, line.length());
				else
					split[1] = parseString(line, 10, 16);
				
				if ((line.length()<18)||(line.charAt(18) == ' '))
					split [2] = "";
				else 
					split [2] = String.valueOf(line.charAt(18));
				if (line.length()<28)
					split[3] = parseString(line, 19, line.length());
				else
					split[3] = parseString(line, 19, 28);
				boolean m = isInteger(split[3]);
				if (m){
					int c = setStart(check, split[1], Integer.parseInt(split[3], 16));
					if (c != 0){
						check = false;
						startAddr = c;
						startLine = k;
					}
				}
				if (line.length()<31)
					split [4] = "";
				else
					split[4] = parseString(line, 31, line.length());
				if (e == 1)
					split[1] = "+" + split[1];
				e = 0;
				table[k] = new AssemblerLine(split);
				k++;
				}
			if (size<table.length)
				table = deleteEmpty(table);
			HashTable dic = new HashTable();
			dic.parseDic("dic");
			LiteralPool [] lits = new LiteralPool [size];
			int st = 1;
			if (check){
				System.out.println("No START instruction! Start at address 0");
				st = 0;
			}
			int nextAddr = startAddr;
			for (int i = 0; i < startLine; i++){
				int a = table[i].setAddr(nextAddr, dic);
				nextAddr = a;
			}
			table[startLine].setAddr(startAddr, dic);
			k = 0;
			for (int i = st; i < size; i++){
				int a = table[i].setAddr(nextAddr, dic);
				nextAddr = a;
				if (table[i].detectLiteral()){
					lits[k] = table[i].createLiteral();
					k++;
				}
				if (table[i].detectLtorg()){
					table = shiftAndAdd(table, k, i+1);
					int m = 0;
					for (int j = i+1; j < i + k+1; j++){
						nextAddr = table[j].pushLiteral(lits[m], nextAddr);
						lits[m] = new LiteralPool();
						m++;
					}
					i+=k;
					size+=k;
					k = 0;					
				}
			}
			if (k > 0){
				table = add(table, k);
				int newSize = size + k;
				int m = 0;
				for (int i = size; i < newSize; i++){
					nextAddr = table[i].pushLiteral(lits[m], nextAddr);
					m++;
				}
				size = newSize;
			}
			for (int i = 0; i < size; i++)
				System.out.println(table[i].toString());	
		}
		catch (IOException E){
			System.out.println("File not found");
		}
		catch (ArrayIndexOutOfBoundsException E){
			System.out.println("File not found");
		}
		catch (StringIndexOutOfBoundsException E){
			System.out.println("Incorrect format of the input file");
		}
		catch (NumberFormatException E){
			System.out.println("Incorrect format of the input file");
		}
	}
	
	public static void main (String[] args){
		String filename = args[0];
		parse(filename);
	}
}
