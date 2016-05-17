package tesi.progettoAppalti;

import generated.legge190_1_0.Pubblicazione;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;

public class ReadFileAppalti {

	public static List<String> fileList;

	public  ReadFileAppalti(){

	}

	public void readFile(){
		//Insert the path of the directory where to read the files
		File pathfile =new File("dataSet/Test");
		searchFile(pathfile);


		String context = "generated.legge190_1_0";
		Pubblicazione.Data data;
		List<Pubblicazione.Data.Lotto> lista_lotti;
		String urlFile_metadati = null;
		/*Flag used to check if the url of the files is already stored in the database */
		boolean in_db;


		//Database connection
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "appalti";
		String driver  = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "ADMIN";

		try{
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);

			for(int i = 0; i < fileList.size(); i++)
			{
				System.out.println("------FILE: "+fileList.get(i));
				//UNMARSHALLING
				JAXBUnmarshalling unmarshall = new JAXBUnmarshalling(fileList.get(i),context);
				/*Get the root of the document*/
				Pubblicazione pubblicazione = (Pubblicazione) unmarshall.getUnmarshalObject();
				//RETRIEVE METADATA
				Metadati metadati = new Metadati();
				metadati.getdata(pubblicazione);
				//Get the url of the dataset to check if it is already in the database
				urlFile_metadati = metadati.getKey();
				//CHECK IF THE DATA ARE ALREADY IN THE DATABASE
				in_db = metadati.select_url_in_database(conn, urlFile_metadati);
				//if the url of the xml file is not in the database, insert all the correspondent data
				//else return error message;
				if(!in_db){
					/* if the url is not already in the metadata table 
					 *    then insert the metadata in the metadata table
					 */
					metadati.insert_metadata_in_database(conn);
					/*Get the data section*/
					data = pubblicazione.getData();
					//Get the list of lots within the data section
					lista_lotti = data.getLotto();
					ManagementLotti manLotti = new ManagementLotti(lista_lotti,urlFile_metadati);
					//call the parseSingleLotti method
					manLotti.parseSigleLotti(conn);
					int zerovalue = manLotti.returnc();
					System.out.println("VALUE: "+zerovalue);
				}
				else{
					System.out.println("Error: is not possible insert twice the same dataset");	

				}
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public static void searchFile(File pathfile)
	{
		File listFile[] = pathfile.listFiles();
		fileList = new LinkedList<String>();

		if(listFile != null)
		{    
			for(int i = 0; i < listFile.length; i++)
			{
				if(listFile[i].isDirectory())
				{
					searchFile(pathfile);
				}
				else
				{
					fileList.add(listFile[i].getPath());
				}
			}
		}

	}
}
