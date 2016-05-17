package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SceltaContraenteFrequency {
	Connection conn;
	public SceltaContraenteFrequency(Connection conn)
	{
		this.conn=conn;
		
	}
	public void countSceltaContraente(){
		
		ResultSet rs1,rs2;
		Statement stm1;
		double tr = 0;
		double freq = 0;
		String ente = null;
		String scelta = null;
		double perc_freq = 0;
		try {
	    	//call the function to compute the total number of rows in the lotto table
		    rs1 =count_total_number_of_row();
	        stm1 = conn.createStatement();
	        while(rs1.next())
	        {
	        	tr = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query = "SELECT  sceltaContraente as scelta , count(*) as freq  FROM appalti.metadati as m left join appalti.lotti as l on m.urlFile = l.metadati_urlFile where entePubblicatore= '"+ente+"' group by  sceltaContraente";
	             rs2 = stm1.executeQuery(query);
	             while(rs2.next())
	             {
	            	 scelta = rs2.getString("scelta");
	            	 freq = rs2.getDouble("freq");
	            	 perc_freq = compute_freq(freq,tr);
	            	 System.out.println("ENTE: "+ente+" SCELTA CONTRAENTE: "+scelta+" PERCENTUALE: "+perc_freq);
	             }
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	
private double compute_freq(double freq,double tr)
{
   double perc_freq = 0;
    perc_freq = (freq / tr)*100; 
   return perc_freq;
}
	
	//Function used to count the total number of row in the table 'lotto'
	private ResultSet count_total_number_of_row(){

		ResultSet rs = null;
		Statement stm;
		String query_count_total_number_of_row = "SELECT entePubblicatore as ente, count(*) as total_rows FROM appalti.metadati AS m LEFT JOIN appalti.lotti AS l ON m.urlFile = l.metadati_urlFile  GROUP BY entePubblicatore"; 

		try {

			stm = conn.createStatement();
			rs = stm.executeQuery(query_count_total_number_of_row);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return rs;
	}
}
