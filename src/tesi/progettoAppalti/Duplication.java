package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import jxl.write.Number;

public class Duplication {

	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet7;
	WritableSheet sheet8;
	public Duplication(Connection conn,WritableWorkbook myexcel)
	{
		this.conn = conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet7 = myexcel.createSheet("Duplication_Partecipanti",6);
		sheet8 = myexcel.createSheet("Duplication_Aggiudicatari",7);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"DUPLICATES");
		Label lA1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label lA2 = new Label (1,0,"DUPLICATES");

		try {
			sheet7.addCell(l1);
			sheet7.addCell(l2);
			sheet8.addCell(lA1);
			sheet8.addCell(lA2);
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}
	public void duplication_partecipanti()
	{
		Statement stm;
		ResultSet rs;
		double dup = 0;
		String ente = null;
		Label lPartecipante;
		Number partdup;
		String query_count_duplicates = "SELECT entePubblicatore AS ente ,SUM(duplicates) as duplicates FROM (SELECT entePubblicatore, lotto_idCig,l_p.partecipante_idPartecipante,(count(*)-1) as duplicates FROM (appalti.lotti_partecipanti AS l_p LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig) LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile GROUP BY  entePubblicatore, lotto_idCig, l_p.partecipante_idPartecipante HAVING count(*)>1) AS A GROUP BY entePubblicatore";

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(query_count_duplicates);
			int i = 1;
			while(rs.next())
			{
				ente = rs.getString("ente");
				dup = rs.getDouble("duplicates");
				System.out.println("ENTE "+ente+" dup: "+dup);
				lPartecipante = new Label(0,i,ente);
				partdup = new Number (1,i,dup);
				try {
					sheet7.addCell(lPartecipante);
					sheet7.addCell(partdup);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				i++; 
			}
			rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void duplication_aggiudicatari()
	{
		Statement stm;
		ResultSet rs = null;
		double dup = 0;
		String ente = null;
		Label lagg;
		Number aggdup;
		String query_counts_duplicates = "SELECT entePubblicatore AS ente, sum(duplicates) as duplicates FROM (SELECT entePubblicatore,lotto_idCig,aggiudicatari_idAggiudicatario,(count(*)-1) as duplicates FROM(appalti.lotti_aggiudicatari AS l_a LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig) LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile GROUP BY entePubblicatore,lotto_idCig, aggiudicatari_idAggiudicatario HAVING count(*)>1) AS A GROUP BY entePubblicatore";
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(query_counts_duplicates);
			int i = 1;
			while(rs.next())
			{
				ente = rs.getString("ente");
				dup = rs.getDouble("duplicates");
				System.out.println("ENTE "+ente+" dup: "+dup);
				lagg = new Label(0,i,ente);
				aggdup = new Number(1,i,dup);
				try {
					sheet8.addCell(lagg);
					sheet8.addCell(aggdup);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				i++;
			}

			rs.close();
			stm.clearWarnings();
		} catch (SQLException e) {
			e.printStackTrace();
		} 

	}
}
