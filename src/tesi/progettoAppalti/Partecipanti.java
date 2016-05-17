package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import generated.legge190_1_0.*;

public class Partecipanti {

	Pubblicazione.Data.Lotto.Partecipanti partecipanti;
	List <SingoloType> partecipante;


	List<AggregatoType> membri;
	List <String> codiceFiscaleList = new ArrayList<String>();
	String codiceFiscale;
	String identificativoFiscaleEstero;
	String ragioneSociale;
	String ruolo = null;
	Connection conn;
	int tot_partecipanti,lotto_idCig,partecipante_idPartecipante;
	//'codeset' variable in table 'partecipanti' is used to compute the completeness of 'codiceFiscale' and 'identificativoFiscaleEstero'
	// One one among them can be present to identify the perticipant 	
	int codeset =0;
	//codesetRuolo in the table 'lotto_partecipanti' is used to compute the completeness of 'ruolo' in the case of raggruppamenti
	int codesetRuolo = 0;
	int zeroCodiceFiscale = 0; 

	//Constructor of Partecipanti class
	public Partecipanti(Pubblicazione.Data.Lotto.Partecipanti partecipanti, Connection conn, int lotto_idCig){
		this.partecipanti = partecipanti;
		this.conn = conn;
		this.lotto_idCig = lotto_idCig;
	}
	//Retrieve the data of 'raggruppamento' in 'partecipanti'    
	public void getSingleRaggruppamento(){
		List<AggregatoType> membri;
		List<Pubblicazione.Data.Lotto.Partecipanti.Raggruppamento> raggruppamento = null;
		int result_count_same_codiceFiscale = 0;
		int result_count_same_identificativoFiscaleEstero = 0;

		//set codesetRuolo = 1 to indicate it should be considered in the computation of the completeness
		codesetRuolo = 1;

		try{
			raggruppamento = partecipanti.getRaggruppamento();
		}
		catch(NullPointerException e)
		{

		}
		if(raggruppamento != null){
			for (Pubblicazione.Data.Lotto.Partecipanti.Raggruppamento ragg: raggruppamento){
				membri = ragg.getMembro();
				for(AggregatoType membro: membri){
					ruolo = membro.getRuolo();
					/*
					 * If ruolo is not null and the value is not one specified in the XML Schema
					 * then assing to ruolo the value NID
					 */
					if(ruolo != null){
						if((!ruolo.matches("01-MANDANTE"))   && 
								(!ruolo.matches("02-MANDATARIA")) && 
								(!ruolo.matches("03-ASSOCIATA"))  && 
								(!ruolo.matches("04-CAPOGRUPPO")) && 
								(!ruolo.matches("05-CONSORZIATA"))){
							ruolo = "NID";
						}
					}
					//get the ragione sociale of each single membro in the lotto
					ragioneSociale = membro.getRagioneSociale();
					/*
					 * If ragioneSociale element is present and its value is empty
					 * then assign 'null' to it
					 * If ragioneSociale element is prensent and it is out of domain
					 * then assign 'NID' value to it
					 */
					if(ragioneSociale != null){
						if(!ragioneSociale.matches(".{1,250}")){
							if(ragioneSociale.matches("^\\s*$")){
								ragioneSociale = "null";
							}
							else{
								ragioneSociale = "NID";
							}
						}
						else if(ragioneSociale.matches("^\\s*$")){
							ragioneSociale = "null";
						}
						else {
							ragioneSociale = cleanString(ragioneSociale);
						}  

					} 
					//Get the codiceFiscale
					codiceFiscale = membro.getCodiceFiscale();
					//GetIndentificativoFiscaleEstero
					identificativoFiscaleEstero = membro.getIdentificativoFiscaleEstero();

					/*
					 *Set 'codeset' variable
					 *If codiceFiscale is not null and identificativoFiscaleEstero is null
					 *codeset == 1
					 *If codiceFiscale is null AND identificativoFiscaleEsteo is not null
					 *codeset == 2
					 *If codiceFiscale is not null AND identificativoFiscaleEstero is not null
					 *codeset == 3
					 *If codiceFiscale is null AND identifiscativoFiscaleEstero is null
					 *codeset == 4
					 */
					if((codiceFiscale != null) && (identificativoFiscaleEstero==null)){
						codeset = 1;
					}
					else if((codiceFiscale == null) && (identificativoFiscaleEstero != null)){
						codeset = 2;
					}
					else if((codiceFiscale != null) && (identificativoFiscaleEstero != null)){
						codeset = 3;
					}
					else if((codiceFiscale == null) && (identificativoFiscaleEstero == null))
					{
						codeset = 4;
					}


					/*
					 * check if the value of 'codiceFiscale' is in the domain
					 * If codiceFiscale is not null and it is not in the domain
					 * Then assign NID value
					 */

					if(codiceFiscale != null){
						if(!codiceFiscale.matches("[A-Za-z]{6}[0-9]{2}[A-Za-z]{1}[0-9]{2}[A-Za-z]{1}[0-9A-Za-z]{3}[A-Za-z]{1}") 
								&& !codiceFiscale.matches("[A-Za-z]{6}[0-9LMNPQRSTUV]{2}[A-Za-z]{1}[0-9LMNPQRSTUV]{2}[A-Za-z]{1}[0-9LMNPQRSTUV]{3}[A-Za-z]{1}")
								&& !codiceFiscale.matches("[0-9]{11,11}")){
							codiceFiscale = "NID";
						}

					}

					/*
					 *Check if the 'identificativoFiscaleEstero' is null
					 *If identificativoFiscaleEstero is empty 
					 *Then assign 'null'
					 */
					if((identificativoFiscaleEstero != null)){
						//check if the tag is present but the value is not assigned
						if(identificativoFiscaleEstero.matches("^\\s*$")){
							identificativoFiscaleEstero = "null";
						}
					}

					if(codeset == 1)
					{
						/*Check if the codiceFiscale is equal to 000000000000
						 *If it is equal to zero we insert it in the partecipanti without checking if it already exists 
						 *we use this codiceFiscale to evaluate an integrity constraint
						 */
						if(codiceFiscale.equalsIgnoreCase("00000000000"))
						{
							insert_partecipante_database();
							partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
							fill_lotto_partecipanti();
						}
						/*if codiceFiscale is not null and it is not equal to NID
						 * then  Check if the partecipante is already in the database
						 *          if the partecipante is not in the database
						 *             then insert the participant in the tabella partecipanti
						 *                   get the id of the partecipante 
						 *                   insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
						 *           else
						 *              get the id of the partecipante already present in the table
						 *              insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
						 */
						else if( (!codiceFiscale.equalsIgnoreCase("NID")) ){
							result_count_same_codiceFiscale = count_number_of_participant_codFisc();
							if(result_count_same_codiceFiscale == 0){
								insert_partecipante_database();
								partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
								fill_lotto_partecipanti();
							}
							else{
								partecipante_idPartecipante = select_participant_database_codFisc(codiceFiscale,ragioneSociale);
								fill_lotto_partecipanti();
							}
						}
						else{
							/*
							 * If the partipant has the codiceFiscale NID
							 * insert the participant in the partecipanti table and in lotto_partecipanti table 
							 */
							insert_partecipante_database();
							partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
							fill_lotto_partecipanti();
						}
					}
					else if(codeset == 2)
					{
						/*If identificativoFiscaleEstero is not null and is not equal to NID and is not equal to string null
						 *    Then check if the partecipante is already in the database
						 *         If the partecipante is not in the database
						 *            then insert the participant in the tabella partecipanti
						 *                 get the id of the partecipante 
						 *                 insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
						 *         Else
						 *             get the id of the partecipanti already prensent in the table
						 *             insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
						 */
						if ((!identificativoFiscaleEstero.equalsIgnoreCase("NID")) && (!identificativoFiscaleEstero.equalsIgnoreCase("null"))){
							result_count_same_identificativoFiscaleEstero = count_number_of_participant_idFiscEst();
							if(result_count_same_identificativoFiscaleEstero == 0){
								insert_partecipante_database();
								partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
								fill_lotto_partecipanti();
							}
							else{
								partecipante_idPartecipante = select_participant_database_idFiscEstero(identificativoFiscaleEstero,ragioneSociale);
								fill_lotto_partecipanti();
							}

						}
						else{
							/*
							 * If the participant has the identificativo fiscale estero equal to null
							 * Then Insert in the partecipanti and lotto_partecipanti table
							 */
							insert_partecipante_database();
							partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
							fill_lotto_partecipanti();
						}
					}
					else if(codeset == 3)
					{
						/*
						 * If the codiceFiscale and the identificativoFiscaleEstero are both not equal to zero
						 * Insert the participant in partecipanti and lotto_partecipanti table
						 */
						insert_partecipante_database();
						partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
						fill_lotto_partecipanti();  	
					}
					else if (codeset == 4)
					{
						/*
						 * If both codiceFiscale and identificativoFiscaleEsstero are null
						 * Insert in both the partecipanti and lotto_partecipanti table
						 */
						insert_partecipante_database();
						partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
						fill_lotto_partecipanti();
					}

				}
			}
		}
	}

	//Get single participant
	public void getSingleParticipant(){
		int result_count_same_codiceFiscale = 0;
		int result_count_same_identificativoFiscaleEstero = 0; 

		//Set ruolo to null for the single participant	
		ruolo = null;
		//Set codesetRuolo equal to 0 to indicate the row should not be considered for completeness of ruolo 
		codesetRuolo = 0;


		try{
			partecipante = partecipanti.getPartecipante();
		}
		catch(NullPointerException e)
		{
			System.out.println("Non ci sono partecipanti");
			System.out.println("LOTTO ID CIG: "+lotto_idCig);
		}

		if(partecipante !=null){
			for(SingoloType par: partecipante){
				/*
				 * For each participant  in the "lotto" element retrieve:
				 * -codiceFiscale
				 * -identificativoFiscaleEstero
				 * -ragioneSociale
				 */
				//Get the ragione sociale of each single participant in the lotto
				ragioneSociale = par.getRagioneSociale();
				if(ragioneSociale != null){
					if(!ragioneSociale.matches(".{1,250}")){
						System.out.println("LENGHT: "+ragioneSociale.length());
						if(ragioneSociale.matches("^\\s*$")){
							ragioneSociale = "null";
						}
						else{
							System.out.println("RAGIONE SOCIALE NON NEL DOMINIO\n");
							ragioneSociale = "NID";
						}
					}
					else if(ragioneSociale.matches("^\\s*$")){
						ragioneSociale = "null";
					}
					else {
						ragioneSociale = cleanString(ragioneSociale);
					}  

				} 


				codiceFiscale = par.getCodiceFiscale();

				/*
				 *Set 'codeset' variable
				 *If codiceFiscale is not null and identificativoFiscaleEstero is null
				 *codeset == 1
				 *If codiceFiscale is null AND identificativoFiscaleEsteo is not null
				 *codeset == 2
				 *If codiceFiscale is not null AND identificativoFiscaleEstero is not null
				 *codeset == 3
				 *If codiceFiscale is null AND identifiscativoFiscaleEstero is null
				 *codeset == 4
				 */
				identificativoFiscaleEstero = par.getIdentificativoFiscaleEstero();
				if((codiceFiscale != null) && (identificativoFiscaleEstero==null))
				{
					codeset = 1;
				}
				else if((codiceFiscale == null) && (identificativoFiscaleEstero != null))
				{
					codeset = 2;
				}
				else if((codiceFiscale != null) && (identificativoFiscaleEstero != null))
				{
					codeset = 3;
				}
				else if((codiceFiscale == null) && (identificativoFiscaleEstero == null))
				{
					codeset = 4;
				}
				/*
				 * check if the value of 'codiceFiscale' is in the domain
				 * If codiceFiscale is not null and it is not in the domain
				 * Then assign NID value
				 */
				if(codiceFiscale != null){

					if(!codiceFiscale.matches("[A-Za-z]{6}[0-9]{2}[A-Za-z]{1}[0-9]{2}[A-Za-z]{1}[0-9A-Za-z]{3}[A-Za-z]{1}") 
							&& !codiceFiscale.matches("[A-Za-z]{6}[0-9LMNPQRSTUV]{2}[A-Za-z]{1}[0-9LMNPQRSTUV]{2}[A-Za-z]{1}[0-9LMNPQRSTUV]{3}[A-Za-z]{1}")
							&& !codiceFiscale.matches("[0-9]{11,11}")){

						codiceFiscale = "NID";
					}

				}

				/*
				 *Check if the 'identificativoFiscaleEstero' is null
				 *If identificativoFiscaleEstero is empty 
				 *Then assign 'null'
				 */
				if(identificativoFiscaleEstero != null){

					if(identificativoFiscaleEstero.matches("^\\s*$")) {
						identificativoFiscaleEstero = "null";
					}
				}



				if(codeset == 1)
				{
					/*
					 * Check if the codiceFiscale is equal to 000000000000
					 * If it is equal to zero we insert it in the partecipanti without checking if it already exists 
					 * we use this codiceFiscale to evaluate an integrity constraint
					 */
					if(codiceFiscale.equalsIgnoreCase("00000000000"))
					{
						insert_partecipante_database();
						partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
						fill_lotto_partecipanti();
					}
					/*if codiceFiscale is not null and it is not equal to NID
					 * then  Check if the partecipante is already in the database
					 *          if the partecipante is not in the database
					 *             then insert the participant in the tabella partecipanti
					 *                   get the id of the partecipante 
					 *                   insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
					 *           else
					 *              get the id of the partecipante already present in the table
					 *              insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
					 */
					else if((!codiceFiscale.equalsIgnoreCase("NID"))){
						result_count_same_codiceFiscale = count_number_of_participant_codFisc();
						if(result_count_same_codiceFiscale == 0){
							insert_partecipante_database();
							partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
							fill_lotto_partecipanti();
						}
						else{
							partecipante_idPartecipante = select_participant_database_codFisc(codiceFiscale,ragioneSociale);
							fill_lotto_partecipanti();
						}
					}
					else
					{
						insert_partecipante_database();	
						partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
						fill_lotto_partecipanti();
					}
				}
				else if(codeset == 2)
				{
					/*If identificativoFiscaleEstero is not null and is not equal to NID and is not equal to string null
					 *    Then check if the partecipante is already in the database
					 *         If the partecipante is not in the database
					 *            then insert the participant in the tabella partecipanti
					 *                 get the id of the partecipante 
					 *                 insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
					 *         Else
					 *             get the id of the partecipanti already prensent in the table
					 *             insert the row [lotto_idCig,partecipante_idPartecipante] in the table lotto_partecipanti
					 */
					if ((!identificativoFiscaleEstero.equalsIgnoreCase("NID")) && (!identificativoFiscaleEstero.equalsIgnoreCase("null"))){
						result_count_same_identificativoFiscaleEstero = count_number_of_participant_idFiscEst();
						if(result_count_same_identificativoFiscaleEstero == 0){
							insert_partecipante_database();
							partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
							fill_lotto_partecipanti();
						}
						else{
							partecipante_idPartecipante = select_participant_database_idFiscEstero(identificativoFiscaleEstero,ragioneSociale);
							fill_lotto_partecipanti();
						}
					}
					else
					{
						insert_partecipante_database();
						partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
						fill_lotto_partecipanti();
					}
				}
				else  if(codeset == 3)
				{
					/*
					 * If the codiceFiscale and the identificativoFiscaleEstero are both not equal to zero
					 * Insert the participant in partecipanti and lotto_partecipanti table
					 */
					insert_partecipante_database();
					partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
					fill_lotto_partecipanti();
				}
				else if(codeset == 4)
				{
					/*
					 * If both codiceFiscale and identificativoFiscaleEsstero are null
					 * Insert in both the partecipanti and lotto_partecipanti table
					 */
					insert_partecipante_database();
					partecipante_idPartecipante = get_partecipante_idPartecipante_from_partecipante();
					fill_lotto_partecipanti();
				}
			}

		}


	}	

	//Return a list of the codiceFiscale of all the Partecipanti for a "Lotto"	
	public List<String> getCodiceFiscale(){
		partecipante = partecipanti.getPartecipante();

		for(SingoloType par: partecipante){
			codiceFiscaleList.add(par.getCodiceFiscale());
		}
		return codiceFiscaleList;
	}
	//Query to insert a partecipante in the "partecipanti" table of appalti database
	public void insert_partecipante_database(){
		PreparedStatement pstm;

		try {
			String query = "INSERT INTO appalti.partecipanti(codiceFiscale,identificativoFiscaleEstero,ragioneSociale,codeset) VALUES ('"+codiceFiscale+"','"+identificativoFiscaleEstero+"','"+ragioneSociale+"','"+codeset+"')";
			pstm = conn.prepareStatement(query);
			pstm.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public int count_number_of_participant_codFisc(){
		int number = 0;
		Statement stm = null;
		String query = "SELECT COUNT(*) AS sameCodiceFiscale FROM appalti.partecipanti WHERE codiceFiscale='"+codiceFiscale+"' AND codeset = '1'";
		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()){
				number = rs.getInt("sameCodiceFiscale");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return number;
	}


	public int count_number_of_participant_idFiscEst(){
		int number = 0;
		Statement stm = null;
		String query = "SELECT COUNT(*) AS sameCodiceFiscale FROM appalti.partecipanti WHERE identificativoFiscaleEstero='"+identificativoFiscaleEstero+"' AND codeset = '2'";
		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()){
				number = rs.getInt("sameCodiceFiscale");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return number;
	}
	//Count the number of the participant with the same codiceFiscale and the same ragioneSociale 
	public int count_number_same_codiceFiscale_ragioneSociale(){
		int number = 0;
		Statement stm = null;
		String query = "SELECT COUNT(*) AS samePartecipanti FROM appalti.partecipanti WHERE codiceFiscale = '"+codiceFiscale+"' AND ragioneSociale = '"+ragioneSociale+"'";

		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()){
				number = rs.getInt("samePartecipanti");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return number;
	}
	//Count the number of participant wit the same identificativoFiscaleEstero and the same ragioneSociale
	public int count_number_same_identificativoFiscaleEstero_ragioneSociale(){
		int number = 0;
		Statement stm = null;
		String query = "SELECT COUNT(*) AS samePartecipanti FROM appalti.partecipanti WHERE identificativoFiscaleEstero = '"+identificativoFiscaleEstero+"'";

		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()){
				number = rs.getInt("samePartecipanti");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return number;
	}
	//Select of a 'partecipante'  with 'codiceFiscale'  or  'identificativoFiscaleEstero' equal to the one passed as argument
	public int select_participant_database_codFisc(String codFisc,String ragioneSociale){
		int id = 0;
		Statement stm = null;
		if(!codFisc.equals("NID") && (codFisc != null)){
			String query = " SELECT idPartecipante FROM appalti.partecipanti WHERE (codiceFiscale ='"+codFisc+"')";
			try {
				stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				while(rs.next()){
					id =  rs.getInt("idPartecipante");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return id;
	}

	public int select_participant_database_idFiscEstero(String identificativoFiscaleEstero,String ragioneSociale)
	{
		int id = 0;
		Statement stm = null;
		if(!identificativoFiscaleEstero.equals("NID") && (identificativoFiscaleEstero != null)){
			String query = " SELECT idPartecipante FROM appalti.partecipanti WHERE (identificativoFiscaleEstero ='"+identificativoFiscaleEstero+"')";
			try {
				stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				while(rs.next()){
					id =  rs.getInt("idPartecipante");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return id;
	}

	public void fill_lotto_partecipanti(){
		/*
		 * Create a ne istance of lotto_partecipanti 
		 * ARGS: lotto_ig_cig, is the identifier of the lotto on which we are working on
		 *      partecipante_idPartecipante: id the id of the last partecipant parsed for the specific lotto
		 *      ruolo: is the ruolo in the case of raggruppamento, null otherwise
		 */
		Lotti_partecipanti lottoPart = new Lotti_partecipanti(conn,lotto_idCig,partecipante_idPartecipante,ruolo,codesetRuolo);
		lottoPart.insert_lotto_partecipanti_database();   
	}
	//Select the idPartecipante of the last participant inserted in the partecipanti table
	int get_partecipante_idPartecipante_from_partecipante(){
		int id = 0;
		Statement stm = null;
		String query = "SELECT idPartecipante FROM `appalti`.`partecipanti` ORDER by idPartecipante DESC LIMIT 1";
		ResultSet rs;

		try {
			stm = conn.createStatement();

			rs = stm.executeQuery(query);
			while(rs.next()){
				id = rs.getInt("idPartecipante");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	//Function to clean the string
	public String cleanString(String stringa){
		stringa = stringa.replace("'", "");
		stringa = stringa.replace("\\", " ");
		return stringa;

	}

	//funzione per contare il numero di codici fiscali a zero
	public int returncode(){
		return zeroCodiceFiscale;
	}

}
