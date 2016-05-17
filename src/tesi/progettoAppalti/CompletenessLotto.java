package tesi.progettoAppalti;

import java.sql.*;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import jxl.write.Number;
public class CompletenessLotto {

	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet2;
	public  CompletenessLotto(Connection conn,WritableWorkbook myexcel){
		this.conn = conn;
		this.myexcel = myexcel;
	}
	public void  writeSheet()
	{ 

		sheet2 = myexcel.createSheet("Completeness Lotto", 1);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CIG");
		Label l3 = new Label (2,0,"CODICE_FISCALE_PROP");
		Label l4 = new Label (3,0,"SCELTA_CONTRAENTE");
		Label l5 = new Label (4,0,"OGGETTO");
		Label l6 = new Label (5,0,"DENOMINAZIONE");
		Label l7 = new Label (6,0,"IMPORTO_AGGIUDICAZIONE");
		Label l8 = new Label (7,0,"IMPORTO_SOMME_LIQUIDATE");
		Label l9 = new Label (8,0,"ROW COMPLETENESS");

		try {

			sheet2.addCell(l1);
			sheet2.addCell(l2);
			sheet2.addCell(l3);
			sheet2.addCell(l4);
			sheet2.addCell(l5);
			sheet2.addCell(l6);
			sheet2.addCell(l7);
			sheet2.addCell(l8);
			sheet2.addCell(l9);

		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}
	//compute the completeness of cig 
	public void completeness_cig_value(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_cig;
		Statement stm1;
		ResultSet rs1,rs2;
		Label lEnte;
		Number cig ;
		try {
			//call the function to compute the total number of rows in the lotto table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				lEnte = new Label(0,i,ente);
				try {
					sheet2.addCell(lEnte);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				String query_count_null_cig = "SELECT entePubblicatore,count(*) as invalid_cig FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE cig = 'null' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_null_cig);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_cig");

				}
				//call the function to compute the completeness of cig 
				completeness_cig = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" cig: "+completeness_cig);
				rs2.close();
				if(!Double.isNaN(completeness_cig))
				{
					cig = new Number (1,i,completeness_cig);
					try {
						sheet2.addCell(cig);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	} 
	//compute the completeness of codiceFiscaleProp
	public void completeness_codiceFiscaleProp_value(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_codiceFiscaleProp;
		Statement stm1;
		ResultSet rs1,rs2;
		Number codiceFiscProp;

		try {
			//call the function to compute the total number of rows in the lotti table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_codiceFiscaleProp = "SELECT entePubblicatore,count(*) AS invalid_codiceFiscaleProp FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE codiceFiscaleProp = 'null' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_codiceFiscaleProp);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_codiceFiscaleProp");
				}
				//call the function to compute the completeness of codiceFiscale_prop 
				completeness_codiceFiscaleProp = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" codiceFiscaleProp: "+completeness_codiceFiscaleProp);
				rs2.close();
				if(!Double.isNaN(completeness_codiceFiscaleProp))
				{
					codiceFiscProp = new Number(2,i,completeness_codiceFiscaleProp);
					try {
						sheet2.addCell(codiceFiscProp);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Compute the completeness of 'sceltaContraente' in the table 'lotti'
	public void completeness_sceltaContraente_value(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_sceltaContraente;
		Statement stm1;
		ResultSet rs1,rs2;
		Number scelta;	   

		try {
			//call the function to compute the total number of rows in the lotti table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_sceltaContraente = "SELECT entePubblicatore,count(*) AS invalid_sceltaContraente FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE sceltaContraente = 'null' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_sceltaContraente);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_sceltaContraente");
				}
				//call the function to compute the completeness of sceltaContraente 
				completeness_sceltaContraente = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" SceltaContraente: "+completeness_sceltaContraente);
				rs2.close();
				if(!Double.isNaN(completeness_sceltaContraente))
				{
					scelta = new Number(3,i,completeness_sceltaContraente);
					try {
						sheet2.addCell(scelta);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Compute the completeness of 'oggetto' in the table 'lotti'
	public void competeness_oggetto_value(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_oggetto;
		Statement stm1;
		ResultSet rs1,rs2;
		Number oggetto;

		try {
			//call the function to compute the total number of rows in the lotti table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_oggetto = "SELECT entePubblicatore,count(*) as invalid_oggetto FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE oggetto = 'null' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_oggetto);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_oggetto");
				}
				//call the function to compute the completeness of oggetto 
				completeness_oggetto = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" Oggetto: "+completeness_oggetto);
				rs2.close();
				if(!Double.isNaN(completeness_oggetto))
				{
					oggetto = new Number(4,i,completeness_oggetto);
					try {
						sheet2.addCell(oggetto);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Compute the completeness of 'denominazione'
	public void completeness_denominazione_value(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_denominazione;
		Statement stm1;
		ResultSet rs1,rs2;
		Number denominazione;

		try {
			//call the function to compute the total number of rows in the lotti table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1; 
			while(rs1.next()){
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_oggetto = "SELECT entePubblicatore,count(*) as invalid_denominazione FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE denominazione = 'null' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_oggetto);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_denominazione");
				}
				//call the function to compute the completeness of denominazione 
				completeness_denominazione = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" Denominazione: "+completeness_denominazione);
				rs2.close();
				if(!Double.isNaN(completeness_denominazione))
				{
					denominazione = new Number (5,i,completeness_denominazione);
					try {
						sheet2.addCell(denominazione);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//Compute the completeness of "importoAggiudicazione" in the table 'lotti'
	public void completeness_importoAggiudicazione_value()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_importoAggiudicazione;
		Statement stm1;
		ResultSet rs1,rs2;
		Number importoAggiudicazione;

		try {
			//call the function to compute the total number of rows in the lotti table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_importoAggiudicazione = "SELECT entePubblicatore,count(*) as invalid_importoAggiudicazione FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE flagAgg = '2' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_importoAggiudicazione);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_importoAggiudicazione");
				}
				//call the function to compute the completeness of importoAggiudicazione 
				completeness_importoAggiudicazione = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" importoAggiudicazione: "+completeness_importoAggiudicazione);
				rs2.close();
				if(!Double.isNaN(completeness_importoAggiudicazione))
				{
					importoAggiudicazione = new Number(6,i,completeness_importoAggiudicazione);
					try {
						sheet2.addCell(importoAggiudicazione);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Compute the completeness of "importoSommeLiquidate" in the table lotti
	public void completeness_importoSommeLiquidate_value()
	{
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_importoSommeLiquidate;
		Statement stm1;
		ResultSet rs1,rs2;
		Number importoSommeLiq;

		try {
			//call the function to compute the total number of rows in the lotti table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_importoSommeLiquidate = "SELECT entePubblicatore,count(*) as importoSommeLiquidate FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE flagSommeLiq = '2' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_importoSommeLiquidate);
				while(rs2.next()){
					rm = 0;
					rm = rs2.getDouble("importoSommeLiquidate");
				}
				//call the function to compute the completeness of importoSommeLiquidate 
				completeness_importoSommeLiquidate = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" importoSommeLiquidate: "+completeness_importoSommeLiquidate);
				rs2.close();
				if(!Double.isNaN(completeness_importoSommeLiquidate))
				{
					importoSommeLiq = new Number(7,i,completeness_importoSommeLiquidate);
					try {
						sheet2.addCell(importoSommeLiq);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	//Compute the completeness on the row of the table
	//Count the rows that have at least one 'null' value
	public void completeness_on_lotto(){
		double rm = 0;
		double rt = 0;
		String ente = null;
		double completeness_on_lotto;
		Statement stm1;
		ResultSet rs1,rs2;
		Number rowComp;

		try {
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{   
				rt = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String count_null_value =  "SELECT entePubblicatore,count(*) as invalid_row FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE (cig='null' or codiceFiscaleProp='null' or denominazione ='null' or oggetto = 'null' or sceltaContraente = 'null' or flagAgg = '2' or flagSommeLiq = '2') AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(count_null_value);
				while(rs2.next())
				{
					rm = 0;
					rm = rs2.getDouble("invalid_row");
				}
				//call the function to compute the completeness of importoSommeLiquidate 
				completeness_on_lotto = completeness_function(rm, rt);
				System.out.println("ENTE: "+ente+" ROW COMPLETENESS: "+completeness_on_lotto);
				rs2.close();
				if(!Double.isNaN(completeness_on_lotto))
				{
					rowComp = new Number(8,i,completeness_on_lotto);
					try {
						sheet2.addCell(rowComp);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				i++;
			}
			rs1.close();
			stm1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	//function used to count the total number of row of 'lotti' table
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

	//function used to compute the completeness 
	private double completeness_function(double rm, double rt){
		double div;
		double completeness_value;
		div = rm/rt;
		completeness_value = 1-(div);

		return completeness_value;
	}
}
