/******************************************************
 Laboratoire #3 : Programmation d'un serveur DNS
 
 Cours :             LOG610
 Session :           Automne 2010
 Groupe :            02
 Projet :            Laboratoire #4
 Étudiant(e)(s) :    Gabriel Desmarais
 					 Jean-François Brais-Villemur
 					 Claude Bouchard
 Code(s) perm. :     DESG24078908
 					 BRAJ14088901
 					 BOUC12018902
 Chargée de lab. :   Fatna Belqasmi 
 Nom du fichier :    ServeurDNS.java
 Date crée :         2007-03-10
 Date dern. modif.   2010-10-27
 *******************************************************/
import java.io.File;
import java.io.IOException;

/**
 * Application principale qui lance les autres processus
 * @author Max
 *
 */
public class ServeurDNS {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//args = new String[]{"8.8.8.8","DNSFILE.TXT","false"};
		//args = new String[]{"showtable","DNSFILE.TXT"};
		
		//logo
		System.out.println("--------------------------------------");
		System.out.println("Serveur DNS");
		System.out.println("Ecole de Technologie Superieures (ETS)");
		System.out.println("LOG610 - Réseau de télécommunication");
		System.out.println("Réalisé par Maxime Bouchard");
		System.out.println("--------------------------------------");
		
		if (args.length == 0) {
			System.out.println("Usage: "
					+"[addresse DNS] <Fichier DNS> <TrueFalse/Redirection seulement>");
			System.out.println("Pour lister la table: "
					+"showtable <Fichier DNS>");
			System.out.println("Pour lancer par defaut, tapper : default");
			System.exit(1);
		}
		
		QueryFinder QF = new QueryFinder();
		UDPReceiver UDPR = new UDPReceiver();
		File f = null;
		
		UDPR.setport(53);
		
		if(args[0].equals("default")){
			if (args.length <= 1) {
				UDPR.setSERVER_DNS("8.8.8.8");
				f = new File("DNSFILE.TXT");
				if(f.exists()){
					UDPR.setDNSFile("DNSFILE.TXT");
				}
				else{
					try {
						f.createNewFile();
						UDPR.setDNSFile("DNSFILE.TXT");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				UDPR.setRedirectionSeulement(false);
				UDPR.start();
				}
			else{
				System.out.print("Cette commande n'a pas d'autre argument");
			}
		}
		else
		{
			if(args[0].equals("showtable")){
				if (args.length == 2) {
					f = new File(args[1]);
					if(f.exists()){
						QF = new QueryFinder(args[1]);
					}
					else{
						try {
							f.createNewFile();
							QF = new QueryFinder(args[1]);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					QF.listCorrespondingTable();
				}
				else{
					System.out.println("Vous n'avez pas indique le nom du fichier");
				}
			}
			else{
				if (args.length == 3) {
					UDPR.setSERVER_DNS(args[0]);
					f = new File(args[1]);
					if(f.exists()){
						UDPR.setDNSFile(args[1]);
					}
					else{
						try {
							f.createNewFile();
							UDPR.setDNSFile(args[1]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(args[2].equals("false")){
							UDPR.setRedirectionSeulement(false);
						}
						else{
							UDPR.setRedirectionSeulement(true);
						}
					}
					UDPR.start();
				}
				else{
					System.out.println("Argument(s) invalide(s)!");
				}
			}
		}
	}
}
