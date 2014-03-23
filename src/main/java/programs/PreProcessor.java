package programs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Random;

public class PreProcessor {
        
    private File input;
    
    public PreProcessor(File file){
        this.input = file;
    }
        
    
    public String timeConverter(String time){
	String[] results = time.split(":");
       
       return results[0];
    }
    
    public String ipConverter(String ip) {
       StringBuilder sb = new StringBuilder(ip);
        String [] ipAddressInArray = sb.toString().split("\\.");
        long result = 0;
        long partialIp = 0;
        for (int i = 3; i >= 0; i--) {
            
            if(ipAddressInArray[3 - i].equals("0"))
                partialIp = 0;
            else
                partialIp = Long.parseLong(ipAddressInArray[3 - i]);
 
        result |= partialIp << (i * 8);
 
	}
       
        return Long.toString(result);
    }
    
    public String portConverter(String port) {
        String[] portList={"53","56","79","80","443"};
        int minimum = 0;
	int maximum = 5;
	int randomNum = minimum + (int)(Math.random()* maximum);
        String results = portList[randomNum];
        
        return results;
    }
    
    public String protocolConverter(String protocol) {
        if(protocol.equals("UDP"))
            return "5";
        else
            return "10";
    }
    
    
    public void logToVector() throws FileNotFoundException, IOException{
        
        //Create the new File
        File vectorFile = new File("Vector.csv");
        if (!vectorFile.exists())
            vectorFile.createNewFile();
        
        
        //Initialize the writer
        FileWriter fw = new FileWriter(vectorFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        
        
        //Declare the String builder
        StringBuilder sb;
        
        
        //Declare the Tokenizer and read line by line
        StringTokenizer st;
        BufferedReader br = new BufferedReader(new FileReader(input));
        String line;
        String currentToken;
        
        while ((line = br.readLine()) != null) {
            String[] list = line.split(",");
            if(list[6].equals("0.0.0.0")) {
                sb = new StringBuilder();
                bw.write(sb.toString());
            }else {
                st = new StringTokenizer(line,",");
                sb = new StringBuilder();
                st.nextToken();
                st.nextToken();
                st.nextToken();
                currentToken = (st.nextToken().toString());
                sb.append(timeConverter(currentToken) + ",");
                st.nextToken();
                st.nextToken();
                sb.append(ipConverter(st.nextToken().toString()) + ",");
                st.nextToken();
                st.nextToken();
                sb.append(portConverter(st.nextToken().toString()) + ",");
                sb.append(protocolConverter(st.nextToken().toString()));
                sb.append("\n");
                bw.write(sb.toString());
            }
        }
        
        bw.close();
        br.close();
    }
    
}