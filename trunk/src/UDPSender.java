/*Laboratoire #4 : Programmation d'un serveur DNS
 
 Cours :             LOG610
 Session :           Hiver 2007
 Groupe :            01
 Projet :            Laboratoire #4

 Nom du fichier :    UDPSender.java
 Date crée :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender implements InterfaceUDPServices {

	private final static int BUF_SIZE = 1024;
	private String SERVER_DNS = null;
	private int port = 0;  // port de réception
	private DatagramPacket packet;
	private DatagramSocket SendSocket;
	private InetAddress addr = null;
	
	public String getSERVER_DNS(){
		return SERVER_DNS;
	}
	
	public void setSocket(DatagramSocket SendSocket){
		this.SendSocket = SendSocket; 
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public void setPacket(DatagramPacket packet){
		this.packet = packet;
	}
	
	public void setSERVER_DNS(String server_dns){
		this.SERVER_DNS = server_dns;
	}
	
	public UDPSender(String server_dns,int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}
	
	public UDPSender(DatagramPacket packet,String ServerDNS,int port){
		this.packet = packet;
		this.SERVER_DNS = ServerDNS;
		this.port = port;
	}
	
	public UDPSender(int port,DatagramSocket SendSocket){
		this.port = port;
		this.SendSocket = SendSocket;
	}
	
	public UDPSender(){
		
	}
	
	public void SendPacketNow(){
		//Envoi du packet à un serveur dns pour interrogation
		try {
			
			//cree l'adresse de destination
			
			//Crée le packet

			//Envoi le packet

			
		} catch (Exception e) {
			System.err.println("Problème à l'exécution :");
			e.printStackTrace(System.err);
		}
	}
}
