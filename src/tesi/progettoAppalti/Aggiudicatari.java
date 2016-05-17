package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import generated.legge190_1_0.AggregatoType;
import generated.legge190_1_0.Pubblicazione;
import generated.legge190_1_0.SingoloType;

public class Aggiudicatari {

	Pubblicazione.Data.Lotto.Aggiudicatari aggiudicatari;
	Connection conn;
	List <SingoloType> aggiudicatario;
	List<String> list_of_codiceFiscale = new ArrayList<String>();
	String codiceFiscale;
	int lotto_idCig;
	String ragioneSociale;
	String identificativoFiscaleEstero;

	String ruolo = null;
	//CodesetRuolo it is used to compute the completeness of 'ruolo' in the case of raggruppamento
	int codesetRuolo;
	/*
	 * Codeset is used to compute the completeness of 'codiceFiscale' and 'identificativoFiscaleEstero' 
	 * only one of them must be present in the file
	 */
	int codeset = 0;
	int id_participant;
	int check_id;
	int aggiudicatario_idAggiudicatario;

	public Aggiudicatari(Pubblicazione.Data.Lotto.Aggiudicatari aggiudicatari,Connection conn,int lotto_idCig ){
		this.lotto_idCig = lotto_idCig;
		this.aggiudicatari = aggiudicatari;
		this.conn = conn;
	}

	//Get the aggiudicatari in aggiudicatarioRaggruppamento
	public void getSingleRaggruppamento(){
		List<AggregatoType> membri;
		List<Pubblicazione.Data.Lotto.Aggiudicatari.AggiudicatarioRaggruppamento> aggiudicatarioRaggruppamento = null;
		try
		{
			aggiudicatarioRaggruppamento = aggiudicatari.getAggiudicatarioRaggruppamento();
		}catch(NullPointerException e)
		{
			System.out.println("NON c'è l'aggiudicatario");
		}
		if(aggiudicatarioRaggruppamento != null)
		{
			//Set codesetRuolo to 1 to state a participant is part of a raggruppamento
			codesetRuolo = 1;
			for(Pubblicazione.Data.Lotto.Aggiudicatari.AggiudicatarioRaggruppamento aggRagg : aggiudicatarioRaggruppamento){
				membri = aggRagg.getMembro();
				for(AggregatoType membro : membri){
					//set id_participant to zero
					id_participant =0;
					ruolo = membro.getRuolo();
					/*
					 * If ruolo is not null and it is not one of the values specified in the XML Schema
					 * Then assign NID
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
					if((identificativoFiscaleEstero != null)){
						//check if the tag is present but the value is not assigned
						if(identificativoFiscaleEstero.matches("^\\s*$")) {
							identificativoFiscaleEstero = "null";
						}
					}
					if(codeset == 1){
						/*Check if the codiceFiscale is equal to 000000000000
						 *If it is equal to zero we insert it in the aggiudicatari without checking if it already exists 
						 *we use this codiceFiscale to evaluate an integrity constraint
						 */
						if(codiceFiscale.equalsIgnoreCase("00000000000"))
						{
							insert_aggiudicatario();
							aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
							fill_lotto_aggiudicatari();
						}
						/*if codiceFiscale is not null and it is not equal to NID
						 * then  Check if the aggiudicatario is already in the database
						 *          if the aggiudicatario is not in the database
						 *             then insert the aggiudicatario in the tabella aggiudicatari
						 *                   get the id of the aggiudicatario 
						 *                   insert the row  in the table lotto_aggiudicatari
						 *           else
						 *              get the id of the aggiudicatario already present in the table
						 *              insert the row in the table lotto_aggiudicatari
						 */
						else if ((!codiceFiscale.equalsIgnoreCase("NID")))
						{
							check_id =  count_number_of_aggiudicatari_codFisc(codiceFiscale);
							if(check_id == 0)
							{
								aggiudicatario_idAggiudicatario = 0;
								insert_aggiudicatario();
								aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
								fill_lotto_aggiudicatari();
							}
							else
							{
								aggiudicatario_idAggiudicatario = select_id_aggiudicatario_from_aggiudicatari_codFisc(codiceFiscale);
								fill_lotto_aggiudicatari();

							}

						}
						else{
							insert_aggiudicatario();
							aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
							fill_lotto_aggiudicatari();

						}
					}
					else if(codeset == 2){
						/* If identificativoFiscaleEstero is not null and is not equal to NID and is not equal to string null
						 *    Then check if the aggiudicatario is already in the database
						 *         If the aggiudicatario is not in the database
						 *            then insert the aggiudicatario in the tabella aggiudicatari
						 *                 get the id of the aggiudicatario
						 *                 insert the row in the table lotto_aggiudicatari
						 *         Else
						 *             get the id of the aggiudicatario already prensent in the table
						 *             insert the row in the table lotto_aggiudicatari
						 */
						if((!identificativoFiscaleEstero.equalsIgnoreCase("NID"))&&(!identificativoFiscaleEstero.equalsIgnoreCase("null")))
						{
							check_id =  count_number_of_aggiudicatari_idFiscEst(identificativoFiscaleEstero);
							if(check_id == 0)
							{   
								insert_aggiudicatario();
								aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
								fill_lotto_aggiudicatari();
							}
							else
							{
								aggiudicatario_idAggiudicatario = select_id_from_aggiudicatari_idFiscEst(identificativoFiscaleEstero);
								fill_lotto_aggiudicatari();

							}

						}
						else{
							insert_aggiudicatario();
							aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
							fill_lotto_aggiudicatari();
						}
					}
					else if(codeset == 3)
					{
						/*
						 * If the codiceFiscale and the identificativoFiscaleEstero are both not equal to zero
						 * Insert the aggiudicatario in aggiudicatari and lotto_aggiudicatari table
						 */
						insert_aggiudicatario();
						aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
						fill_lotto_aggiudicatari();

					}
					if(codeset == 4)
					{
						/*
						 * If both codiceFiscale and identificativoFiscaleEsstero are null
						 * Insert in both the aggiudicatari and lotto_aggiudicatari table
						 */
						insert_aggiudicatario();
						aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
						fill_lotto_aggiudicatari();
					}
				}
			}
		}
	}

	//---GET SINGLE SUCCESSFUL TENDERER---
	public void getSingleAggiudicatario(){

		id_participant =0;
		//set codesetRuolo to 0 because the aggiudicatario is not part of a group
		codesetRuolo = 0;
		//set ruolo to null because the aggiudicatario is not in a group
		ruolo = null;

		try{
			aggiudicatario = aggiudicatari.getAggiudicatario();
		}catch(NullPointerException e)
		{
			System.out.println("l'aggiudicatario non c'è vado avanti\n");
		}
		if(aggiudicatario != null)
		{
			for(SingoloType agg: aggiudicatario){
				ragioneSociale = agg.getRagioneSociale();
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
				codiceFiscale = agg.getCodiceFiscale();
				identificativoFiscaleEstero = agg.getIdentificativoFiscaleEstero();

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
				if((identificativoFiscaleEstero != null)){
					//check if the tag is present but the value is not assigned
					if(identificativoFiscaleEstero.matches("^\\s*$")){
						identificativoFiscaleEstero = "null";
					}
				}


				if(codeset == 1)
				{
					/*
					 * Check if the codiceFiscale is equal to 000000000000
					 * If it is equal to zero we insert it in the aggiudicatari without checking if it already exists 
					 * we use this codiceFiscale to evaluate an integrity constraint
					 */
					if(codiceFiscale.equalsIgnoreCase("00000000000"))
					{

						insert_aggiudicatario();
						aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
						fill_lotto_aggiudicatari();

					}
					/*if codiceFiscale is not null and it is not equal to NID
					 * then  Check if the aggiudicatario is already in the database
					 *          if the aggiudicatario is not in the database
					 *             then insert the aggiudicatario in the tabella aggiudicatari
					 *                   get the id of the aggiudicatario 
					 *                   insert the row  in the table lotto_aggiudicatari
					 *           else
					 *              get the id of the aggiudicatario already present in the table
					 *              insert the row in the table lotto_aggiudicatari
					 */
					else if ((codiceFiscale != null) && (!codiceFiscale.equalsIgnoreCase("NID")))
					{
						check_id =  count_number_of_aggiudicatari_codFisc(codiceFiscale);
						if(check_id == 0)
						{
							insert_aggiudicatario();
							aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
							fill_lotto_aggiudicatari();
						}
						else
						{
							aggiudicatario_idAggiudicatario = select_id_aggiudicatario_from_aggiudicatari_codFisc(codiceFiscale);
							fill_lotto_aggiudicatari();

						}

					}
					else{

						insert_aggiudicatario();
						aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
						fill_lotto_aggiudicatari();
					}
				}
				else if(codeset == 2)
				{
					/* If identificativoFiscaleEstero is not null and is not equal to NID and is not equal to string null
					 *    Then check if the aggiudicatario is already in the database
					 *         If the aggiudicatario is not in the database
					 *            then insert the aggiudicatario in the tabella aggiudicatari
					 *                 get the id of the aggiudicatario
					 *                 insert the row in the table lotto_aggiudicatari
					 *         Else
					 *             get the id of the aggiudicatario already prensent in the table
					 *             insert the row in the table lotto_aggiudicatari
					 */
					if((!identificativoFiscaleEstero.equalsIgnoreCase("NID"))&& (!identificativoFiscaleEstero.equalsIgnoreCase("null")))
					{
						check_id =  count_number_of_aggiudicatari_idFiscEst(identificativoFiscaleEstero);
						if(check_id == 0)
						{
							insert_aggiudicatario();
							aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
							fill_lotto_aggiudicatari();
						}
						else
						{
							aggiudicatario_idAggiudicatario = select_id_from_aggiudicatari_idFiscEst(identificativoFiscaleEstero);
							fill_lotto_aggiudicatari();

						}


					}
					else{
						insert_aggiudicatario();
						aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
						fill_lotto_aggiudicatari();

					}
				}
				else if(codeset == 3)
				{
					/*
					 * If the codiceFiscale and the identificativoFiscaleEstero are both not equal to zero
					 * Insert the aggiudicatario in aggiudicatari and lotto_aggiudicatari table
					 */
					insert_aggiudicatario();
					aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
					fill_lotto_aggiudicatari();
				}
				else if(codeset == 4)
				{
					/*
					 * If both codiceFiscale and identificativoFiscaleEsstero are null
					 * Insert in both the aggiudicatari and lotto_aggiudicatari table
					 */
					insert_aggiudicatario();
					aggiudicatario_idAggiudicatario = get_aggiudicatario_idAggiudicatario_from_aggiudicatario();
					fill_lotto_aggiudicatari();

				}
			}	
		}
	}

	public int count_number_of_aggiudicatari_codFisc(String codiceFiscale)
	{
		Statement stm = null;
		int counter = 0;
		String query = "SELECT COUNT(*) as counter FROM appalti.aggiudicatari WHERE codiceFiscale = '"+codiceFiscale+"' AND codeset='1'";

		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next())
			{
				counter = rs.getInt("counter");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return counter;
	}

	public int count_number_of_aggiudicatari_idFiscEst(String identificativoFiscaleEstero)
	{
		Statement stm = null;
		int counter = 0;
		String query = "SELECT COUNT(*) as counter FROM appalti.aggiudicatari WHERE identificativoFiscaleEstero = '"+identificativoFiscaleEstero+"' AND codeset='2'";

		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next())
			{
				counter = rs.getInt("counter");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return counter;

	}
	public int get_aggiudicatario_idAggiudicatario_from_aggiudicatario()
	{
		int id = 0;
		Statement stm = null;
		String query = "SELECT idAggiudicatario FROM appalti.aggiudicatari ORDER BY idAggiudicatario DESC LIMIT 1";
		ResultSet rs;

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(query);
			while(rs.next())
			{
				id = rs.getInt("idAggiudicatario");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;

	}

	public void insert_aggiudicatario()
	{
		PreparedStatement pstm;
		String query = "INSERT INTO appalti.aggiudicatari (codiceFiscale,identificativoFiscaleEstero,ragioneSociale,codeset) VALUES ('"+codiceFiscale+"','"+identificativoFiscaleEstero+"','"+ragioneSociale+"','"+codeset+"')";
		try {
			pstm = conn.prepareStatement(query);
			pstm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//This function creates a  new istance of lotto_aggiudicatari class and
	//call the method to insert a new tuple in the lotto_aggiudicatari table
	void fill_lotto_aggiudicatari()
	{
		Lotti_aggiudicatari lottoAgg = new Lotti_aggiudicatari(conn,lotto_idCig,aggiudicatario_idAggiudicatario,ruolo,codesetRuolo);
		lottoAgg.insert_lotto_aggiudicatario();

	}

	public int select_id_aggiudicatario_from_aggiudicatari_codFisc(String codiceFiscale)
	{
		int id = 0;
		Statement stm = null;
		ResultSet rs;
		if((codiceFiscale != null) &&(!codiceFiscale.equalsIgnoreCase("NID")))
		{
			String query = "SELECT idAggiudicatario FROM appalti.aggiudicatari WHERE codiceFiscale = '"+codiceFiscale+"'";
			try {
				stm = conn.createStatement();
				rs = stm.executeQuery(query);
				while(rs.next())
				{
					id = rs.getInt("idAggiudicatario");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
	public int select_id_from_aggiudicatari_idFiscEst(String identificativoFiscaleEstero)
	{
		int id = 0;
		Statement stm = null;
		ResultSet rs;
		if((identificativoFiscaleEstero != null) && (!identificativoFiscaleEstero.equalsIgnoreCase("NID")))
		{
			String query = "SELECT idAggiudicatario FROM appalti.aggiudicatari WHERE identificativoFiscaleEstero = '"+identificativoFiscaleEstero+"'";
			try {
				stm = conn.createStatement();
				rs = stm.executeQuery(query);
				while(rs.next())
				{
					id = rs.getInt("idAggiudicatario");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
	//Function to clean the string
	public String cleanString(String stringa){
		stringa = stringa.replace("'", "");
		stringa = stringa.replace("\\", " ");

		return stringa;

	}

}


