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

public class CompletenessAggiudicatari {
	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet6;
	public CompletenessAggiudicatari(Connection conn,WritableWorkbook myexcel){
		this.conn = conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet6 = myexcel.createSheet("Completeness Aggiudicatari",5);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CODICE_FISCALE");
		Label l3 = new Label (2,0,"IDENTIFICATIVO_FISCALE_ESTERO");
		Label l4 = new Label (3,0,"RAGIONE_SOCIALE");
		Label l5 = new Label (4,0,"RUOLO");
		Label l6 = new Label (5,0,"ROW COMPLETENESS");
		try {
			sheet6.addCell(l1);
			sheet6.addCell(l2);
			sheet6.addCell(l3);
			sheet6.addCell(l4);
			sheet6.addCell(l5);	
			sheet6.addCell(l6);
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}

	//Compute the completeness of 'codiceFiscale' in the 'aggiudicatari' table
	public void completeness_codiceFiscale_value()
	{

		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_codiceFiscale;
		Statement stm;
		ResultSet rs1, rs2;
		Label lEnte;
		Number codFisc;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lEnte = new Label(0,i,ente);
				try {
					sheet6.addCell(lEnte);
				} catch (RowsExceededException e1) {
					e1.printStackTrace();
				} catch (WriteException e1) {
					e1.printStackTrace();
				}
				String query_count_null_codiceFiscale = "SELECT entePubblicatore,count(*) as invalid_codiceFiscale FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE codiceFiscale='null' AND codeset = '4' and entePubblicatore = '"+ente+"'"; 
				rs2 = stm.executeQuery(query_count_null_codiceFiscale);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_codiceFiscale");
				}
				completeness_codiceFiscale = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" codiceFiscale: "+completeness_codiceFiscale);
				rs2.close();
				if(!Double.isNaN(completeness_codiceFiscale))
				{
					codFisc = new Number(1,i,completeness_codiceFiscale);
					try {
						sheet6.addCell(codFisc);
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
	//Compute the completeness of 'identificativoFiscaleEstero' of the aggiudicatari table 
	public void completeness_identificativoFiscaleEstero_value()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_identificativoFiscaleEstero;
		Statement stm;
		ResultSet rs1,rs2;
		Number idFiscEst;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_identificativoFiscaleEstero = "SELECT entePubblicatore,count(*) AS invalid_identificativoFiscaleEstero FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE identificativoFiscaleEstero = 'null' and (codeset = '4' OR codeset = '2') and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_identificativoFiscaleEstero);
				while(rs2.next())
				{ 
					rm = 0;
					rm = rs2.getDouble("invalid_identificativoFiscaleEstero");
				}
				completeness_identificativoFiscaleEstero = completeness_function(rm,rt);
				System.out.println("ENTE: "+ente+" identificativoFiscaleEstero: "+completeness_identificativoFiscaleEstero);
				rs2.close();
				if(!Double.isNaN(completeness_identificativoFiscaleEstero))
				{
					idFiscEst = new Number(2,i,completeness_identificativoFiscaleEstero);
					try {
						sheet6.addCell(idFiscEst);
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
	//Compute the completeness of the 'ragioneSociale' of the 'aggiudicatari' table
	public void completeness_ragioneSociale_value()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_ragioneSociale;
		Statement stm;
		ResultSet rs1,rs2;
		Number ragSociale;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{ 
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_ragioneSociale = "SELECT entePubblicatore,count(*) as invalid_ragioneSociale FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE ragioneSociale='null' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_ragioneSociale);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_ragioneSociale");
				}
				completeness_ragioneSociale = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" invalid_ragioneSociale: "+completeness_ragioneSociale);
				rs2.close();
				if(!Double.isNaN(completeness_ragioneSociale))
				{
					ragSociale = new Number(3,i,completeness_ragioneSociale);

					try {
						sheet6.addCell(ragSociale);
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

	// Compute the completeness of 'ruolo' in the aggiudicatari table
	public void completeness_ruolo_value()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_ruolo;
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
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_ruolo = "SELECT entePubblicatore,count(*) as invalid_ruolo FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE ruolo = 'null' and codesetRuolo = '1' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_ruolo);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_ruolo");
				}
				completeness_ruolo = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" ruolo: "+completeness_ruolo);
				rs2.close();
				if(!Double.isNaN(completeness_ruolo))
				{
					ruolo = new Number(4,i,completeness_ruolo);
					try {
						sheet6.addCell(ruolo);
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
	//Compute the completeness of the aggiudicatari table
	public void completeness_on_aggiudicatari()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_on_aggiudicatari;
		Statement stm;
		ResultSet rs1,rs2;
		Number rowComp;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_rows = "SELECT entePubblicatore,count(*) AS invalid_rows FROM (appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari AS l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE ((l_a.ruolo = 'null' and l_a.codesetRuolo = '1') OR (codiceFiscale='null' AND codeset = '4') OR (identificativoFiscaleEstero = 'null' and (codeset = '4' OR codeset = '2')) OR (ragioneSociale='null')) and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_rows);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_rows");
				}
				completeness_on_aggiudicatari = completeness_function(rm,rt);
				System.out.println("ENTE: "+ente+" completezza su aggiudicatario: "+completeness_on_aggiudicatari);
				rs2.close();
				if(!Double.isNaN(completeness_on_aggiudicatari))
				{
					rowComp = new Number(5,i,completeness_on_aggiudicatari);
					try {
						sheet6.addCell(rowComp);
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
	//Function to compute the completeness 
	private double completeness_function(double rm, double rt){
		double div;
		double completeness_value;
		div = rm/rt;
		completeness_value = 1-(div);

		return completeness_value;
	}
}
