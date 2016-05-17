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

public class AccuracyPartecipanti {
	Connection conn;
	Statement stm;
	ResultSet rs;
	WritableWorkbook myexcel;
	WritableSheet sheet3;
	public AccuracyPartecipanti(Connection conn,WritableWorkbook myexcel){
		this.conn = conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet3 = myexcel.createSheet("Accuracy Partecipanti", 2);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CODICE_FISCALE");
		Label l3 = new Label (2,0,"IDENTIFICATIVO_FISCALE_ESTERO");
		Label l4 = new Label (3,0,"RAGIONE_SOCIALE");
		Label l5 = new Label (4,0,"RUOLO");
		try {
			sheet3.addCell(l1);
			sheet3.addCell(l2);
			sheet3.addCell(l3);
			sheet3.addCell(l4);
			sheet3.addCell(l5);	
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}

	//Compute the accuracy of 'codiceFiscale' in table 'partecipanti'
	public void accuracy_codiceFiscale_value(){
		double csfr  = 0;
		double ct = 0;
		String ente = null;
		double accuracy_codiceFiscale;
		Statement stm;
		ResultSet rs1,rs2;
		Label lEnte;
		Number codiceFiscale;
		try{
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lEnte = new Label(0,i,ente);
				try {
					sheet3.addCell(lEnte);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				String query_count_invalid_ruolo = "SELECT entePubblicatore,count(*) as invalid_codiceFiscale FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile where codiceFiscale = 'NID' AND entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_ruolo);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_codiceFiscale");

				}
				accuracy_codiceFiscale = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" codiceFiscale: "+accuracy_codiceFiscale);
				rs2.close();
				if(!Double.isNaN(accuracy_codiceFiscale))
				{
					codiceFiscale = new Number(1,i,accuracy_codiceFiscale);
					try {
						sheet3.addCell(codiceFiscale);
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
	//Compute the accuracy of 'identificativoFiscaleEstero' in table 'partecipanti'
	public void accuracy_identificativoFiscaleEstero_value(){
		double csfr  = 0;
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
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_identificativoFiscaleEstero = "SELECT entePubblicatore,count(*) as invalid_codiceIdentificativoEstero FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile where identificativoFiscaleEstero='NID' AND entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_identificativoFiscaleEstero);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_codiceIdentificativoEstero");
				}
				accuracy_identificativoFiscaleEstero = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" identificativoFiscaleEstero: "+accuracy_identificativoFiscaleEstero);
				rs2.close();
				if(!Double.isNaN(accuracy_identificativoFiscaleEstero))
				{
					ideFiscEst = new Number(2,i,accuracy_identificativoFiscaleEstero);
					try {
						sheet3.addCell(ideFiscEst);
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
	//Compute the accuracy of 'ragioneSociale' in the table 'partecipanti'
	public void accuracy_ragioneSociale_value(){
		double csfr  = 0;
		double ct = 0;
		String ente = null;
		double accuracy_ragioneSociale;
		Statement stm;
		ResultSet rs1,rs2;
		Number ragioneSociale;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_ragioneSociale = "SELECT entePubblicatore,count(*) as invalid_ragioneSociale FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile where ragioneSociale  = 'NID' AND  entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_ragioneSociale);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_ragioneSociale");
				}
				accuracy_ragioneSociale = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" RagioneSociale: "+accuracy_ragioneSociale);
				rs2.next();
				if(!Double.isNaN(accuracy_ragioneSociale))
				{
					ragioneSociale = new Number(3,i,accuracy_ragioneSociale);
					try {
						sheet3.addCell(ragioneSociale);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.next();
			stm.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Compute the accuracy of 'ruolo' in the table 'partecipanti'
	public void accuracy_ruolo_value(){
		double csfr  = 0;
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
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_ruolo = "SELECT entePubblicatore, count(*) as invalid_ruolo FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile where ruolo='NID' AND entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_ruolo);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_ruolo");
				}
				accuracy_ruolo = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" ruolo: "+accuracy_ruolo);
				rs2.next();
				if(!Double.isNaN(accuracy_ruolo))
				{
					ruolo = new Number(4,i,accuracy_ruolo);
					try {
						sheet3.addCell(ruolo);
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

		String count_number_of_row = "SELECT count(distinct(idPartecipante)) as total_rows, entePubblicatore as ente FROM appalti.metadati as m LEFT JOIN appalti.lotti as l on  m.urlFile=l.metadati_urlFile LEFT JOIN appalti.lotti_partecipanti as l_p on l.idCig = l_p.lotto_idCig LEFT JOIN appalti.partecipanti as p ON l_p.partecipante_idPartecipante = p.idPartecipante GROUP BY entePubblicatore";
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

