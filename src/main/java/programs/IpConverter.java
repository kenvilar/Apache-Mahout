/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package programs;

/**
 *
 * @author hduser
 */
public class IpConverter {
    public static void main(String[] args){
        String ipAddress = "192.168.1.2";
        String[] ipAddressInArray = ipAddress.split("\\.");
 
	double result = 0;
	for (int i = 0; i < ipAddressInArray.length; i++) {
 
		int power = 3 - i;
		int ip = Integer.parseInt(ipAddressInArray[i]);
		result += ip * Math.pow(256, power);
 
	}
        System.out.println(result);
    }
}
