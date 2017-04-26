package com.shenkar.gui;
import com.shenkar.funcationality.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Set;

public class RatesGUI {
	private JFrame frame;
	private JTable table;
	private JPanel topDatePanel;
	private JPanel midPanel;
	private JPanel midLeftPanel;
	private JPanel midCenterPanel;
	private JPanel midRightPanel;
	private DefaultTableModel dTable;
	private JButton convertButton;
	private JButton refreshButton;
	private JLabel amountLabel;
	private JLabel toLabel;
	private JLabel fromLabel;
	private JTextField fromTF;
	private JTextField toTF;
	private JTextField amountTF;
	private JTextField resultTF;
	private JTextField resultTF1;
	private JTextField dateTF;
	private IExchangeRates _exchangeRates;

public RatesGUI(IExchangeRates exchangeRates){
	this._exchangeRates = exchangeRates;
	Thread Model = new Thread((Runnable) this._exchangeRates);
	Model.start();
	while (!this._exchangeRates.isFileReady()){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
//	_exchangeRates.getTableSize();
	dTable = new DefaultTableModel(_exchangeRates.getCurrency().size()+1 , _exchangeRates.getCurrency().size()+1);
	frame = new JFrame("Exchange Rates - Final Project");
	table = new JTable(dTable);
	topDatePanel = new JPanel();
	midPanel = new JPanel();
	convertButton = new JButton("Convert!");
	midRightPanel = new JPanel();
	midLeftPanel = new JPanel();
	amountLabel = new JLabel("   Amount: ");
	toLabel = new JLabel("   To: ");
	fromLabel = new JLabel("   From: ");
	amountTF = new JTextField(12);
	toTF = new JTextField(12);
	fromTF = new JTextField(12);
	resultTF = new JTextField(15);
	resultTF1 = new JTextField(15);
	refreshButton = new JButton("Click to refresh");
	dateTF = new JTextField("Last Update Date is:" + _exchangeRates.getDate());
	midCenterPanel = new JPanel();
}

public void start(){
	resultTF.setEditable(false);
	resultTF1.setEditable(false);
	dateTF.setEditable(false);
	frame.setLayout(new BorderLayout());
	topDatePanel.add(refreshButton);
	topDatePanel.add(dateTF);
	midPanel.setLayout(new BorderLayout());
	midPanel.add(midCenterPanel);
	midPanel.add(convertButton, BorderLayout.CENTER);
	midRightPanel.setLayout(new GridLayout(2, 1));
	midRightPanel.add(resultTF);
	midRightPanel.add(resultTF1);
	midPanel.add(midRightPanel, BorderLayout.EAST);
	midLeftPanel.setLayout(new GridLayout(3, 2));
	midLeftPanel.add(fromLabel);
	midLeftPanel.add(fromTF);
	midLeftPanel.add(toLabel);
	midLeftPanel.add(toTF);
	midLeftPanel.add(amountLabel);
	midLeftPanel.add(amountTF);
	midPanel.add(midLeftPanel, BorderLayout.WEST);
	table.setGridColor(Color.BLACK);
	table.setBackground(Color.LIGHT_GRAY);
	frame.add(topDatePanel, BorderLayout.NORTH);
	frame.add(midPanel, BorderLayout.CENTER);
	frame.add(table, BorderLayout.SOUTH);
	frame.setSize(700,500);
	frame.setResizable(false);
	dateTF.setText("Last Updated Date: " + _exchangeRates.getDate());
	createTable();
	table.setEnabled(false);
	frame.setVisible(true);
	frame.pack();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e){
			System.exit(0);
		}
	});
	refreshButton.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			_exchangeRates.ParseXML();
			dateTF.setText("Last Updated Date: " + _exchangeRates.getDate());
		}
		
	});
	
	convertButton.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!(fromTF.getText().isEmpty() || toTF.getText().isEmpty() || amountTF.getText().isEmpty())){
				String from = fromTF.getText().trim().toUpperCase();
				String to = toTF.getText().trim().toUpperCase();
				float amount = Float.parseFloat(amountTF.getText());
				try{
					float sum = _exchangeRates.calculateResult(from, to, amount);
					System.out.println(sum);
//					System.out.println(sum);
					resultTF.setText(String.valueOf(sum));
					resultTF1.setText(fromTF.getText() + " -> " + toTF.getText());
				}
				catch (Exception r){
					resultTF.setText("Error!!!");
					resultTF1.setText(r.getMessage());
				}
			
			}
			
		}
	});
	
	}
	public DefaultTableModel getGUITable(){
		return this.dTable;
	}
	
	public void createTable(){
		int i = 1, j = 1;
		this.dTable.setValueAt("Currencies", 0, 0);
		for(String tempCurr: _exchangeRates.getCurrency().keySet()){
			dTable.setValueAt(tempCurr, i, 0);
			dTable.setValueAt(tempCurr, 0, j);
			j++;
			i++;
		}
		for(i = 1; i < _exchangeRates.getCurrency().size()+1; i++){
			for(j = 1; j < _exchangeRates.getCurrency().size()+1; j++){
				String temp=_exchangeRates.CalculateRates(dTable.getValueAt(i, 0).toString(), dTable.getValueAt(0, j).toString());
				dTable.setValueAt(temp,i, j);
			}
		}
			
	}


}
