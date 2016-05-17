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

public class IntrarelationalConstraints {

	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet9;
	public IntrarelationalConstraints(Connection conn,WritableWorkbook myexcel)
	{
		this.conn =conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet9 = myexcel.createSheet("Intrarelational Constraints",8);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CDF_IFE_PART");
		Label l3 = new Label (2,0,"CDF_IFE_AGG");
		Label l4 = new Label (3,0,"CDF_EQ_ZERO_PAR");
		Label l5 = new Label (4,0,"CDF_EQ_ZERO_AGG");
		Label l6 = new Label (5,0,"DATE");
		Label l7 = new Label (6,0,"IMPORT");
		try {
			sheet9.addCell(l1);
			sheet9.addCell(l2);
			sheet9.addCell(l3);
			sheet9.addCell(l4);
			sheet9.addCell(l5);	
			sheet9.addCell(l6);
			sheet9.addCell(l7);
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}
	//evalutation of the following integrity constraint:
	//For a participant must appear one and only one among <codiceFiscale><identificativoFiscaleEstero>	
	public void cfd_ife_partecipanti_constraint()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null; 
		double cm = 0;
		double ct = 0;
		double consistency_of_codeset_partecipanti;
		Label lEnte;
		Number cfd_ife_part;  

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row_partecipanti();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lEnte = new Label(0,i,ente);
				try {
					sheet9.addCell(lEnte);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				String query_count_invalid_participant = "SELECT entepubblicatore, COUNT(*) AS invalid_participant FROM (appalti.partecipanti AS p LEFT JOIN appalti.lotti_partecipanti AS l_p ON p.idPartecipante = partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE codeset  = '3' and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_participant);
				while(rs2.next())
				{
					cm = 0;
					cm = rs2.getDouble("invalid_participant");
				}
				consistency_of_codeset_partecipanti = consistency_function(cm, ct);	
				System.out.println("ENTE: "+ente+"codiceFiscale/identificativoFiscaleEstero on partecipanti consistency: "+consistency_of_codeset_partecipanti);
				rs2.close();
				if(!Double.isNaN(consistency_of_codeset_partecipanti))
				{
					cfd_ife_part = new Number(1,i,consistency_of_codeset_partecipanti);

					try {
						sheet9.addCell(cfd_ife_part);
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
	//evalutation of the following integrity constraint:
	//For an aggiudicatario  must appear one and only one among <codiceFiscale> <identificativoFiscaleEstero>
	public void cdf_ide_aggiudicatari_constraint()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null;
		double cm = 0;
		double ct = 0;
		double consistency_of_codeset_aggiudicatari;
		Number cfd_ife_agg;  

		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_aggiudicatari();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_aggiudicatari = "SELECT entepubblicatore, COUNT(*) as invalid_aggiudicatari FROM(appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari as l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig  LEFT JOIN appalti.metadati AS m on l.metadati_urlFile = m.urlFile WHERE a.codeset = '3' AND entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_count_invalid_aggiudicatari);
				while(rs2.next())
				{
					cm = 0;
					cm = rs2.getDouble("invalid_aggiudicatari");
				}
				consistency_of_codeset_aggiudicatari = consistency_function(cm, ct);
				System.out.println("ENTE: "+ente+"codiceFiscale/identificativoFiscaleEstero on aggiudicatari consistency: "+consistency_of_codeset_aggiudicatari);
				rs2.close();
				if(!Double.isNaN(consistency_of_codeset_aggiudicatari))
				{
					cfd_ife_agg = new Number(2,i,consistency_of_codeset_aggiudicatari);
					try {
						sheet9.addCell(cfd_ife_agg);
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
	//Evaluation of the following integrity constraint
	//CodiceFiscale must be not equal to 00000000000 or 0000000000000000 in partecipanti table 
	public void cdf_equal_zero_partecipanti()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null;
		double cm = 0;
		double ct = 0;
		double consistency_of_cdf_partecipante;
		Number cdf_part;

		try {
			//call the function to compute the total number of rows
			rs1 = count_total_number_of_row_partecipanti();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_invalid_codiceFiscale_partecipanti = "SELECT entePubblicatore, COUNT(*) AS invalid_codiceFiscale FROM (appalti.partecipanti AS p LEFT JOIN appalti.lotti_partecipanti AS l_p ON p.idPartecipante = partecipante_idPartecipante) LEFT JOIN appalti.lotti AS l ON l_p.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE (codiceFiscale = '00000000000' OR codiceFiscale= '0000000000000000') AND entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_invalid_codiceFiscale_partecipanti);
				while(rs2.next())
				{
					cm = 0;
					cm = rs2.getDouble("invalid_codiceFiscale");
				}

				consistency_of_cdf_partecipante = consistency_function(cm, ct);
				System.out.println("ENTE: "+ente+" codiceFiscale pertecipanti equal to zero: "+consistency_of_cdf_partecipante);
				if(!Double.isNaN(consistency_of_cdf_partecipante))
				{
					cdf_part = new Number(3,i,consistency_of_cdf_partecipante);
					try {
						sheet9.addCell(cdf_part);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Evaluation of the following integrity constraint
	//CodiceFiscale must be not equal to 00000000000 or 0000000000000000 in aggiudicatari table 
	public void cdf_equal_zero_aggiudicatari()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null;
		double cm = 0;
		double ct = 0;
		double consistency_of_cdf_aggiudicatario;
		Number cdf_agg;

		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_aggiudicatari();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_invalid_codiceFiscale_aggiudicatari = "SELECT entepubblicatore, COUNT(*) AS invalid_codiceFiscale FROM(appalti.aggiudicatari AS a LEFT JOIN appalti.lotti_aggiudicatari as l_a ON a.idAggiudicatario = l_a.aggiudicatari_idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig  LEFT JOIN appalti.metadati AS m on l.metadati_urlFile = m.urlFile WHERE (codiceFiscale = '00000000000' OR codiceFiscale = '0000000000000000') AND entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_invalid_codiceFiscale_aggiudicatari);
				while(rs2.next())
				{
					cm = 0;
					cm = rs2.getDouble("invalid_codiceFiscale");
				}
				consistency_of_cdf_aggiudicatario = consistency_function(cm, ct);
				System.out.println("ENTE: "+ente+" codiceFiscale aggiudicatari equal to zero: "+consistency_of_cdf_aggiudicatario);
				rs2.close();
				if(!Double.isNaN(consistency_of_cdf_aggiudicatario))
				{
					cdf_agg = new Number(4,i,consistency_of_cdf_aggiudicatario);
					try {
						sheet9.addCell(cdf_agg);
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
	//evaluates the integrity constraint
	//dataInizio MUST be before dataUltimazione
	public void check_on_date()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente =null;
		double cm = 0;
		double ct = 0;
		double invalid_data = 0;
		Number dateCheck;


		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_lotto();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_invalid_data = "SELECT entePubblicatore as ente, count(*) AS invalid_data FROM appalti.lotti AS l LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE dataInizio > dataUltimazione and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_invalid_data);
				while(rs2.next())
				{
					cm = 0;
					cm = rs2.getDouble("invalid_data");
				}
				invalid_data = consistency_function(cm, ct);
				System.out.println("ENTE: "+ente+" invalid data: "+invalid_data);
				rs2.close();
				if(!Double.isNaN(invalid_data))
				{
					dateCheck = new Number(5,i,invalid_data);
					try {
						sheet9.addCell(dateCheck);
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
	//evaluates the following integrity constraint
	//The importoSommeLiquidate MUST be less than or equal to the ImportoAggiudicazione
	public void importoSommeLiquidate_lt_et_ImportoAggiudicazione()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null;
		double cm = 0 ;
		double ct;
		double ISL_Lt_Et_IA;
		Number importo_check;

		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_lotto();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_invalid_import = "SELECT entePubblicatore as ente, count(*) as invalid_import FROM appalti.lotti AS l LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile  WHERE importoSommeLiquidate > importoAggiudicazione and entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query_invalid_import);
				while(rs2.next())
				{
					cm = 0;
					cm = rs2.getDouble("invalid_import");
				}
				ISL_Lt_Et_IA = consistency_function(cm, ct);
				System.out.println("ENTE: "+ente+" importoSommeLiquidate greater than importoAggiudicazione: "+ISL_Lt_Et_IA);
				rs2.close();
				if(!Double.isNaN(ISL_Lt_Et_IA))
				{
					importo_check = new Number(6,i,ISL_Lt_Et_IA);
					try {
						sheet9.addCell(importo_check);
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
	//function used to count the total number of row of 'partecipanti' table
	private ResultSet count_total_number_of_row_partecipanti(){
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
	//function used to count the total number of row of 'aggiudicatari' table
	private ResultSet count_total_number_of_row_aggiudicatari(){
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
	//function used to count the total number of row of the 'lotti' table
	private ResultSet count_total_number_of_row_lotto(){

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
	//function used to compute the number of row wich does not violate the constraints
	private double consistency_function(double cm, double ct){
		double div;
		double completeness_value;
		div = cm/ct;
		completeness_value = 1-(div);

		return completeness_value;
	}

}
