package bankexcercise;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class BankApplication extends JFrame {

	ArrayList<BankAccount> accountList = new ArrayList<BankAccount>();
	static HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	private final static int TABLE_SIZE = 29;
	static private final String newline = "\n";

	JMenuBar menuBar;
	JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;

	JMenuItem deposit, withdraw, calcInterest;
	JMenuItem closeApp;
	JButton firstItemButton, lastItemButton, nextItemButton, prevItemButton;


	String gui [] = {"Account ID", "Account Number", "First Name", "Last Name" , "Account Type", "Balance", "Overdraft"};
	Map<String, JLabel> labels = new HashMap<String, JLabel>();
	Map<String, JTextField> fields = new HashMap<String, JTextField>();
	Map<String, JMenuItem> recMenuItems = new HashMap<String, JMenuItem>();
	Map<String, JMenuItem> navMenuItems = new HashMap<String, JMenuItem>();
	Map<String, JMenuItem> fileMenuItems = new HashMap<String, JMenuItem>();
 	
	static JFileChooser fc;
	JTable jTable;
	double interestRate;

	int currentItem = 0;


	boolean openValues;

	public BankApplication() {

		super("Bank Application");

		int currentItem;
		initComponents();
	}

	public void initComponents() {
		setLayout(new BorderLayout());
		JPanel displayPanel = new JPanel(new MigLayout());

		for (String str: gui) {
			labels.put(str, new JLabel(str + ": "));
			fields.put(str, new JTextField(15));
			fields.get(str).setEditable(false);

			displayPanel.add(labels.get(str), "growx, pushx");
			displayPanel.add(fields.get(str), "growx, pushx, wrap");
		}

		add(displayPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

		nextItemButton = new JButton(new ImageIcon("next.png"));
		prevItemButton = new JButton(new ImageIcon("prev.png"));
		firstItemButton = new JButton(new ImageIcon("first.png"));
		lastItemButton = new JButton(new ImageIcon("last.png"));

		buttonPanel.add(firstItemButton);
		buttonPanel.add(prevItemButton);
		buttonPanel.add(nextItemButton);
		buttonPanel.add(lastItemButton);

		add(buttonPanel, BorderLayout.SOUTH);

		menuBar =  new JMenuBar();
		setJMenuBar(menuBar);

		navigateMenu = new JMenu("Navigate");

		ArrayList<String> navMenuLabels = new ArrayList<String>(
				Arrays.asList("Next Item", "Previous Item", "First Item",
						"Last Item", "Find By Account Number", "Find By Surname", "List All Records"));
		setMenuItems(navMenuItems, navigateMenu, navMenuLabels);


		menuBar.add(navigateMenu);

		recordsMenu = new JMenu("Records");

		ArrayList<String> recMenuLabels = new ArrayList<String>(
				Arrays.asList("Create Item", "Modify Item", "Delete Item", "Set Overdraft", "Set Interest"));
		setMenuItems(recMenuItems, recordsMenu, recMenuLabels);


		menuBar.add(recordsMenu);

		transactionsMenu = new JMenu("Transactions");

		deposit = new JMenuItem("Deposit");
		withdraw = new JMenuItem("Withdraw");
		calcInterest = new JMenuItem("Calculate Interest");

		transactionsMenu.add(deposit);
		transactionsMenu.add(withdraw);
		transactionsMenu.add(calcInterest);

		menuBar.add(transactionsMenu);

		fileMenu = new JMenu("File");

		ArrayList<String> fileMenuLabels = new ArrayList<String>(Arrays.asList("Open File", "Save File", "Save As"));
		    	setMenuItems(fileMenuItems, fileMenu, fileMenuLabels);
		     	
		menuBar.add(fileMenu);

		exitMenu = new JMenu("Exit");

		closeApp = new JMenuItem("Close Application");

		exitMenu.add(closeApp);

		menuBar.add(exitMenu);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		recMenuItems.get("Set Overdraft").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(table.get(currentItem).getAccountType().trim().equals("Current")){
					String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
					fields.get("Overdraft").setText(newOverdraftStr);
					table.get(currentItem).setOverdraft(Double.parseDouble(newOverdraftStr));
				}
				else
					JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");

			}
		});

		ActionListener first = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				saveOpenValues();

				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);
			}
		};

		ActionListener next = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				// No next if at end of list.
				if (currentItem != (table.size()-1)) {
					// Move to next item.
					currentItem++;
					while(!table.containsKey(currentItem) ){
						currentItem++;
					}
					displayDetails(currentItem);			
				}				
			}
		};

		ActionListener next1 = new ActionListener(){
			public void actionPerformed(ActionEvent e){

				ArrayList<Integer> keyList = new ArrayList<Integer>();
				int i=0;

				while(i<TABLE_SIZE){
					i++;
					if(table.containsKey(i))
						keyList.add(i);
				}

				int maxKey = Collections.max(keyList);

				saveOpenValues();	

				if(currentItem<maxKey){
					currentItem++;
					while(!table.containsKey(currentItem)){
						currentItem++;
					}
				}
				displayDetails(currentItem);			
			}
		};



		ActionListener prev = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ArrayList<Integer> keyList = new ArrayList<Integer>();
				int i=0;

				while(i<TABLE_SIZE){
					i++;
					if(table.containsKey(i))
						keyList.add(i);
				}

				int minKey = Collections.min(keyList);
				//System.out.println(minKey);

				if(currentItem>minKey){
					currentItem--;
					while(!table.containsKey(currentItem)){
						//System.out.println("Current: " + currentItem + ", min key: " + minKey);
						currentItem--;
					}
				}
				displayDetails(currentItem);				
			}
		};

		ActionListener last = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();

				currentItem =29;

				while(!table.containsKey(currentItem)){
					currentItem--;

				}

				displayDetails(currentItem);
			}
		};

		nextItemButton.addActionListener(next);
		navMenuItems.get("Next Item").addActionListener(next);

		prevItemButton.addActionListener(prev);
		navMenuItems.get("Previous Item").addActionListener(prev);

		firstItemButton.addActionListener(first);
		navMenuItems.get("First Item").addActionListener(first);


		lastItemButton.addActionListener(last);
		navMenuItems.get("Last Item").addActionListener(last);

		recMenuItems.get("Delete Item").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				table.remove(currentItem);
				JOptionPane.showMessageDialog(null, "Account Deleted");


				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);

			}
		});

		recMenuItems.get("Create Item").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new CreateBankDialog(table);		
			}
		});


		recMenuItems.get("Modify Item").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fields.get("Last Name").setEditable(true);
				fields.get("First Name").setEditable(true);
				openValues = true;
			}
		});

		recMenuItems.get("Set Interest").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				String interestRateStr = JOptionPane.showInputDialog("Enter Interest Rate: (do not type the % sign)");
				if(interestRateStr!=null)
					interestRate = Double.parseDouble(interestRateStr);

			}
		});

		navMenuItems.get("List All Records").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){

				JFrame frame = new JFrame("TableDemo");
				JPanel pan = new JPanel();

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				String col[] = {"ID","Number","Name", "Account Type", "Balance", "Overdraft"};

				DefaultTableModel tableModel = new DefaultTableModel(col, 0);
				jTable = new JTable(tableModel);
				JScrollPane scrollPane = new JScrollPane(jTable);
				jTable.setAutoCreateRowSorter(true);

				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {


					Object[] objs = {entry.getValue().getAccountID(), entry.getValue().getAccountNumber(), 
							entry.getValue().getFirstName().trim() + " " + entry.getValue().getSurname().trim(), 
							entry.getValue().getAccountType(), entry.getValue().getBalance(), 
							entry.getValue().getOverdraft()};

					tableModel.addRow(objs);
				}
				frame.setSize(600,500);
				frame.add(scrollPane);
				//frame.pack();
				frame.setVisible(true);			
			}
		});

		fileMenuItems.get("Open File").addActionListener(new ActionListener(){
 			public void actionPerformed(ActionEvent e){
				readFile();
				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);
			}
		});

		fileMenuItems.get("Save File").addActionListener(new ActionListener(){
 			public void actionPerformed(ActionEvent e){
				writeFile();
			}
		});

		fileMenuItems.get("Save As").addActionListener(new ActionListener(){
 			public void actionPerformed(ActionEvent e){
				saveFileAs();
			}
		});

		closeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int answer = JOptionPane.showConfirmDialog(BankApplication.this, "Do you want to save before quitting?");
				if (answer == JOptionPane.YES_OPTION) {
					saveFileAs();
					dispose();
				}
				else if(answer == JOptionPane.NO_OPTION)
					dispose();
				else if(answer==0)
					;



			}
		});	

		navMenuItems.get("Find By Surname").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){

				String sName = JOptionPane.showInputDialog("Search for surname: ");
				boolean found = false;

				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {

					if(sName.equalsIgnoreCase((entry.getValue().getSurname().trim()))){
						found = true;
						fields.get("Account ID").setText(entry.getValue().getAccountID()+"");
						fields.get("Account Number").setText(entry.getValue().getAccountNumber());
						fields.get("Last Name").setText(entry.getValue().getSurname());
						fields.get("First Name").setText(entry.getValue().getFirstName());
						fields.get("Account Type").setText(entry.getValue().getAccountType());
						fields.get("Balance").setText(entry.getValue().getBalance()+"");
						fields.get("Overdraft").setText(entry.getValue().getOverdraft()+"");

					}
				}		
				if(found)
					JOptionPane.showMessageDialog(null, "Surname  " + sName + " found.");
				else
					JOptionPane.showMessageDialog(null, "Surname " + sName + " not found.");
			}
		});

		navMenuItems.get("Find By Account Number").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){

				String accNum = JOptionPane.showInputDialog("Search for account number: ");
				boolean found = false;

				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {

					if(accNum.equals(entry.getValue().getAccountNumber().trim())){
						found = true;
						fields.get("Account ID").setText(entry.getValue().getAccountID()+"");
						fields.get("Account Number").setText(entry.getValue().getAccountNumber());
						fields.get("Last Name").setText(entry.getValue().getSurname());
						fields.get("First Name").setText(entry.getValue().getFirstName());
						fields.get("Account Type").setText(entry.getValue().getAccountType());
						fields.get("Balance").setText(entry.getValue().getBalance()+"");
						fields.get("Overdraft").setText(entry.getValue().getOverdraft()+"");						

					}			 
				}
				if(found)
					JOptionPane.showMessageDialog(null, "Account number " + accNum + " found.");
				else
					JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");

			}
		});

		deposit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String accNum = JOptionPane.showInputDialog("Account number to deposit into: ");
				boolean found = false;

				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					if(accNum.equals(entry.getValue().getAccountNumber().trim())){
						found = true;
						String toDeposit = JOptionPane.showInputDialog("Account found, Enter Amount to Deposit: ");
						entry.getValue().setBalance(entry.getValue().getBalance() + Double.parseDouble(toDeposit));
						displayDetails(entry.getKey());
						//balanceTextField.setText(entry.getValue().getBalance()+"");
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
			}
		});

		withdraw.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String accNum = JOptionPane.showInputDialog("Account number to withdraw from: ");
				String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ");
				boolean found;

				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {


					if(accNum.equals(entry.getValue().getAccountNumber().trim())){

						found = true;

						if(entry.getValue().getAccountType().trim().equals("Current")){
							if(Double.parseDouble(toWithdraw) > entry.getValue().getBalance() + entry.getValue().getOverdraft())
								JOptionPane.showMessageDialog(null, "Transaction exceeds overdraft limit");
							else{
								entry.getValue().setBalance(entry.getValue().getBalance() - Double.parseDouble(toWithdraw));
								displayDetails(entry.getKey());
							}
						}
						else if(entry.getValue().getAccountType().trim().equals("Deposit")){
							if(Double.parseDouble(toWithdraw) <= entry.getValue().getBalance()){
								entry.getValue().setBalance(entry.getValue().getBalance()-Double.parseDouble(toWithdraw));
								displayDetails(entry.getKey());
							}
							else
								JOptionPane.showMessageDialog(null, "Insufficient funds.");
						}
					}					
				}
			}
		});

		calcInterest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					if(entry.getValue().getAccountType().equals("Deposit")){
						double equation = 1 + ((interestRate)/100);
						entry.getValue().setBalance(entry.getValue().getBalance()*equation);
						//System.out.println(equation);
						JOptionPane.showMessageDialog(null, "Balances Updated");
						displayDetails(entry.getKey());
					}
				}
			}
		});		
	}

	private void setMenuItems(Map<String, JMenuItem> items, JMenu menu, ArrayList<String> menuItems) {
		menuItems.forEach(item ->{
			items.put(item, new JMenuItem(item));
			menu.add(items.get(item));
		});
	}

	public void saveOpenValues(){		
		if (openValues){
			fields.get("Last Name").setEditable(false);
			fields.get("First Name").setEditable(false);

			table.get(currentItem).setSurname(fields.get("Last Name").getText());
			table.get(currentItem).setFirstName(fields.get("First Name").getText());
		}
	}	

	public void displayDetails(int currentItem) {	
		fields.get("Account ID").setText(table.get(currentItem).getAccountID()+"");
		fields.get("Account Number").setText(table.get(currentItem).getAccountNumber());
		fields.get("Surname").setText(table.get(currentItem).getSurname());
		fields.get("First Name").setText(table.get(currentItem).getFirstName());
		fields.get("Account Type").setText(table.get(currentItem).getAccountType());
		fields.get("Balance").setText(table.get(currentItem).getBalance()+"");
		if(fields.get("Account Type").getText().trim().equals("Current"))
			fields.get("Overdraft").setText(table.get(currentItem).getOverdraft()+"");

		else
			fields.get("Overdraft").setText("Only applies to current accs");

	}

	private static RandomAccessFile input;
	private static RandomAccessFile output;
	private static final int NUMBER_RECORDS = 100;


	public static void openFileRead()
	{

		table.clear();

		fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

		} else {
		}


		try // open file
		{
			if(fc.getSelectedFile()!=null)
				input = new RandomAccessFile( fc.getSelectedFile(), "r" );
		} // end try
		catch ( IOException ioException )
		{
			JOptionPane.showMessageDialog(null, "File Does Not Exist.");
		} // end catch

	} // end method openFile

	static String fileToSaveAs = "";

	public static void openFileWrite()
	{
		if(fileToSaveAs!=""){
			try // open file
			{
				output = new RandomAccessFile( fileToSaveAs, "rw" );
				JOptionPane.showMessageDialog(null, "Accounts saved to " + fileToSaveAs);
			} // end try
			catch ( IOException ioException )
			{
				JOptionPane.showMessageDialog(null, "File does not exist.");
			} // end catch
		}
		else
			saveToFileAs();
	}

	public static void saveToFileAs()
	{

		fc = new JFileChooser();

		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			fileToSaveAs = file.getName();
			JOptionPane.showMessageDialog(null, "Accounts saved to " + file.getName());
		} else {
			JOptionPane.showMessageDialog(null, "Save cancelled by user");
		}


		try {
			if(fc.getSelectedFile()==null){
				JOptionPane.showMessageDialog(null, "Cancelled");
			}
			else
				output = new RandomAccessFile(fc.getSelectedFile(), "rw" );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	public static void closeFile() 
	{
		try // close file and exit
		{
			if ( input != null )
				input.close();
		} // end try
		catch ( IOException ioException )
		{

			JOptionPane.showMessageDialog(null, "Error closing file.");//System.exit( 1 );
		} // end catch
	} // end method closeFile

	public static void readRecords()
	{

		RandomAccessBankAccount record = new RandomAccessBankAccount();



		try // read a record and display
		{
			while ( true )
			{
				do
				{
					if(input!=null)
						record.read( input );
				} while ( record.getAccountID() == 0 );



				BankAccount ba = new BankAccount(record.getAccountID(), record.getAccountNumber(), record.getFirstName(),
						record.getSurname(), record.getAccountType(), record.getBalance(), record.getOverdraft());


				Integer key = Integer.valueOf(ba.getAccountNumber().trim());

				int hash = (key%TABLE_SIZE);


				while(table.containsKey(hash)){

					hash = hash+1;
				}

				table.put(hash, ba);


			} // end while
		} // end try
		catch ( EOFException eofException ) // close file
		{
			return; // end of file was reached
		} // end catch
		catch ( IOException ioException )
		{
			JOptionPane.showMessageDialog(null, "Error reading file.");
			System.exit( 1 );
		} // end catch
	}

	public static void saveToFile(){


		RandomAccessBankAccount record = new RandomAccessBankAccount();

		Scanner input = new Scanner( System.in );


		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			record.setAccountID(entry.getValue().getAccountID());
			record.setAccountNumber(entry.getValue().getAccountNumber());
			record.setFirstName(entry.getValue().getFirstName());
			record.setSurname(entry.getValue().getSurname());
			record.setAccountType(entry.getValue().getAccountType());
			record.setBalance(entry.getValue().getBalance());
			record.setOverdraft(entry.getValue().getOverdraft());

			if(output!=null){

				try {
					record.write( output );
				} catch (IOException u) {
					u.printStackTrace();
				}
			}

		}


	}

	public static void writeFile(){
		openFileWrite();
		saveToFile();
		//addRecords();
		closeFile();
	}

	public static void saveFileAs(){
		saveToFileAs();
		saveToFile();	
		closeFile();
	}

	public static void readFile(){
		openFileRead();
		readRecords();
		closeFile();		
	}

	public void put(int key, BankAccount value){
		int hash = (key%TABLE_SIZE);

		while(table.containsKey(key)){
			hash = hash+1;

		}
		table.put(hash, value);

	}

	public static void main(String[] args) {
		BankApplication ba = new BankApplication();
		ba.setSize(1200,400);
		ba.pack();
		ba.setVisible(true);
	}


}