package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import generated.legge190_1_0.Pubblicazione;
import javax.xml.datatype.XMLGregorianCalendar;

public class Metadati {
	Pubblicazione.Metadata metadati;
	String titolo;
	String abstractTag;
	XMLGregorianCalendar dataPubblicazioneDataset;
	String entePubblicatore;
	XMLGregorianCalendar dataUltimoAggiornamentoDataset;
	int annoRiferimento;
	String urlFile;
	Object licenza;
	public Metadati(){

	}
	//Get metadati from the xml file
	public void getdata(Pubblicazione pubblicazione){
		metadati = pubblicazione.getMetadata();
		titolo = metadati.getTitolo();
		titolo = cleanString(titolo);
		abstractTag = metadati.getAbstract();
		abstractTag = cleanString(abstractTag);
		dataPubblicazioneDataset = metadati.getDataPubbicazioneDataset();
		entePubblicatore = metadati.getEntePubblicatore();
		entePubblicatore = cleanString(entePubblicatore);
		dataUltimoAggiornamentoDataset = metadati.getDataUltimoAggiornamentoDataset();
		annoRiferimento = metadati.getAnnoRiferimento();
		licenza = metadati.getLicenza();
		urlFile = getKey();
	}

	//Query to insert the metadata in the appalti database
	public void insert_metadata_in_database(Connection conn){
		PreparedStatement pstm;

		try {
			pstm = conn.prepareStatement("INSERT INTO appalti.metadati(titolo,abstractTag,dataPubblicazioneDataset,entePubblicatore,dataUltimoAggiornamentoDataset,annoRiferimento,urlFile,licenza) VALUES ('"+titolo+"','"+abstractTag+"','"+dataPubblicazioneDataset+"','"+entePubblicatore+"','"+dataUltimoAggiornamentoDataset+"','"+annoRiferimento+"','"+urlFile+"','"+licenza+"')");
			pstm.execute();
		} catch (SQLException e) {
			System.out.println(" "+e.getErrorCode());
		}
	}

	/*Query  to verify if the url of the dataset passed as argument is already in the table metadati
	 *it returns a boolean
	 *true if it is present, false otherwise
	 */ 
	public boolean select_url_in_database(Connection conn,String url_metadati){
		boolean in_db = false;
		int n = 0;
		Statement stm = null;
		String query = "select COUNT(*) from appalti.metadati where urlFile='"+url_metadati+"'";
		ResultSet rs;

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(query);
			while(rs.next()){
				n = rs.getInt(1);
			}
			if(n != 0){
				in_db = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return in_db;
	}
	//The url specified in the  metadata is  the key of the table metadata
	public String getKey(){
		urlFile = metadati.getUrlFile();
		return urlFile;
	}
	//Function used to clean the strings
	public String cleanString(String stringa){

		stringa = stringa.replace("'", " ");
		stringa = stringa.replace("\\", " ");

		return stringa;

	}
}
