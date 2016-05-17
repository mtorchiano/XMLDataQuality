package tesi.progettoAppalti;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class AccuracyAggiudicatari {
	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet5;
	public AccuracyAggiudicatari(Connection conn,WritableWorkbook myexcel)
	{
		this.conn=conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet5 = myexcel.createSheet("Accuracy Aggiudicatari", 4);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CODICE_FISCALE");
		Label l3 = new Label (2,0,"IDENTIFICATIVO_FISCALE_ESTERO");
		Label l4 = new Label (3,0,"RAGIONE_SOCIALE");
		Label l5 = new Label (4,0,"RUOLO");
		try {
			sheet5.addCell(l1);
			sheet5.addCell(l2);
			sheet5.addCell(l3);
			sheet5.addCell(l4);
			sheet5.addCell(l5);	
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}

	//Compute the accuracy of codiceFiscale
	public void accuracy_codiceFiscale_value()
	{
		double csfr = 0;
		double ct = 0;
		String ente = null;
		double accuracy_codiceFiscale;
		Statement stm;
		ResultSet rs1,rs2;
		Label lEnte;
		Number codiceFiscale;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lEnte = new Label(0,i,ente);
				try {
					sheet5.addCell(lEnte);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				String query_count_invalid_codiceFiscale = "SELECT entePubblicatore,count(*) AS invalid_codiceFiscale FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE codiceFiscale = 'NID' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_codiceFiscale);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_codiceFiscale");
				}
				accuracy_codiceFiscale = accuracy_function(csfr, ct);
				System.out.println("ente: "+ente+" codiceFiscale: "+accuracy_codiceFiscale);
				rs2.close();
				if(!Double.isNaN(accuracy_codiceFiscale))
				{
					codiceFiscale = new Number(1,i,accuracy_codiceFiscale);
					try {
						sheet5.addCell(codiceFiscale);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Compute the accuracy on 'identificativoFiscaleEstero'
	public void accuracy_identificativoFiscaleEstero_value()
	{

		double csfr = 0;
		double ct = 0;
		String ente = null;
		double accuracy_identificativoFiscaleEstero;
		Statement stm;
		ResultSet rs1,rs2;
		Number ideFiscEst;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_identificativoFiscaleEstero = "SELECT entePubblicatore,count(*) AS invalid_identificativoFiscaleEstero FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE identificativoFiscaleEstero = 'NID' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_identificativoFiscaleEstero);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_identificativoFiscaleEstero");
				}
				accuracy_identificativoFiscaleEstero = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" identificativoFiscaleEstero: "+accuracy_identificativoFiscaleEstero);
				rs2.close();
				if(!Double.isNaN(accuracy_identificativoFiscaleEstero))
				{
					ideFiscEst = new Number(2,i,accuracy_identificativoFiscaleEstero);
					try {
						sheet5.addCell(ideFiscEst);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void accuracy_ragioneSociale_value()
	{
		double csfr = 0;
		double ct = 0;
		double accuracy_ragioneSociale;
		String ente = null;
		Statement stm;
		ResultSet rs1,rs2;
		Number ragioneSociale;
		try {
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_ragioneSociale = "SELECT entePubblicatore,count(*) invalid_ragioneSociale FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE ragioneSociale = 'NID' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_ragioneSociale);
				while(rs2.next())
				{
					csfr = 0;
					csfr =  rs2.getDouble("invalid_ragioneSociale");
				}
				accuracy_ragioneSociale = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" ragioneSociale: "+accuracy_ragioneSociale);
				rs2.close();
				if(!Double.isNaN(accuracy_ragioneSociale))
				{
					ragioneSociale = new Number(3,i,accuracy_ragioneSociale);
					try {
						sheet5.addCell(ragioneSociale);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Compute the accuracy of 'ruolo'
	public void accuracy_ruolo_value()
	{
		double csfr = 0;
		double ct = 0;
		String ente = null;
		double accuracy_ruolo;
		Statement stm;
		ResultSet rs1,rs2;
		Number ruolo;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_ruolo = "SELECT entePubblicatore,count(*) as invalid_ruolo FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE l_a.ruolo = 'NID' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_ruolo);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_ruolo");
				}
				accuracy_ruolo = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" ruolo: "+accuracy_ruolo);
				rs2.close();
				if(!Double.isNaN(accuracy_ruolo))
				{
					ruolo = new Number(4,i,accuracy_ruolo);
					try {
						sheet5.addCell(ruolo);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Function used to compute the total number of rows
	private ResultSet count_total_number_of_row(){
		Statement stm;
		ResultSet rs = null;

		String count_number_of_row = "SELECT count(distinct(idAggiudicatario)) AS total_rows, entePubblicatore AS ente FROM appalti.metadati AS m LEFT JOIN appalti.lotti AS l ON m.urlFile = l.metadati_urlFile LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON l.idCig = l_a.lotto_idCig LEFT JOIN appalti.aggiudicatari AS a ON l_a.aggiudicatari_idAggiudicatario = a.idAggiudicatario GROUP BY entePubblicatore";
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(count_number_of_row);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;

	}
	//Function used to compute the accuracy
	private double accuracy_function(double csfr,double ct){
		double div;
		double accuracy_value;

		div = csfr/ct;
		accuracy_value = 1-div;

		return accuracy_value;
	}
}
