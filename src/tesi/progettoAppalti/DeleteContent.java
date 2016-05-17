package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteContent {

	public DeleteContent()
	{
	
	}
   
    public void delete()
    {
    	//DATABASE CONNECTION
    	String url = "jdbc:mysql://localhost:3306/";
    	String dbName = "appalti";
    	String driver  = "com.mysql.jdbc.Driver";
    	String userName = "root";
    	String password = "ADMIN";

    	try {
			Class.forName(driver).newInstance();
		
        Connection conn = DriverManager.getConnection(url+dbName,userName,password);
    	PreparedStatement pstm;
    	String delete_lotto_aggiudicatario = "DELETE FROM appalti.lotti_aggiudicatari";
    	String delete_aggiudicatari = "DELETE FROM appalti.aggiudicatari";
    	String delete_lotto_partecipanti = "DELETE FROM appalti.lotti_partecipanti";
    	String delete_lotto = "DELETE FROM appalti.lotti";
    	String delete_metadati = "DELETE FROM appalti.metadati";
    	String delete_partecipanti = "DELETE FROM appalti.partecipanti";
    	String reset_aggiudicatari = "ALTER TABLE appalti.aggiudicatari AUTO_INCREMENT = 1";
    	String reset_lotto = "ALTER TABLE appalti.lotti AUTO_INCREMENT = 1";
    	String reset_lotto_aggiudicatari = "ALTER TABLE appalti.lotti_aggiudicatari AUTO_INCREMENT = 1";
    	String reset_lotto_partecipanti = "ALTER TABLE appalti.lotti_partecipanti AUTO_INCREMENT = 1";
    	String reset_lotto_metadati = "ALTER TABLE appalti.metadati AUTO_INCREMENT = 1";
    	String reset_partecipanti = "ALTER TABLE appalti.partecipanti AUTO_INCREMENT = 1";
    	
    	try {
			pstm = conn.prepareStatement(delete_lotto_aggiudicatario);
			pstm.execute();
			pstm = conn.prepareStatement(delete_aggiudicatari);
			pstm.execute();
			pstm = conn.prepareStatement(delete_lotto_partecipanti);
			pstm.execute();
			pstm = conn.prepareStatement(delete_lotto);
			pstm.execute();
			pstm = conn.prepareStatement(delete_metadati);
			pstm.execute();
			pstm = conn.prepareStatement(delete_partecipanti);
			pstm.execute();
			pstm = conn.prepareStatement(reset_aggiudicatari);
			pstm.execute();
			pstm = conn.prepareStatement(reset_lotto);
			pstm.execute();
			pstm = conn.prepareStatement(reset_lotto_aggiudicatari);
			pstm.execute();
			pstm = conn.prepareStatement(reset_lotto_partecipanti);
			pstm.execute();
			pstm = conn.prepareStatement(reset_lotto_metadati);
			pstm.execute();
			pstm = conn.prepareStatement(reset_partecipanti);
			pstm.execute();
			
			
			
		 conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	} catch (Exception e ) {
    		
			e.printStackTrace();
		}
    	
    }
}
