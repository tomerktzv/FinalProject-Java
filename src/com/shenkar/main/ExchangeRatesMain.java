package com.shenkar.main;
import org.apache.log4j.BasicConfigurator;
import javax.swing.SwingUtilities;

import com.shenkar.funcationality.ServerApplication;
import com.shenkar.gui.RatesGUI;

public class ExchangeRatesMain {

		   public static void main(String[] args) throws InterruptedException {
		    	BasicConfigurator.configure(); //for the log4j - logger object
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		                RatesGUI ProgramGUI = new RatesGUI(new ServerApplication());
		                ProgramGUI.start();
		            }
		        });
		    }
		}