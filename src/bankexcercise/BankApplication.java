package bankexcercise;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class BankApplication extends JFrame {

	private static final long serialVersionUID = 1L;
	private final static int TABLE_SIZE = 29;

	static String fileToSaveAs = "";
	private static RandomAccessFile input;
	private static RandomAccessFile output;
	private FileHelp fileHelp;

	static HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	private JMenuBar menuBar;
	private JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;

	private JMenuItem closeApp;

	private String gui [] = {"Account ID", "Account Number", "First Name", "Last Name" , "Account Type", "Balance", "Overdraft"};
	private Map<String, JLabel> labels = new HashMap<String, JLabel>();
	private Map<String, JTextField> fields = new HashMap<String, JTextField>();
	private Map<String, JMenuItem> recMenuItems = new HashMap<String, JMenuItem>();
	private Map<String, JMenuItem> navMenuItems = new HashMap<String, JMenuItem>();
	private Map<String, JMenuItem> fileMenuItems = new HashMap<String, JMenuItem>();
	private Map<String, JMenuItem> transMenuItems = new HashMap<String, JMenuItem>();

	private String[] pics = {"first.png", "prev.png", "next.png", "last.png" };
	private JButton[] itemButtons = new JButton[pics.length];

	static JFileChooser fc;
	private JTable jTable;
	private double interestRate;

	private int currentItem = 0;

	private boolean openValues;

	public BankApplication() {

		super("Bank Application");
		fileHelp = new FileHelp(input, output, fileToSaveAs);
		initComponents();
	}

	public void initComponents() {
		setLayout(new BorderLayout());
		JPanel displayPanel = new JPanel(new MigLayout());

		for (String str: gui) {
			labels.put(str, new JLabel(str + ": "));
			if(str.equals("First Name") || str.equals("Last Name")) {
				fields.put(str, new JTextField(20));
			} else if (str.equals("Account Number")) {
				fields.put(str, new JTextField(8));
			}
			else {
				fields.put(str, new JTextField(15));
			}
			fields.get(str).setEditable(false);

			displayPanel.add(labels.get(str), "growx, pushx");
			displayPanel.add(fields.get(str), "growx, pushx, wrap");
		}

		add(displayPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

		for(int i=0;i< pics.length; i++) {
			itemButtons[i] = new JButton(new ImageIcon(pics[i]));
			buttonPanel.add(itemButtons[i]);
		}

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

		ArrayList<String> transMenuLabels = new ArrayList<String>(
				Arrays.asList("Deposit","Withdraw","Calculate Interest"));
		setMenuItems(transMenuItems, transactionsMenu, transMenuLabels);

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
				if(table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else if(table.get(currentItem)==null) {
					JOptionPane.showMessageDialog(null,"No Account Selected");
				}
				else {
					if(table.get(currentItem).getAccountType().trim().equals("Current")){
						String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
						fields.get("Overdraft").setText(newOverdraftStr);
						table.get(currentItem).setOverdraft(Double.parseDouble(newOverdraftStr));
					}
					else
						JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");
				}
			}
		});

		ActionListener first = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else if(table.get(currentItem)==null) {
					JOptionPane.showMessageDialog(null,"No Account Selected");
				}
				else {
					saveOpenValues();

					currentItem=0;
					while(!table.containsKey(currentItem)){
						currentItem++;
					}
					displayDetails(currentItem);

				}
			}
		};

		ActionListener next = new ActionListener(){
			public void actionPerformed(ActionEvent e){

				ArrayList<Integer> keyList = new ArrayList<Integer>();
				int i=0;

				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else if(table.get(currentItem)==null) {
					JOptionPane.showMessageDialog(null,"No Account Selected");
				}
				else {
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
			}
		};

		ActionListener prev = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ArrayList<Integer> keyList = new ArrayList<Integer>();
				int i=0;

				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else if(table.get(currentItem)==null) {
					JOptionPane.showMessageDialog(null,"No Account Selected");
				}
				else {
					while(i<TABLE_SIZE){
						i++;
						if(table.containsKey(i))
							keyList.add(i);
					}

					int minKey = Collections.min(keyList);

					if(currentItem>minKey){
						currentItem--;
						while(!table.containsKey(currentItem)){
							currentItem--;
						}
					}
					displayDetails(currentItem);				
				}
			}
		};

		ActionListener last = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();

				currentItem =TABLE_SIZE;

				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else if(table.get(currentItem)==null) {
					JOptionPane.showMessageDialog(null,"No Account Selected");
				}
				else {
					while(!table.containsKey(currentItem)){
						currentItem--;

					}

					displayDetails(currentItem);
				}
			}
		};

		itemButtons[2].addActionListener(next);
		navMenuItems.get("Next Item").addActionListener(next);

		itemButtons[1].addActionListener(prev);
		navMenuItems.get("Previous Item").addActionListener(prev);

		itemButtons[0].addActionListener(first);
		navMenuItems.get("First Item").addActionListener(first);

		itemButtons[3].addActionListener(last);
		navMenuItems.get("Last Item").addActionListener(last);

		recMenuItems.get("Delete Item").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else {
					table.remove(currentItem);
					JOptionPane.showMessageDialog(null, "Account Deleted");


					currentItem=0;
					while(!table.containsKey(currentItem)){
						currentItem++;
					}
					displayDetails(currentItem);
				}
			}
		});

		recMenuItems.get("Create Item").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new CreateBankDialog(table);		
			}
		});


		recMenuItems.get("Modify Item").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else {
					fields.get("Last Name").setEditable(true);
					fields.get("First Name").setEditable(true);
					openValues = true;
				}
			}
		});

		recMenuItems.get("Set Interest").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else if(table.get(currentItem)==null) {
					JOptionPane.showMessageDialog(null,"No Account Selected");
				}
				else {
					interestRate = Double.parseDouble(JOptionPane.showInputDialog("Enter Interest Rate: (do not type the % sign)"));
				}
			}
		});

		navMenuItems.get("List All Records").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){

				JFrame frame = new JFrame("TableDemo");

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
				frame.setVisible(true);			
			}
		});

		fileMenuItems.get("Open File").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fileHelp.readFile(table, fc);
				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);
			}
		});

		fileMenuItems.get("Save File").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fileHelp.writeFile(table,fc);
			}
		});

		fileMenuItems.get("Save As").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fileHelp.saveFileAs(table,fc);
			}
		});

		closeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int answer = JOptionPane.showConfirmDialog(BankApplication.this, "Do you want to save before quitting?");
				if (answer == JOptionPane.YES_OPTION) {
					fileHelp.saveFileAs(table,fc);
					dispose();
				}
				else if(answer == JOptionPane.NO_OPTION)
					dispose();
			}
		});	

		navMenuItems.get("Find By Surname").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(table.size()==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else {
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
			}
		});

		navMenuItems.get("Find By Account Number").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(table.size()==0) {
					JOptionPane.showMessageDialog(null, "Empty Set");
				}
				else {
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
			}
		});

		transMenuItems.get("Deposit").addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
			if (table.size() ==0) {
				JOptionPane.showMessageDialog(null, "No accounts to deposit to");
			}
			else {
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
		}
		});

		transMenuItems.get("Withdraw").addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (table.size() ==0) {
					JOptionPane.showMessageDialog(null, "No accounts to withdraw from");
				}
				else {
					String accNum = JOptionPane.showInputDialog("Account number to withdraw from: ");
					//String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ");
					boolean found = false;

					for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {


						if(accNum.equals(entry.getValue().getAccountNumber().trim())){
							String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ");
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
					if (!found)
						JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
				}
			}
		});

		transMenuItems.get("Calculate Interest").addActionListener(new ActionListener(){
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

	private void saveOpenValues(){		
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
		fields.get("Last Name").setText(table.get(currentItem).getSurname());
		fields.get("First Name").setText(table.get(currentItem).getFirstName());
		fields.get("Account Type").setText(table.get(currentItem).getAccountType());
		fields.get("Balance").setText(table.get(currentItem).getBalance()+"");
		if(fields.get("Account Type").getText().trim().equals("Current"))
			fields.get("Overdraft").setText(table.get(currentItem).getOverdraft()+"");

		else
			fields.get("Overdraft").setText("Only applies to current accs");

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