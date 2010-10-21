/******************************************************
 Laboratoire #3 : Programmation d'un serveur DNS
 
 Cours :             LOG610
 Session :           Hiver 2007
 Groupe :            01
 Projet :            Laboratoire #3
 Étudiant(e)(s) :    Maxime Bouchard
 Code(s) perm. :     BOUM24028309
 
 Professeur :        Michel Lavoie 
 Nom du fichier :    UDPReceiver.java
 Date crée :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

import sun.io.Converters;

/**
 * Cette classe permet la réception d'un paquet UDP sur le port de réception
 * UDP/DNS. Elle analyse le paquet et extrait le hostname
 * 
 * Il s'agit d'un Thread qui écoute en permanance 
 * pour ne pas affecter le déroulement du programme
 * @author Max
 *
 */


public class UDPReceiver extends Thread {
	/**
	 * Les champs d'un Packet UDP
	 * --------------------------
	 * En-tête (12 octects)
	 * Question : l'adresse demandé
	 * Réponse : l'adresse IP
	 * Autorité : info sur le serveur d'autorité
	 * Additionnel : information supplémentaire
	 */
	
	/**
	 * Définition de l'En-tête d'un Packet UDP
	 * ---------------------------------------
	 * Identifiant Paramètres
	 * QDcount Ancount
	 * NScount ARcount
	 * 
	 *– identifiant est un entier permettant d’identifier la requete.
	 *– parametres contient les champs suivant :
	 *	– QR (1 bit) : indique si le message est une question (0) ou une reponse (1).
	 *	– OPCODE (4 bits) : type de la requete (0000 pour une requete simple).
	 *	– AA (1 bit) : le serveur qui a fourni la reponse a-t’il autorite sur le domaine?
	 *	– TC (1 bit) : indique si le message est tronque.
	 *	– RD (1 bit) : demande d’une requete recursive.
	 *	– RA (1 bit) : indique que le serveur peut faire une demande recursive.
	 *	– UNUSED, AD, CD (1 bit chacun) : non utilises.
	 *	– RCODE (4 bits) : code de retour. 0 : OK, 1 : erreur sur le format de la requete, 2: probleme du serveur,
	 *    3 : nom de domaine non trouve (valide seulement si AA), 4 : requete non supportee, 5 : le serveur refuse
	 *    de repondre (raisons de s´ecurite ou autres).
	 * – QDCount : nombre de questions.
	 * – ANCount, NSCount, ARCount : nombre d’entrees dans les champs ”Reponse”, ”Autorite”, ”Additionnel”.
	 */
	
	/**
	 * Les champs Reponse, Autorite, Additionnel sont tous representes de la meme maniere :
	 *
	 * – Nom (16 bits) : Pour eviter de recopier la totalite du nom, on utilise des offsets. Par exemple si ce champ
	 *   vaut C0 0C, cela signifie qu’on a un offset (C0) de 12 (0C) octets. C’est-a-dire que le nom en clair se trouve
	 *   au 12eme octet du message.
	 * – Type (16 bits) : idem que pour le champ Question.
	 * – Class (16 bits) : idem que pour le champ Question.
	 * – TTL (32 bits) : dur´ee de vie de l’entr´ee.
	 * – RDLength (16 bits): nombre d’octets de la zone RDData.
	 * – RDData (RDLength octets) : reponse
	 */
	
	private DataInputStream d = null;
	protected final static int BUF_SIZE = 1024;
	protected String SERVER_DNS = null;
	protected int port = 53;  // port de réception
	private String DomainName = "none";
	private String DNSFile = null;
	private String adrIP = null;
	private boolean RedirectionSeulement = false;
	private String adresseIP = null;
	
	public void setport(int p) {
		this.port = p;
	}
	
	public void setRedirectionSeulement(boolean b){
		this.RedirectionSeulement = b;
	}
	
	public String gethostNameFromPacket(){
		return DomainName;
	}
	
	public String getAdrIP(){
		return adrIP;
	}
	
	private void setAdrIP(String ip){
		adrIP = ip;
	}
	
	public void sethostNameFromPacket(String hostname){
		this.DomainName = hostname;
	}
	
	public String getSERVER_DNS(){
		return SERVER_DNS;
	}
	
	public void setSERVER_DNS(String server_dns){
		this.SERVER_DNS = server_dns;
	}
	
	public void UDPReceiver(String SERVER_DNS,int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}
	
	public void setDNSFile(String filename){
		DNSFile = filename;
	}
	
	public void run(){

		/**
		 * Utilisé pour l'instant :
		 * http://www.faqs.org/rfcs/rfc1035.html
		 * 
		 * Peut être utile : 
		 * http://www.netfor2.com/rfc1034.txt
		 * http://www.faqs.org/rfcs/rfc1034.html (Même chose ou pas?)
		 * http://www.netfor2.com/dns.htm
		 */
		try{
			//GAB :
			byte[] buffer = new byte[BUF_SIZE];

			
			//*Creation d'un socket UDP
			DatagramSocket socket = new DatagramSocket(port);
			
			//*Boucle infinie de recpetion
			while(true){
				
				//*Reception d'un paquet UDP via le socket
				DatagramPacket p = new DatagramPacket(buffer, buffer.length);
				System.out.println("Waiting to receive on : " + port);
				socket.receive(p);
				System.out.println("Received on : " + port);
			    byte[] packet = new byte[p.getLength()];
			    System.arraycopy(p.getData(), 0, packet, 0, p.getLength());
				
				//*Creation d'un DataInputStream ou ByteArrayInputStream pour manipuler les bytes du paquet				
				d = new DataInputStream(new ByteArrayInputStream(p.getData()));
				
				//*Lecture et sauvegarde des deux premier bytes, qui specifie l'identifiant
				Byte[] id = new Byte[2];
				id[0] = d.readByte();
				id[1] = d.readByte();
				
				//GAB : Lecture du 1er BIT du 3e octet pour savoir si Query/Answer
				//Query = 0 	Answer (response) = 1
				//http://www.faqs.org/rfcs/rfc1035.html section 4.1.1.
				Byte QR = d.readByte();
				int query = QR>>7 & 0x0001;
				
				//*Lecture et sauvegarde du huitieme byte, qui specifie le nombre de reponse dans le message 
				Byte response;
				for (int i=4;i<8;i++)
					d.readByte();
				response = d.readByte();
				
				//*Dans le cas d'une reponse
				if (query==1 && (int)response==1)
				{
					System.out.println("Q=1");
					
					
					//*Lecture du Query Domain name, a partir du 13 byte
					for (int i=9;i<13;i++)
						d.readByte();
					
					//*Sauvegarde du Query Domain name
					int nbchar = d.readByte();
					String domainName = "";
					
					while(nbchar != 0)
					{
						while(nbchar > 0) 
						{
							domainName += String.valueOf(Character.toChars(d.readByte()));
							nbchar--;
						}
						domainName += ".";
						nbchar = d.readByte();
					}
					
					System.out.println(domainName);
										
					
					//*Passe par dessus Query Type et Query Class
					//*Passe par dessus les premiers champs du ressource record pour arriver au ressource data
					//*qui contient l'adresse IP associe au hostname (dans le fond saut de 16 bytes)
					for (int i=0;i<16;i++)
						d.readByte();
										     
					//*Capture de l'adresse IP
					String address="";
					for (int i=0;i<4;i++)
					{
						byte da = d.readByte();
						address+=Integer.parseInt(Integer.toString((da & 0xff) + 0x100, 16).substring(1), 16);
						if (i!=3)
							address+=".";
					}
					
						
					//*Ajouter la correspondance dans le fichier seulement si une seule
					//*reponse dans le message DNS (cette apllication ne traite que ce cas)
					
					
					//*Faire parvenir le paquet reponse au demandeur original, ayant emis une requete 
					//*avec cet identifiant
				}
				
				//*Dans le cas d'une requete
				if (query==0)
				{
					System.out.println("Q=0");
					
					
					//*Lecture du Query Domain name, a partir du 13 byte
					for (int i=9;i<13;i++)
						d.readByte();
					
					//*Sauvegarde du Query Domain name					
					int nbchar = d.readByte();
					String domainName = "";
					
					while(nbchar != 0)
					{
						while(nbchar > 0) 
						{
							domainName += String.valueOf(Character.toChars(d.readByte()));
							nbchar--;
						}
						domainName += ".";
						nbchar = d.readByte();
					}
					
					
					//*Sauvegarde de l'adresse, du port et de l'identifiant de la requete
					/**
					 * WTF ?!
					 */
					InetAddress clientIP = p.getAddress();
					int clientPort = p.getPort();
					// Identifiant = id[] ?
					
					//*Si le mode est redirection seulement
					if (RedirectionSeulement)
					{
						//*Rediriger le paquet vers le serveur DNS	
						socket.send(new DatagramPacket(packet, packet.length, InetAddress.getByName(SERVER_DNS), 53));
					}
					//*Sinon
					else
					{
						//*Rechercher l'adresse IP associe au Query Domain name dans le fichier de 
						//*correspondance de ce serveur
					
						//*Si la correspondance n'est pas trouvee
							
							//*Rediriger le paquet vers le serveur DNS
							socket.send(new DatagramPacket(packet, packet.length, InetAddress.getByName(SERVER_DNS), 53));
					
						//*Sinon
							//*Creer le paquet de reponse a l'aide du UDPAnswerPaquetCreator
					
							//*Placer ce paquet dans le socket
					
							//*Envoyer le paquet
					}
				}
			}
		}catch(Exception e){
			System.err.println("Problème à l'exécution :");
			e.printStackTrace(System.err);
		}	
	}
}
