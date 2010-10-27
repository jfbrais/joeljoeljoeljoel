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
 Nom du fichier :    QueryFinder.java
 Date crée :         2007-03-10
 Date dern. modif.   2010-10-27
 *******************************************************/
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Cette classe est utilisé pour la recherche d'un hostname
 * dans le fichier contenant l'information de celui-ci.
 * Si le hostname existe, l'adresse IP est retrouné, sinon
 * l'absence de cette adresse est signalé
 * @author Max
 *
 */
public class QueryFinder extends DataStorageAccess {
	
	private String adresse = null;
	private String filename = null;
	private Scanner scanneurFichierSource = null;
	private String uneligne = null;
	private String[] hostnameFromFile = null;
	private String valueToReturn = null;
	
	/**
	 * Constructeur
	 * @param filename
	 * @param adresse
	 */
	public QueryFinder(String filename, String adresse){
		this.filename = filename;
		this.adresse = adresse;
	}
	
	/**
	 * Constructeur
	 * @param filename
	 */
	public QueryFinder(String filename){
		this.filename = filename;
	}
	
	/**
	 * Construteur
	 */
	public QueryFinder(){
		
	}
	
	
	public String getadresse(){
		return adresse;
	}
	
	public String StartResearch(String adresse){
		
		this.adresse = adresse;
		
		try {
			scanneurFichierSource = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		//Test pour savoir si le fichier est vide
		//S'il n'y a pas de ligne après le début du fichier (quand le curseur est avant le
		//début du fichier), le fichier est vide
		
		if(!scanneurFichierSource.hasNextLine()){
			System.out.println("Le fichier DNS est vide");
			return "none";
		}
		
		//prend une ligne
		uneligne = scanneurFichierSource.nextLine();
		hostnameFromFile = uneligne.split(" ");
		
		while(!(hostnameFromFile[0].equals(this.adresse)) && (scanneurFichierSource.hasNextLine())){
			uneligne = scanneurFichierSource.nextLine();
			hostnameFromFile = uneligne.split(" ");
		}
		
		if(hostnameFromFile[0].equals(this.adresse)){
			this.valueToReturn = hostnameFromFile[1];
		}
		else{
			this.valueToReturn = "none";
		}
		scanneurFichierSource.close();
		return this.valueToReturn;
	}
	
	public void listCorrespondingTable(){
		
		try {
			scanneurFichierSource = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		if(!scanneurFichierSource.hasNextLine()){
			System.out.println("La table est vide!");
			return;
		}
		
		while(scanneurFichierSource.hasNextLine()){
			uneligne = scanneurFichierSource.nextLine();
			System.out.println(uneligne);
		}
		scanneurFichierSource.close();
	}
}