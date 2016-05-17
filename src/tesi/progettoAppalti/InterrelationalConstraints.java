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

public class InterrelationalConstraints {
	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet10;

	InterrelationalConstraints(Connection conn,	WritableWorkbook myexcel)
	{
		this.conn = conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet10 = myexcel.createSheet("Interrelational Constraint",9);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"PART_CONST");
		Label l3 = new Label (2,0,"AGG_CONST");
		Label l4 = new Label (3,0,"SOMMELIQ_CONST");
		Label l5 = new Label (4,0,"IMPORTOAGG_CONST"); 

		try {
			sheet10.addCell(l1);
			sheet10.addCell(l2);
			sheet10.addCell(l3);
			sheet10.addCell(l4);
			sheet10.addCell(l5);

		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}
	//evaluation of the following constraint
	//if there is an aggiudicatario there MUST be at least one participant
	public void aggiudicatario_partecipanti_constraint()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null;
		double rm = 0;
		double rt;
		double invalid_rows;
		Label lente;
		Number part_const;

		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_lotto();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{

				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lente = new Label(0,i,ente);
				try {
					sheet10.addCell(lente);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				String query = "SELECT entePubblicatore,count(DISTINCT(l_a.lotto_idCig)) AS invalid_aggiudicatari FROM(appalti.lotti_aggiudicatari AS l_a LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig) LEFT JOIN appalti.lotti_partecipanti AS l_p ON l.idCig = l_p.lotto_idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE l_p.partecipante_idPartecipante is null AND entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_aggiudicatari");

				}
				invalid_rows = consistency_function(rm, rt);
				System.out.println("ENTE: "+ente+" Aggiudicatari senza partecipanti: "+invalid_rows);
				rs2.close();
				part_const = new Number(1,i,invalid_rows);
				try {
					sheet10.addCell(part_const);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	//evaluation of the following constraint 
	//the aggiudicatario of a lot MUST be a partecipante for that lotto
	public void aggiudicatario_is_partecipante_constraint()
	{
		Statement stm;
		ResultSet rs1,rs2;
		String ente = null;
		double rm = 0;
		double rt;
		double invalid_rows;
		Number agg_const;
		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_lotto();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query = "SELECT count(DISTINCT(l_a.lotto_idCig)) as invalid_aggiudicatario FROM (appalti.lotti_aggiudicatari AS l_a  LEFT JOIN appalti.aggiudicatari AS a ON l_a.aggiudicatari_idAggiudicatario = a.idAggiudicatario) LEFT JOIN appalti.lotti AS l ON l_a.lotto_idCig = l.idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile LEFT JOIN  (appalti.lotti_partecipanti AS l_p LEFT JOIN appalti.partecipanti as p ON l_p.partecipante_idPartecipante = p.idPartecipante) ON  l.idCig = l_p.lotto_idCig AND a.codiceFiscale = p.codiceFiscale AND a.identificativoFiscaleEstero = p.identificativoFiscaleEstero WHERE p.codiceFiscale is null AND entePubblicatore= '"+ente+"'";
				rs2 = stm.executeQuery(query);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_aggiudicatario");
				}
				invalid_rows = consistency_function(rm, rt);
				System.out.println("ENTE: "+ente+"aggiudicatario non è partecipante: "+invalid_rows);
				rs2.close();
				agg_const = new Number(2,i,invalid_rows);
				try {
					sheet10.addCell(agg_const);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//evaluaton of the following constraint 
	//when the aggiudicatario is not present the importoSommeLiquidate MUST be zero
	public void agg_np_importoSommeLiquidate_ez()
	{
		Statement stm;
		ResultSet rs1,rs2;
		double rm = 0;
		double rt = 0;
		double invalid_rows;
		String ente = null;

		Number sommeLiq_constraint;


		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_lotto();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ente = rs1.getString("ente");
				rt = rs1.getDouble("total_rows");
				String query = "SELECT COUNT(*) as invalid_value FROM appalti.lotti as  l LEFT JOIN appalti.lotti_aggiudicatari as l_a ON l.idCig = l_a.lotto_idCig LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE l_a.aggiudicatari_idAggiudicatario is null AND l.importoSommeLiquidate <> '0' AND entePubblicatore = '"+ente+"'";
				rs2 = stm.executeQuery(query);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_value");
				}
				invalid_rows = consistency_function(rm, rt);
				System.out.println("Aggiudicatario not presente importoSommeLiquidate diverso da zero: "+invalid_rows);
				sommeLiq_constraint = new Number(3,i,invalid_rows);
				try {
					sheet10.addCell(sommeLiq_constraint);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				i++;
			}
			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//evaluation of the following integrity constraint
	//when the aggiudicatario is present, the importoAggiudicazione MUST be different by zero
	public void agg_p_importoAggiudicazione_nez()
	{
		Statement stm;
		ResultSet rs1,rs2;
		double rm = 0;
		double rt = 0;
		String ente = null;
		double invalid_rows = 0;
		Number importoAgg_cons;
		try {
			//call the function to compute the total number of rows on aggiudicatari
			rs1 = count_total_number_of_row_lotto();
			stm = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ente = rs1.getString("ente");
				rt = rs1.getDouble("total_rows");
				String query = "SELECT  COUNT(*) AS invalid_value  FROM appalti.lotti as l LEFT JOIN appalti.lotti_aggiudicatari as l_a ON l.idCig = l_a.lotto_idCig  LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile  WHERE l_a.aggiudicatari_idAggiudicatario is not null AND l.importoAggiudicazione = '0' AND entePubblicatore ='"+ente+"'";
				rs2 = stm.executeQuery(query);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_value");
				}
				invalid_rows = consistency_function(rm, rt);
				System.out.println("AAggiudicatario presente importo aggudicazione uguale a zero: "+invalid_rows);
				importoAgg_cons = new Number(4,i,invalid_rows);
				try {
					sheet10.addCell(importoAgg_cons);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				i++;
			}

			rs1.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
	//function used to compute the number of row which does not violate the constraints
	private double consistency_function(double rm, double rt){
		double div;
		double completeness_value;
		div = rm/rt;
		completeness_value = 1-(div);

		return completeness_value;
	}
}
