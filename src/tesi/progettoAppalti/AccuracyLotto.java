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


public class AccuracyLotto {
	Connection conn;
	WritableWorkbook myexcel;
	WritableSheet sheet1;
	//constructor of the class
	public AccuracyLotto(Connection conn,WritableWorkbook myexcel){
		this.conn = conn;
		this.myexcel = myexcel; 
	}
	public void  writeSheet()
	{ 

		sheet1 = myexcel.createSheet("Accuracy Lotto", 0);
		Label l1 = new Label (0,0,"ENTE_PUBBLICATORE");
		Label l2 = new Label (1,0,"CIG");
		Label l3 = new Label (2,0,"CODICE_FISCALE_PROP");
		Label l4 = new Label (3,0,"SCELTA_CONTRAENTE");
		Label l5 = new Label (4,0,"OGGETTO");
		Label l6 = new Label (5,0,"DENOMINAZIONE");
		Label l7 = new Label (6,0,"DATA_INIZIO");
		Label l8 = new Label (7,0,"DATA_ULTIMAZIONE");
		Label l9 = new Label (8,0,"IMPORTO_AGGIUDICAZIONE");
		Label l10 = new Label (9,0,"IMPORTO_SOMME_LIQUIDATE");
		try {

			sheet1.addCell(l1);
			sheet1.addCell(l2);
			sheet1.addCell(l3);
			sheet1.addCell(l4);
			sheet1.addCell(l5);
			sheet1.addCell(l6);
			sheet1.addCell(l7);
			sheet1.addCell(l8);
			sheet1.addCell(l9);
			sheet1.addCell(l10);
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		}

	}
	//Compute the accuracy of 'cig' in the table 'lotti' of 'appalti' database
	public void  accuracy_cig_value(){
		double csfr  = 0;
		double ct = 0;
		double accuracy_cig;
		Number cig;
		String ente = null; 

		Statement stm1;
		ResultSet rs1;
		ResultSet rs2;



		try {
			//call the function to compute the total number of rows in the lotto table
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;

			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				Label lEnte = new Label(0,i,ente);
				try {
					sheet1.addCell(lEnte);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}

				String query_count_invalid_cig = "SELECT entePubblicatore AS ente,count(*) as invalid_cig FROM appalti.lotti AS l LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE CIG= 'NID' AND entePubblicatore = '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_invalid_cig);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_cig");

				}
				//compute the accuracy on lotto
				accuracy_cig = accuracy_function(csfr, ct);
				System.out.println("ENTE "+ente+" CIG "+accuracy_cig);
				rs2.close();
				if(!Double.isNaN(accuracy_cig))
				{
					cig = new Number(1,i,accuracy_cig);
					try {
						sheet1.addCell(cig);
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
	//compute the accuracy of 'codiceFiscaleProp'
	public void accuracy_codiceFiscaleProp_value(){
		double csfr  = 0;
		double ct = 0;
		double accuracy_codiceFiscaleProp;
		Statement stm1;
		ResultSet rs1,rs2;
		String ente;
		Number lCodFiscProp;
		try {
			//count the total number of rows on lotto
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_codiceFiscaleProp = "SELECT entePubblicatore,COUNT(*) AS invalid_codiceFiscaleProp FROM appalti.lotti as l LEFT JOIN appalti.metadati AS m ON l.metadati_urlFile = m.urlFile WHERE codiceFiscaleProp = 'NID' AND entePubblicatore = '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_invalid_codiceFiscaleProp);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_codiceFiscaleProp");    

				}
				accuracy_codiceFiscaleProp = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" codiceFiscaleProp: "+accuracy_codiceFiscaleProp);
				rs2.close();
				if(!Double.isNaN(accuracy_codiceFiscaleProp))
				{
					lCodFiscProp = new Number(2,i,accuracy_codiceFiscaleProp);
					try {
						sheet1.addCell(lCodFiscProp);
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
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Compute the accuracy of sceltaContraente
	public void accuracy_sceltaContraente_value(){
		double csfr = 0;
		double ct = 0;
		String ente = null;
		Number scelta;
		double accuracy_sceltaContraente;
		Statement stm1;
		ResultSet rs1,rs2;
		try {
			//count the total number of rows on lotto
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_sceltaContraente = "SELECT entePubblicatore,count(*) as invalid_sceltaContraente FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE sceltaContraente = 'NID' AND entePubblicatore= '"+ente+"'";

				rs2 = stm1.executeQuery(query_count_invalid_sceltaContraente);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_sceltaContraente");	 



				}
				accuracy_sceltaContraente = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" SceltaContraente: "+accuracy_sceltaContraente);
				rs2.close();
				if(!Double.isNaN(accuracy_sceltaContraente))
				{
					scelta = new Number (3,i, accuracy_sceltaContraente);
					try {
						sheet1.addCell(scelta);
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

	//Compute the accuracy of oggetto
	public void accuracy_oggetto_value(){
		double csfr = 0;
		double ct = 0;
		double accuracy_oggetto;
		String ente = null;
		Statement stm1;
		ResultSet rs1,rs2;
		Number oggetto; 
		try {
			//count the total number of rows on lotto
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows"); 
				ente = rs1.getString("ente");
				String query_count_invalid_oggetto = "SELECT entePubblicatore, count(*) as invalid_oggetto FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE oggetto = 'NID' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_invalid_oggetto);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_oggetto");

				}
				//call the function to compute the accuracy on the oggetto
				accuracy_oggetto = accuracy_function(csfr, ct);
				System.out.println("Ente: "+ente+" oggetto: "+accuracy_oggetto);
				rs2.close();
				if(!Double.isNaN(accuracy_oggetto))
				{
					oggetto = new Number(4,i,accuracy_oggetto);

					try {
						sheet1.addCell(oggetto);
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

	//Compute the accuracy of denominazione
	public void accuracy_denominazione_value(){
		double csfr = 0;
		double ct = 0;
		double accuracy_denominazione;
		String ente;
		Statement stm1;
		ResultSet rs1,rs2;
		Number denominazione;
		try {
			//count the total number of rows on lotto
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next()){
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_denominazione = "SELECT entePubblicatore,count(*) as invalid_denominazione FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE denominazione= 'NID' AND entePubblicatore = '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_invalid_denominazione);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_denominazione");

				}
				//call the function to compute the accuracy on denominazione
				accuracy_denominazione = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" Denominazione: "+accuracy_denominazione);
				rs2.close();
				if(!Double.isNaN(accuracy_denominazione))
				{
					denominazione = new Number(5,i,accuracy_denominazione);
					try {
						sheet1.addCell(denominazione);
					}catch (RowsExceededException e) {
						e.printStackTrace();
					}catch (WriteException e) {
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
	//Compute the accuracy of dataInizio
	public void accuracy_dataInizio_value(){

		double csfr = 0;
		double ct = 0;
		String ente = null;
		double accuracy_dataInizio;
		Statement stm1;
		ResultSet rs1,rs2;
		Number dataInizio;
		try {
			//count the total number of rows on lotto
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_dataInizio = "SELECT entePubblicatore,count(*) as invalid_dataInizio FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE dataInizio = '0001-01-01' AND entePubblicatore= '"+ente+"'";

				rs2 = stm1.executeQuery(query_count_invalid_dataInizio);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_dataInizio");
				}
				accuracy_dataInizio = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" dataInizio: "+accuracy_dataInizio);
				rs2.close();
				if(!Double.isNaN(accuracy_dataInizio))
				{
					dataInizio = new Number(6,i,accuracy_dataInizio);

					try {
						sheet1.addCell(dataInizio);
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
	//Compute the accuracy of dataUltimazione
	public void accuracy_dataUltimazione_value()
	{
		double csfr = 0;
		double ct = 0;
		String ente = null;
		double accuracy_dataUltimazione;
		Statement stm1;
		ResultSet rs1,rs2;
		Number dataUltimazione;

		try {
			//count the total number of rows on lotto
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_dataUtimazione = "SELECT entePubblicatore,count(*) as invalid_dataUltimazione FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE dataUltimazione ='3999-12-31' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_invalid_dataUtimazione);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_dataUltimazione");
				}
				accuracy_dataUltimazione = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" DataUltimazione: "+accuracy_dataUltimazione);
				rs2.close();
				if(!Double.isNaN(accuracy_dataUltimazione))
				{
					dataUltimazione = new Number(7,i,accuracy_dataUltimazione);

					try {
						sheet1.addCell(dataUltimazione);
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

	//Compute the accuracy of importoAggiudicazione
	public void accuracy_importoAggiudicazione_value(){

		double csfr = 0;
		double ct = 0;
		String ente = null;
		double accuracy_importoAggiudicazione;
		Statement stm1;
		ResultSet rs1,rs2;
		Number importoAggiudicazione;
		try {
			rs1 =count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				//count the total number of rows on lotto
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_importoAggiudicazione  = "SELECT entePubblicatore,count(*) as invalid_importoAggiudicazione FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE flagAgg = '1' AND entePubblicatore= '"+ente+"'";

				rs2 = stm1.executeQuery(query_count_invalid_importoAggiudicazione);
				while(rs2.next())
				{ 
					csfr = 0;
					csfr = rs2.getDouble("invalid_importoAggiudicazione");
				}
				accuracy_importoAggiudicazione = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+"importoAggiudicazione: "+accuracy_importoAggiudicazione);
				rs2.close();
				if(!Double.isNaN(accuracy_importoAggiudicazione))
				{
					importoAggiudicazione = new Number(8,i,accuracy_importoAggiudicazione);
					try {
						sheet1.addCell(importoAggiudicazione);
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
	//Compute the accuracy of importoSommeLiquidate
	public void accuracy_importoSommeLiquidate_value()
	{
		double csfr = 0;	
		double ct = 0;
		String ente = null;
		double accuracy_importoSommeLiquidate;
		Statement stm1;
		ResultSet rs1,rs2;
		Number importoSommeLiquidate;

		try {
			//count the total number of rows on lotto
			rs1 = count_total_number_of_row();
			stm1 = conn.createStatement();
			int i = 1;
			while(rs1.next())
			{
				ct = rs1.getDouble("total_rows");
				ente = rs1.getString("ente");
				String query_count_invalid_importoSommeLiquidate = "SELECT entePubblicatore,count(*) as invalid_SommeLiquidate FROM appalti.lotti as l LEFT JOIN appalti.metadati as m ON l.metadati_urlFile = m.urlFile WHERE flagSommeLiq = '1' AND entePubblicatore= '"+ente+"'";
				rs2 = stm1.executeQuery(query_count_invalid_importoSommeLiquidate);
				while(rs2.next())
				{
					csfr = 0;
					csfr = rs2.getDouble("invalid_SommeLiquidate");
				}
				accuracy_importoSommeLiquidate = accuracy_function(csfr, ct);
				System.out.println("ENTE: "+ente+" DataUltimazione: "+accuracy_importoSommeLiquidate);
				rs2.close();
				if(!Double.isNaN(accuracy_importoSommeLiquidate))
				{
					importoSommeLiquidate = new Number(9,i,accuracy_importoSommeLiquidate);

					try {
						sheet1.addCell(importoSommeLiquidate);
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
	//Function used to compute the accuracy
	private double accuracy_function(double csfr,double ct){
		double div;
		double accuracy_value;

		div = csfr/ct;
		accuracy_value = 1-div;

		return accuracy_value;
	}

}
