package tesi.progettoAppalti;

import generated.legge190_1_0.Pubblicazione;
import java.sql.Connection;
import java.util.List;

public class ManagementLotti {
 
	List<Pubblicazione.Data.Lotto> lista_lotti;
	String metadati = null; 
    int zeroCodFiscale = 0;
    
    //Class constructor
	public ManagementLotti(List<Pubblicazione.Data.Lotto> lista_lotti,String metadati){
		this.lista_lotti = lista_lotti;
		this.metadati= metadati;
	}
	public void parseSigleLotti(Connection conn){
		int size_lista_lotti;
		int lotto_idCig;
		
		/* Determine the size of lista_lotti*/
		size_lista_lotti = lista_lotti.size();
		System.out.println("----SIZE LISTA LOTTI: "+size_lista_lotti+"----");
		
		for(Pubblicazione.Data.Lotto lotto : lista_lotti){
			//for each lotto: istanciate a new object lotto
			Lotti lotto_new = new Lotti(lotto,conn,metadati);
		
			//RETRIEVE THE DATA FOR LOTTO
			lotto_new.getAllDataLotto();
			//INSERT THE LOTTO DATA IN THE DATABASE
			lotto_new.insert_lotto_database();
			//RETRIEVE THE CIG OF THE LOTTO
	    	//lotto_idCig = lotto_new.select_lotto_database(cigLotto);
		    lotto_idCig = lotto_new.get_idCig_from_lotto();
			//GET DATA OF "PARTECIPANTI" FROM EACH LOTTO
			Pubblicazione.Data.Lotto.Partecipanti partecipanti = lotto.getPartecipanti();
		    Partecipanti partecipante = new Partecipanti(partecipanti, conn,lotto_idCig);
	        //FUNCTION USED TO GET THE PARTICIPANT WHICH PARTICIPATE TO THE lotto AS GROUP
		    partecipante.getSingleRaggruppamento(); 
		    //FUNCTION USED TO GET THE SINGLE PARTICIPANT IN THE lotto AND TO SAVE THEM IN THE partecipanti TABLE
		    partecipante.getSingleParticipant();
		    int zero = partecipante.returncode();
		    zeroCodFiscale = zeroCodFiscale +zero;

		    //GET THE DATA OF "AGGIUDICATARIO" FOR EACH LOTTO
		    Pubblicazione.Data.Lotto.Aggiudicatari aggiudicatari = lotto.getAggiudicatari();
		    Aggiudicatari singleAggiudicatario = new Aggiudicatari(aggiudicatari,conn, lotto_idCig);
		   //FUNCTION USED TO GET THE AGGIUDICATARI WHICH PARTICIPARE TO THE lotto  AS GROUP
            singleAggiudicatario.getSingleRaggruppamento();
            singleAggiudicatario.getSingleAggiudicatario();
		   
		    
		    
		
			
		}
	}
	//funzione per contare il numero di codici fiscali a zero
	//RIMUOVERE ALLA FINE
public int returnc(){
	return zeroCodFiscale;
}
}
