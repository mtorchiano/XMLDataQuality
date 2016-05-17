package tesi.progettoAppalti;

import java.io.File;
import java.sql.DriverManager;
import java.sql.Connection;

import jxl.Workbook;
import jxl.write.WritableWorkbook;


public class DataQuality {

	//Constructor of the class
	public DataQuality(){

	}
	public void qualityAnalisys(){
		//DATABASE CONNECTION
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "appalti";
		String driver  = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "ADMIN";
		WritableWorkbook myexcel;
		File f = new File  ("dataSet/myfile.xls");
		try {
			myexcel = Workbook.createWorkbook(f);


			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			// ACCURACY LOTTO //
			System.out.println("-----ACCURACY ON LOTTO-----");
			AccuracyLotto acc = new AccuracyLotto(conn,myexcel);
			acc.writeSheet();
			acc.accuracy_cig_value();
			acc.accuracy_codiceFiscaleProp_value();
			acc.accuracy_sceltaContraente_value();
			acc.accuracy_oggetto_value();
			acc.accuracy_denominazione_value();
			acc.accuracy_dataInizio_value();
			acc.accuracy_dataUltimazione_value();
			acc.accuracy_importoAggiudicazione_value();
			acc.accuracy_importoSommeLiquidate_value();
			// COMPLETENESS LOTTO //
			System.out.println("-----COMPLETENESS ON LOTTO-----");
			CompletenessLotto com = new CompletenessLotto(conn,myexcel);
			com.writeSheet();
			com.completeness_cig_value();
			com.completeness_codiceFiscaleProp_value();
			com.completeness_sceltaContraente_value();
			com.competeness_oggetto_value();
			com.completeness_denominazione_value();
			com.completeness_importoAggiudicazione_value();
			com.completeness_importoSommeLiquidate_value();
			com.completeness_on_lotto();
			// ACCURACY ON PARTECIPANTI //
			System.out.println("-----ACCURACY ON PARTECIPANTI-----");
			AccuracyPartecipanti accp = new AccuracyPartecipanti(conn,myexcel);
			accp.writeSheet();
			accp.accuracy_codiceFiscale_value();
			accp.accuracy_identificativoFiscaleEstero_value();
			accp.accuracy_ragioneSociale_value();
			accp.accuracy_ruolo_value();
			//COMPLETENESS ON PARTECIPANTI//
			System.out.println("-----COMPLETENESS ON PARTECIPANTI-----");
			CompletenessPartecipanti comp = new CompletenessPartecipanti(conn,myexcel);
			comp.writeSheet();
			comp.completeness_ruolo_value();
			comp.completeness_codiceFiscale_value();
			comp.completeness_identificativoFiscaleEstero_value();
			comp.completeness_ragioneSociale_value();
			comp.completeness_ruolo_value();
			comp.completeness_on_partecipante();
			//ACCURACY ON AGGIUDICATARI 
			System.out.println("-----ACCURACY ON AGGIUDICATARI-----");
			AccuracyAggiudicatari acca = new AccuracyAggiudicatari(conn,myexcel);
			acca.writeSheet();
			acca.accuracy_ruolo_value();
			acca.accuracy_codiceFiscale_value();
			acca.accuracy_identificativoFiscaleEstero_value();
			acca.accuracy_ragioneSociale_value();
			//COMPLETENESS ON THE AGGIUDICATARI
			System.out.println("-----COMPLETENESS ON AGGIUDICATARI-----");
			CompletenessAggiudicatari coma =new CompletenessAggiudicatari(conn,myexcel);
			coma.writeSheet();
			coma.completeness_ruolo_value();
			coma.completeness_codiceFiscale_value();
			coma.completeness_identificativoFiscaleEstero_value();
			coma.completeness_ragioneSociale_value();
			coma.completeness_on_aggiudicatari();

			//DUPLICATION
			System.out.println("-----DUPLICATION ON PARTECIPANTI-----");
			Duplication dup = new Duplication(conn,myexcel);
			dup.writeSheet();
			dup.duplication_partecipanti();
			System.out.println("-----DUPLICATION ON AGGIUDICATARI-----");
			dup.duplication_aggiudicatari();
			System.out.println("----INTRARELATIONAL CONSISTENCY-----");
			IntrarelationalConstraints con = new IntrarelationalConstraints(conn,myexcel);
			con.writeSheet();
			System.out.println("----codiceFiscale e identificativoEstero partecipanti----");
			con.cfd_ife_partecipanti_constraint();
			System.out.println("----codiceFiscale e identificativoEstero aggiudicatari----");
			con.cdf_ide_aggiudicatari_constraint();
			System.out.println("----codiceFiscale equal to zero on partecipanti----");
			con.cdf_equal_zero_partecipanti();
			System.out.println("----codiceFiscale equal to zero on aggiudicatari----");
			con.cdf_equal_zero_aggiudicatari();
			System.out.println("---sommeLiquidate minore di importoAggiudicazione----");
			con.importoSommeLiquidate_lt_et_ImportoAggiudicazione();
			System.out.println("----dataInizio > dataUltimazione----");
			con.check_on_date();
			System.out.println("----INTERRELATION CONSISTENCY-----");
			InterrelationalConstraints inCon = new InterrelationalConstraints(conn,myexcel);
			inCon.writeSheet();
			inCon.aggiudicatario_partecipanti_constraint();
			inCon.aggiudicatario_is_partecipante_constraint();
			System.out.println("Aggiudicatario non c'è, importoSommeLiquidate diverso da zero");
			inCon.agg_np_importoSommeLiquidate_ez();
			System.out.println("Aggiudicatario presente e importoAggiudicatario uguale a zero");
			inCon.agg_p_importoAggiudicazione_nez();
			System.out.println("---FREQUENZE-----");
			SceltaContraenteFrequency scf = new SceltaContraenteFrequency(conn);
			scf.countSceltaContraente();
			//CLOSE THE CONNECTION WITH DATABASE
			conn.close();
			//Write the results on the excel 
			myexcel.write();
			//close the excell file
			myexcel.close();


		} catch (Exception  e) {
			e.printStackTrace();
		} 


	}
}
