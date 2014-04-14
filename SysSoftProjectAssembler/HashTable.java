import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class HashTable {
	private int size;
	private String[][] table;
	HashTable(){
		size = 0;
		table = null;
	}
	
	public boolean isNumber(String s){
		boolean c = true;
		try{
			Integer.parseInt(s);
		}catch(NumberFormatException exc){
			c = false;
		}
		return c;
	}
	public int nextPrime (int n)
	{
		if (n%2==0) n+=1;
		boolean check = false;
		while (!check) {
			int stop = (int)Math.sqrt(n);
			if (stop < 3){
				check = true;
				break;
			}
			else for (int i = 2; i < stop; i+=2) {
				if (n%i==0) {
					check=false; 
					break;
				} 
				
				check = true;
			}
			if (!check) {
				n+=2;
			}
		}
		return n;
	}
	public int hash(String s, int size)
	{
		int h = 0;
		for (int i = 0; i < s.length(); i++)
		{
			h = (h * 10 + s.charAt(i)) % size;
		}
		return h;
	}
	public int hash(int item, int size)
	{
		String s = String.valueOf(item);
		int h = 0;
		for (int i = 0; i < s.length(); i++)
		{
			h = (h * 10 + Integer.parseInt(String.valueOf(s.charAt(i)))) % size;
		}
		return h;
	}
	
	public void parseDic(String fileName){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while (reader.readLine() != null){
				size++;
			}
			size = nextPrime(size);
			table = new String[4][size];
			String line = new String();
			reader.close();
			reader = new BufferedReader(new FileReader(fileName));
			while ((line = reader.readLine()) != null){
				String [] brk = line.split(" ");
				int hvalue = hash(brk[0], size);
				while(table[3][hvalue] != null){
					hvalue++;
					hvalue=hash(hvalue,size);
				}
				table[0][hvalue] = brk[0];
				table[1][hvalue] = brk[1];
				table[2][hvalue] = brk[2];
				table[3][hvalue] = String.valueOf(hvalue);
			}			
		}
		catch (IOException E){
			System.out.println("File not found");
		}
		catch (ArrayIndexOutOfBoundsException E){
			System.out.println("File not found");
		}
	}
	public int retrieveSize(String mneumonic){
		int s = 0;
		boolean c = false;
		int hvalue = hash(mneumonic, size);
		int count = 0;
		while (table[3][hvalue] != null)
		{
			if (table[0][hvalue].equals(mneumonic)){
				s = Integer.valueOf(table[2][hvalue]);
				c = true;
				break;
			}
			++count;
			if (count > size)
				break;
			++hvalue;
			hvalue=hash(hvalue,size);
		}
		if ((!c)||(count > size)){
			System.out.println("Error, " + mneumonic + " wasn't found!"+"\n");
			return 99999;
		}
		return s;
	}
	public void createSimpleTable(int size){
		this.size = nextPrime(size);
		table = new String [2][this.size];

	}
	public int useSimpleTable (String s){
		if (s.equals(""))
			return 1;
		int hvalue = hash(s, size);
		boolean c = true;
		while(table[0][hvalue] != null){
			if (table[0][hvalue].equals(s)){
				System.out.println("Error! Label " + s + " already exists in the file");
				c = false;
				break;
			}
			else {
				hvalue++;
				hvalue=hash(hvalue,size);
			}	
		}
		if (c){
			table[0][hvalue] = s;
			table[1][hvalue] = String.valueOf(hvalue);
		}
		return 0;
	
	}
}
