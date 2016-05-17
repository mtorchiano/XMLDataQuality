package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;



public class Lotti_partecipanti {

	Connection conn;
	int idLotto;
	List <Integer> idPartecipante;
	int partecipante_idPartecipante;
	String ruolo;
	int codesetRuolo;
	
	public Lotti_partecipanti(Connection conn, int idLotto, int partecipante_idPartecipante,String ruolo,int codesetRuolo ){
		this.conn = conn;
		this.idLotto = idLotto;
		this.partecipante_idPartecipante = partecipante_idPartecipante;
		this.ruolo = ruolo;
		this.codesetRuolo = codesetRuolo;
		
	}
	public void insert_lotto_partecipanti_database(){
	
		PreparedStatement pstm = null;
		try {
			String query = "INSERT INTO appalti.lotti_partecipanti (lotto_idCig,partecipante_idPartecipante,ruolo,codesetRuolo) VALUES ('"+idLotto+"','"+partecipante_idPartecipante+"','"+ruolo+"','"+codesetRuolo+"')";	
		    pstm = conn.prepareStatement(query);
			pstm.execute();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		
	}
}
