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

public class CompletenessPartecipanti {

	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet4;
	public CompletenessPartecipanti(Connection conn,WritableWorkbook myexcel){
		this.conn = conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet4 = myexcel.createSheet("Completeness Partecipanti",3);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CODICE_FISCALE");
		Label l3 = new Label (2,0,"IDENTIFICATIVO_FISCALE_ESTERO");
		Label l4 = new Label (3,0,"RAGIONE_SOCIALE");
		Label l5 = new Label (4,0,"RUOLO");
		Label l6 = new Label (5,0,"ROW COMPLETENESS");
		try {
			sheet4.addCell(l1);
			sheet4.addCell(l2);
			sheet4.addCell(l3);
			sheet4.addCell(l4);
			sheet4.addCell(l5);	
			sheet4.addCell(l6);
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}

	//Compute the completeness of 'codiceFiscale' in table 'partecipanti'
	public void completeness_codiceFiscale_value(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_codiceFiscale;
		Statement stm;
		ResultSet rs1,rs2;
		Label lEnte;
		Number codFisc;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lEnte = new Label(0,i,ente);
				try {
					sheet4.addCell(lEnte);
				} catch (RowsExceededException e1) {
					e1.printStackTrace();
				} catch (WriteException e1) {
					e1.printStackTrace();
				}
				String query_count_null_codiceFiscale = "SELECT entePubblicatore,count(*) AS invalid_codiceFiscale FROM (`appalti`.`partecipanti` as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE codiceFiscale='null' AND codeset = '4' AND entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_codiceFiscale);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_codiceFiscale");
				}
				completeness_codiceFiscale = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" codiceFiscale "+completeness_codiceFiscale);
				rs2.close();
				if(!Double.isNaN(completeness_codiceFiscale))
				{
					codFisc = new Number(1,i,completeness_codiceFiscale);
					try {
						sheet4.addCell(codFisc);
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
	//Compute the completeness of 'identificativoFiscaleEstero' of 'partecipanti' table
	public void completeness_identificativoFiscaleEstero_value(){
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
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_identificativoFiscaleEstero = "SELECT entePubblicatore,count(*) as invalid_identificativoFiscaleEstero FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE identificativoFiscaleEstero='null' AND (codeset = '4' OR codeset='2') AND entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_identificativoFiscaleEstero);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_identificativoFiscaleEstero");
				}
				completeness_identificativoFiscaleEstero = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" identificativoFiscaleEstero: "+completeness_identificativoFiscaleEstero);
				rs2.close();
				if(!Double.isNaN(completeness_identificativoFiscaleEstero))
				{
					idFiscEst = new Number(2,i,completeness_identificativoFiscaleEstero);
					try {
						sheet4.addCell(idFiscEst);
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

	//Compute the completeness of 'ragioneSociale' of 'partecipanti' table
	public void completeness_ragioneSociale_value(){
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
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_ragioneSociale = "SELECT entePubblicatore,count(*) AS invalid_ragioneSociale FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE ragioneSociale='null' AND entepubblicatore='"+ente+"'";
				rs2 = stm.executeQuery(query_count_null_ragioneSociale);
				while(rs2.next()){
					rm = 0;
					rm = rs2.getDouble("invalid_ragioneSociale");
				}
				completeness_ragioneSociale = completeness_function(rm,rt);

				System.out.println("ENTE: "+ente+" ragioneSociale: "+completeness_ragioneSociale);
				rs2.close();
				if(!Double.isNaN(completeness_ragioneSociale))
				{
					ragSociale = new Number(3,i,completeness_ragioneSociale);
					try {
						sheet4.addCell(ragSociale);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Compute the completeness of 'ruolo'in the table 'partecipanti'
	public void completeness_ruolo_value(){
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
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_null_ruolo = "SELECT entePubblicatore,count(*) AS invalid_ruolo FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE ruolo = 'null' AND codesetRuolo = '1' AND entepubblicatore='"+ente+"'";
				rs2  = stm.executeQuery(query_count_null_ruolo);
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
						sheet4.addCell(ruolo);
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
	//Compute the completeness on the partecipanti table
	public void completeness_on_partecipante()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_on_partecipanti;
		Statement stm;
		ResultSet rs1,rs2;
		Number rowComp;


		try {
			rs1 = count_total_number_of_row();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{   rt = rs1.getDouble("total_rows");
			ente = rs1.getString("ente");
			String query_count_null_rows = "SELECT entePubblicatore,count(*) AS null_rows FROM (appalti.partecipanti as p LEFT JOIN appalti.lotti_partecipanti as l_p on p.idPartecipante = l_p.partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile  WHERE ((l_p.ruolo = 'null' AND l_p.codesetRuolo = '1') OR (codiceFiscale='null' AND codeset = '4') OR (identificativoFiscaleEstero='null' AND (codeset = '4' OR codeset='2')) OR (ragioneSociale='null'))AND entepubblicatore='"+ente+"'";
			rs2 = stm.executeQuery(query_count_null_rows);
			while(rs2.next())
			{
				rm = 0;
				rm = rs2.getDouble("null_rows");
			}
			completeness_on_partecipanti = completeness_function(rm,rt);
			System.out.println("ENTE: "+ente+" completezza su righe: "+completeness_on_partecipanti);
			rs2.close();
			if(!Double.isNaN(completeness_on_partecipanti))
			{
				rowComp = new Number(5,i,completeness_on_partecipanti);
				try {
					sheet4.addCell(rowComp);
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
	//Function to compute the completeness 
	private double completeness_function(double rm, double rt){
		double div;
		double completeness_value;
		div = rm/rt;
		completeness_value = 1-(div);

		return completeness_value;
	}
}
