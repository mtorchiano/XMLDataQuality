package tesi.progettoAppalti;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import generated.legge190_1_0.Pubblicazione;

public class Lotti {

	Pubblicazione.Data.Lotto lotto;
	Pubblicazione.Data.Lotto.StrutturaProponente strutturaProponente;
	Pubblicazione.Data.Lotto.TempiCompletamento tempiCompletamento;

	String metadati = null;
	int id=0;
	String codiceFiscaleProp;
	String denominazione;
	String cig="";
	String oggetto;
	String sceltaContraente;
	String importoAggiudicazione_string;
	BigDecimal importoAggiudicazione;
	String importoSommeLiquidate_string;
	BigDecimal importoSommeLiquidate;
	String dataInizio;
	String dataUltimazione;
	Connection conn;


	/* 
	 * The flagAgg is associated to the importoAggiudicazione element
	 * 
	 * importoAggiudicazione out of domain ---> flagAgg = 1    importoAggiudicazione =  0,00
	 * importoAggiudicazione empty         ---> flagAgg = 1    importoAggiudicazione =  0,00
	 * importoAggiudicazione not present   ---> flagAgg = 2    importoAggiudicazione =  0,00
	 */
	int flagAgg = 0;
	/*
	 * The flagSommeLiq is associated to the importoSommeLiquidateElement
	 * 
	 * importoSommeLiquidate out of domain ---> flagAgg = 1    importoSommeLiquidate = 0,00
	 * importoSommeLiquidate empty         ---> flagAgg = 1    importoSommeLiquidate = 0,00
	 * importoSommeLiquidate not present   ---> flagAgg = 2    importoSommeLiquidate = 0,00
	 */
	int flagSommeLiq = 0;

	public Lotti(Pubblicazione.Data.Lotto lotto,Connection conn,String metadati){
		this.lotto = lotto;
		this.conn = conn;
		this.metadati = metadati;

	}

	void getAllDataLotto(){

		cig = lotto.getCig();

		//check lotto
		if(cig != null){
			if(!cig.matches("[a-zA-Z0-9\" \"]{10}")){
				// cig out of the domain: insert a NID value in the database
				cig="NID";

			}
		}
		//getOggetto
		getOggetto();
		//getSceltaContraente
		getSceltaContraente();
		//getImportoAggiudicazione
		getImportoAggiudicazione();
		//getImportoSommeLiquidate
		getImportoSommeLiquidate();
		//get the tempiCompletamento for each lotto
		getTempiCompletamentoElement();
		//get the strutturaProponente for each lotto
		getStrutturaProponenteElement();



	}
	public String getCig(){
		cig = lotto.getCig();
		return cig;
	}

	public void getOggetto(){
		oggetto = lotto.getOggetto();

		// Check if the oggetto element matches the domain as specified in the XML Schema
		if(oggetto != null){
			if(!oggetto.matches(".{1,250}")){

				// This check is used in the case of empty value
				// If oggetto is empty, a 'null' is inserted in the corresponding value of the oggetto field
				if(oggetto.matches("^\\s*$")){
					oggetto = "null";
				}
				else{
					oggetto = "NID";
				}
			}
			else if(oggetto.matches("^\\s*$")){
				oggetto = "null";
			}
			else {
				oggetto = cleanString(oggetto);
			}  

		} 
	}
	public void getSceltaContraente(){
		sceltaContraente = lotto.getSceltaContraente();
		/*
		 * If sceltaContrante is not null and its value is not one specified in the XML Schema
		 * Then assign NID to it.
		 */
		if(sceltaContraente != null)
		{
			if(!sceltaContraente.matches("01-PROCEDURA APERTA")
					&& !sceltaContraente.matches("02-PROCEDURA RISTRETTA")
					&& !sceltaContraente.matches("03-PROCEDURA NEGOZIATA PREVIA PUBBLICAZIONE DEL BANDO")
					&& !sceltaContraente.matches("04-PROCEDURA NEGOZIATA SENZA PREVIA PUBBLICAZIONE DEL BANDO")
					&& !sceltaContraente.matches("05-DIALOGO COMPETITIVO")
					&& !sceltaContraente.matches("06-PROCEDURA NEGOZIATA SENZA PREVIA INDIZIONE DI GARA ART. 221 D.LGS. 163/2006")
					&& !sceltaContraente.matches("07-SISTEMA DINAMICO DI ACQUISIZIONE")
					&& !sceltaContraente.matches("08-AFFIDAMENTO IN ECONOMIA - COTTIMO FIDUCIARIO")
					&& !sceltaContraente.matches("14-PROCEDURA SELETTIVA EX ART 238 C.7, D.LGS. 163/2006")
					&& !sceltaContraente.matches("17-AFFIDAMENTO DIRETTO EX ART. 5 DELLA LEGGE N.381/91")
					&& !sceltaContraente.matches("21-PROCEDURA RISTRETTA DERIVANTE DA AVVISI CON CUI SI INDICE LA GARA")
					&& !sceltaContraente.matches("22-PROCEDURA NEGOZIATA DERIVANTE DA AVVISI CON CUI SI INDICE LA GARA")
					&& !sceltaContraente.matches("23-AFFIDAMENTO IN ECONOMIA - AFFIDAMENTO DIRETTO")
					&& !sceltaContraente.matches("24-AFFIDAMENTO DIRETTO A SOCIETA' IN HOUSE")
					&& !sceltaContraente.matches("25-AFFIDAMENTO DIRETTO A SOCIETA' RAGGRUPPATE/CONSORZIATE O CONTROLLATE NELLE CONCESSIONI DI LL.PP")
					&& !sceltaContraente.matches("26-AFFIDAMENTO DIRETTO IN ADESIONE AD ACCORDO QUADRO/CONVENZIONE")
					&& !sceltaContraente.matches("27-CONFRONTO COMPETITIVO IN ADESIONE AD ACCORDO QUADRO/CONVENZIONE")
					&& !sceltaContraente.matches("28-PROCEDURA AI SENSI DEI REGOLAMENTI DEGLI ORGANI COSTITUZIONALI")){

				sceltaContraente = "NID";
			}
			else{
				sceltaContraente = cleanString(sceltaContraente);
			}
		}
	}
	public void getStrutturaProponenteElement(){
		try{
			strutturaProponente = lotto.getStrutturaProponente();
		}catch(NullPointerException e){
		}
		if(strutturaProponente != null)
		{
			codiceFiscaleProp = strutturaProponente.getCodiceFiscaleProp();
			/*
			 *  If codiceFiscaleProp is not null and its value is not in the domain (Specified in the XML Schema)
			 *  Then assign NID value
			 */
			if(codiceFiscaleProp != null){
				if(!codiceFiscaleProp.matches("[A-Za-z]{6}[0-9]{2}[A-Za-z]{1}[0-9]{2}[A-Za-z]{1}[0-9A-Za-z]{3}[A-Za-z]{1}") 
						&& !codiceFiscaleProp.matches("[A-Za-z]{6}[0-9LMNPQRSTUV]{2}[A-Za-z]{1}[0-9LMNPQRSTUV]{2}[A-Za-z]{1}[0-9LMNPQRSTUV]{3}[A-Za-z]{1}")
						&& !codiceFiscaleProp.matches("[0-9]{11,11}")){
					codiceFiscaleProp = "NID";
				}
			}

			denominazione = strutturaProponente.getDenominazione();
			if(denominazione != null){

				if(!denominazione.matches(".{1,250}")){
					System.out.println("LENGHT: "+oggetto.length());
					if(denominazione.matches("^\\s*$")){
						denominazione = "null";
					}
					else{
						denominazione = "NID";
					}
				}
				else if(denominazione.matches("^\\s*$")){
					denominazione = "null";
				}
				else {
					denominazione = cleanString(denominazione);
				}


			} 
		}

	}
	public void getImportoAggiudicazione()
	{

		//Initiate again the flagAgg to zero
		flagAgg = 0;
		importoAggiudicazione_string = lotto.getImportoAggiudicazione();

		/*
		 *If importoAggiudicazione exist but the value is blank
		 *insert 0 and change the FlagAgg to 1
		 *If importoAggiudicazione tag does not exist 
		 *insert 0 and change the FlagAgg to 2
		 *If ImportoAggiudicazione is not equal to null and the value is not in domain
		 *insert 0 and change the FlagAgg to 1
		 */
		if(importoAggiudicazione_string == null)
		{
			importoAggiudicazione =new BigDecimal("0"+"."+"00");
			flagAgg = 2;
		}
		else if(importoAggiudicazione_string != null)
		{

			if(!importoAggiudicazione_string.matches("([0-9]{1,12}\\.[0-9]{1,2})|([0-9]{1,15})|(\\.[0-9]{1,2})|([0-9]{1,12}\\.)"))
			{
				importoAggiudicazione =new BigDecimal("0"+"."+"00");
				flagAgg = 1;
			}
			else
			{
				importoAggiudicazione =new BigDecimal(importoAggiudicazione_string);

			}

		}

	}

	public void getImportoSommeLiquidate(){
		//Initialize the flagSommeLiq to zero
		flagSommeLiq = 0;
		importoSommeLiquidate_string = lotto.getImportoSommeLiquidate();

		/*
		 *If importoSommeLiquidate_string exist but the value is blank
		 *insert 0.00 and change the FlagAgg to 1
		 *If importoSommeLiquidate_string tag does not exist 
		 *insert 0.00 and change the FlagAgg to 2
		 *If importoSommeLiquidate_string is not equal to null and the value is not in domain
		 *insert 0.00 and change the FlagAgg to 1
		 */
		if(importoSommeLiquidate_string == null)
		{
			//CASE1: tag not present, flag = 2
			importoSommeLiquidate = new BigDecimal("0"+"."+"00");
			flagSommeLiq = 2;
		}
		else if(importoSommeLiquidate_string != null)
		{
			if(!importoSommeLiquidate_string.matches("([0-9]{1,12}\\.[0-9]{1,2})|([0-9]{1,15})|(\\.[0-9]{1,2})|([0-9]{1,12}\\.)"))
			{
				importoSommeLiquidate = new BigDecimal("0"+"."+"00");
				flagSommeLiq = 1;
			}
			else
			{
				importoSommeLiquidate = new BigDecimal(importoSommeLiquidate_string);
			}
		}

	}	


	public void getTempiCompletamentoElement(){

		try
		{
			tempiCompletamento = lotto.getTempiCompletamento();	
		}catch(NullPointerException e )
		{}
		if(tempiCompletamento != null)
		{
			//Get Data Inizio
			dataInizio = tempiCompletamento.getDataInizio();
			/*If dataInizio != null and the date is in  the domain 
			 * store the data as is
			 * If it is not in domain 
			 * store the date: 0001-01-01
			 */
			if(dataInizio != null)
			{
				if(!dataInizio.matches("(([0-9]{4})\\-((0[13578])|(1[02]))\\-((0[1-9])|(1[0-9])|(2[0-9])|3[01]))|(([0-9]{4})\\-(02)\\-((0[1-9])|(1[0-9])|(2[0-9])))|(([0-9]{4})\\-((0[469])|11)\\-((0[1-9])|(1[0-9])|(2[0-9])|(30)))"))
                {
					dataInizio = "0001-01-01";
				}
			}
			//Get Data Ultimazione
			dataUltimazione = tempiCompletamento.getDataUltimazione();
			/*IF dataultimazione != null and the date is in the domain
			 * store the data as is
			 * If it is not in domain 
			 * store the date: 3999-12-31
			 */
			if(dataUltimazione != null)
			{
				if(!dataUltimazione.matches("(([0-9]{4})\\-((0[13578])|(1[02]))\\-((0[1-9])|(1[0-9])|(2[0-9])|3[01]))|(([0-9]{4})\\-(02)\\-((0[1-9])|(1[0-9])|(2[0-9])))|(([0-9]{4})\\-((0[469])|11)\\-((0[1-9])|(1[0-9])|(2[0-9])|(30)))"))
				{
					dataUltimazione = "3999-12-31";
				}

			}
		}



	}

//Insert "lotto" in "appalti" database
	public void insert_lotto_database(){
		PreparedStatement pstm;
		try {
			//minOccurs of dataUltimazione is zero: when the value is null, insert null in the database
			if((dataInizio == null) && (dataUltimazione == null)){
				pstm = conn.prepareStatement("INSERT INTO appalti.lotti(cig,codiceFiscaleProp,denominazione,oggetto,sceltaContraente,importoAggiudicazione,dataInizio,dataUltimazione,importoSommeLiquidate,metadati_urlFile,flagAgg,flagSommeLiq) VALUES ('"+cig+"','"+codiceFiscaleProp+"','"+denominazione+"','"+oggetto+"','"+sceltaContraente+"','"+importoAggiudicazione+"',null,null,'"+importoSommeLiquidate+"','"+metadati+"','"+flagAgg+"','"+flagSommeLiq+"')");
				pstm.execute();
			}
			else if((dataInizio == null) && (dataUltimazione != null)){
				pstm = conn.prepareStatement("INSERT INTO appalti.lotti(cig,codiceFiscaleProp,denominazione,oggetto,sceltaContraente,importoAggiudicazione,dataInizio,dataUltimazione,importoSommeLiquidate,metadati_urlFile,flagAgg,flagSommeLiq) VALUES ('"+cig+"','"+codiceFiscaleProp+"','"+denominazione+"','"+oggetto+"','"+sceltaContraente+"','"+importoAggiudicazione+"',null,'"+dataUltimazione+"','"+importoSommeLiquidate+"','"+metadati+"','"+flagAgg+"','"+flagSommeLiq+"')");
				pstm.execute();
			}
			else if((dataInizio != null) && (dataUltimazione == null)){
				pstm = conn.prepareStatement("INSERT INTO appalti.lotti(cig,codiceFiscaleProp,denominazione,oggetto,sceltaContraente,importoAggiudicazione,dataInizio,dataUltimazione,importoSommeLiquidate,metadati_urlFile,flagAgg,flagSommeLiq) VALUES ('"+cig+"','"+codiceFiscaleProp+"','"+denominazione+"','"+oggetto+"','"+sceltaContraente+"','"+importoAggiudicazione+"','"+dataInizio+"',null,'"+importoSommeLiquidate+"','"+metadati+"','"+flagAgg+"','"+flagSommeLiq+"')");
				pstm.execute();
			}
			else if((dataInizio != null) && (dataUltimazione !=null)){
				pstm = conn.prepareStatement("INSERT INTO appalti.lotti(cig,codiceFiscaleProp,denominazione,oggetto,sceltaContraente,importoAggiudicazione,dataInizio,dataUltimazione,importoSommeLiquidate,metadati_urlFile,flagAgg,flagSommeLiq) VALUES ('"+cig+"','"+codiceFiscaleProp+"','"+denominazione+"','"+oggetto+"','"+sceltaContraente+"','"+importoAggiudicazione+"','"+dataInizio+"','"+dataUltimazione+"','"+importoSommeLiquidate+"','"+metadati+"','"+flagAgg+"','"+flagSommeLiq+"')");
				pstm.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//select a "Lotto" from "appalti" database with cig equal to the one passed by argument
	public int select_lotto_database(String cigPassed){
		int id=0;
		Statement stm = null;

		String query = "select idCig FROM appalti.lotti WHERE cig ='"+cigPassed+"'";
		ResultSet rs;

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(query);
			while(rs.next()){
				id = rs.getInt("idCig");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public int get_idCig_from_lotto(){
		int id=0;
		Statement stm = null;
		String query = "SELECT idCig FROM appalti.lotti ORDER by idCig DESC LIMIT 1";
		ResultSet rs;

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(query);
			while(rs.next()){
				id = rs.getInt("idCig");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;

	}
	//Function used to clean the strings
	public String cleanString(String stringa){
		stringa = stringa.replace("'", " ");
		stringa = stringa.replace("\\", " ");
		return stringa;

	}


}
