package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Lotti_aggiudicatari {
	
	Connection conn;
	int idLotto;
	int aggiudicatari_idAggiudicatario;
	String ruolo;
	int codesetRuolo;
	
	public Lotti_aggiudicatari(Connection conn,int idLotto,int aggiudicatari_idAggiudicatario ,String ruolo,int codesetRuolo)
	{
		this.conn = conn;
		this.idLotto = idLotto;
		this.aggiudicatari_idAggiudicatario = aggiudicatari_idAggiudicatario;
		this.ruolo = ruolo;
		this.codesetRuolo = codesetRuolo;
		
	}
	
	public void insert_lotto_aggiudicatario()
	{
		 PreparedStatement pstm = null;
	     String query = "INSERT INTO appalti.lotti_aggiudicatari(lotto_idCig,aggiudicatari_idAggiudicatario,ruolo,codesetRuolo) VALUES ('"+idLotto+"','"+aggiudicatari_idAggiudicatario+"','"+ruolo+"','"+codesetRuolo+"')";
	     try {
			pstm = conn.prepareStatement(query);
	        pstm.execute();
	 	} catch (SQLException e) {
           e.printStackTrace();
		}
	}

}
