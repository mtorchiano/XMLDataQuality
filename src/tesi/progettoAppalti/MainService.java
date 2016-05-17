package tesi.progettoAppalti;



import javax.swing.JFrame;
import javax.swing.JOptionPane;




public class MainService {

	public static void main(String[] args) {


		String message1 = "1)Inserimento dati\n";
		String message2 = "2)Analisi del dataset\n"; 
		String message3 = "3)Cancella e resetta contenuto\n";
		String message4 = "4)Esci dal programma\n";
		String message_choice = "Inserire la scelta: ";



		while(true){
			//JFRAME
			JFrame frame = new JFrame ("InputDiagol");
			String choice_str = JOptionPane.showInputDialog(frame,message1+message2+message3+message4+message_choice);
			if(choice_str.isEmpty()){
				choice_str = "0";
			}
			int choice = Integer.valueOf(choice_str);
			switch(choice){

			case 1:  System.out.println("----Parsa i dati----");
			ReadFileAppalti parser = new ReadFileAppalti();
			parser.readFile();
			break;
			case 2:  System.out.println("----Effettua analisi----");
			DataQuality dataQuality = new DataQuality();
			dataQuality.qualityAnalisys();
			break;
			case 3:  System.out.println("----Elimina i dati----");
			DeleteContent delete = new DeleteContent();
			delete.delete();
			break;
			case 4:  System.out.println("----Esci----");
			System.exit(0);
			default: System.out.println("Errore di digitazione");
			break;
			}

		}


	}
}

