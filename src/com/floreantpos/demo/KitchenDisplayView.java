/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is Infinity POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.Messages;
import com.floreantpos.actions.LogoutAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.model.Printer;
import com.floreantpos.model.dao.KitchenTicketDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosComboRenderer;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.HeaderPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.ViewPanel;

public class KitchenDisplayView extends ViewPanel implements ActionListener {

	public final static String VIEW_NAME = "KD"; //$NON-NLS-1$
	private static KitchenDisplayView instance;
	private JComboBox<Printer> cbPrinters = new JComboBox<Printer>();
	private JComboBox<OrderType> cbTicketTypes = new JComboBox<OrderType>();

	private HeaderPanel headerPanel;
	private JPanel filterPanel;
	private JLabel lblFilter;

	private PosButton btnFilter;

	private KitchenTicketListPanel ticketPanel = new KitchenTicketListPanel();

	private Timer viewUpdateTimer;

	private PosButton btnLogout;

	public KitchenDisplayView(boolean showHeader) {
		setLayout(new BorderLayout(5, 5));
		PosPrinters printers = Application.getPrinters();
		List<Printer> kitchenPrinters = printers.getKitchenPrinters();
		DefaultComboBoxModel<Printer> printerModel = new DefaultComboBoxModel<Printer>();
		printerModel.addElement(null);
		for (Printer printer : kitchenPrinters) {
			printerModel.addElement(printer);
		}

		Font font = getFont().deriveFont(18f);

		cbPrinters.setFont(font);
		cbPrinters.setRenderer(new PosComboRenderer());
		cbPrinters.setModel(printerModel);
		cbPrinters.addActionListener(this);

		JPanel firstTopPanel = new JPanel(new BorderLayout(5, 5));
		if (showHeader) {
			headerPanel = new HeaderPanel();
			firstTopPanel.add(headerPanel, BorderLayout.NORTH);
		}

		filterPanel = new JPanel();

		PosButton btnBack = new PosButton(Messages.getString("KitchenDisplayView.1")); //$NON-NLS-1$
		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RootView.getInstance().showDefaultView();
			}
		});

		JLabel label = new JLabel(Messages.getString("KitchenDisplayView.5")); //$NON-NLS-1$
		label.setFont(font);

		JLabel label2 = new JLabel(Messages.getString("KitchenDisplayView.6")); //$NON-NLS-1$
		label2.setFont(font);

		filterPanel.setLayout(new MigLayout("", "[][][][][fill,grow][]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		filterPanel.add(label);
		filterPanel.add(cbPrinters);

		filterPanel.add(label2);
		filterPanel.add(cbTicketTypes);

		btnFilter = new PosButton(Messages.getString("KitchenDisplayView.2")); //$NON-NLS-1$
		btnFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				KitchenFilterDialog dialog = new KitchenFilterDialog();
				dialog.add(filterPanel, BorderLayout.CENTER);
				dialog.open();
			}
		});

		JPanel topPanel = new JPanel(new MigLayout("fill, ins 2 2 0 2", "[][fill, grow][]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//topPanel.setBorder(BorderFactory.createTitledBorder("Filter: All Printers- All Orders")); //$NON-NLS-1$

		//topPanel.add(label);
		//topPanel.add(cbPrinters);
		
		Dimension size=PosUIManager.getSize(60, 40); 
		
		Font filterFont = getFont().deriveFont(Font.BOLD, 12f);
		lblFilter = new JLabel("Filter: All Printers- All Orders"); //$NON-NLS-1$
		lblFilter.setForeground(new Color(49, 106, 196));
		lblFilter.setFont(filterFont);
		topPanel.add(lblFilter);
		topPanel.add(btnFilter,"w "+ size.width+"!,h "+size.height+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		topPanel.add(btnBack, "w "+ size.width+"!, h "+size.height+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		topPanel.setBackground(Color.white);

		cbTicketTypes.setFont(font);
		cbTicketTypes.setRenderer(new PosComboRenderer());
		DefaultComboBoxModel<OrderType> ticketTypeModel = new DefaultComboBoxModel<OrderType>();
		for(OrderType orderType:Application.getInstance().getOrderTypes()) {
			ticketTypeModel.addElement(orderType); 
		}
		ticketTypeModel.insertElementAt(null, 0);
		cbTicketTypes.setModel(ticketTypeModel);
		cbTicketTypes.setSelectedIndex(0);
		cbTicketTypes.addActionListener(this);

		//topPanel.add(label2);
		//topPanel.add(cbTicketTypes);

		btnLogout = new PosButton(new LogoutAction(true, false)); //$NON-NLS-1$
		//btnLogout.addActionListener(this);
		topPanel.add(btnLogout, "w "+ size.width+"!, h "+size.height+"!, wrap"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		topPanel.add(new JSeparator(), "grow,span"); //$NON-NLS-1$

		firstTopPanel.setPreferredSize(new Dimension(0, PosUIManager.getSize(50)));
		firstTopPanel.add(topPanel);
		add(firstTopPanel, BorderLayout.NORTH);

		ticketPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		//JScrollPane scrollPane = new JScrollPane(ticketPanel);
		//scrollPane.getHorizontalScrollBar().setSize(new Dimension(100, 60));
		//scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(100, 60));
		add(ticketPanel);

		add(ticketPanel.getPaginationPanel(), BorderLayout.SOUTH);

		viewUpdateTimer = new Timer(5 * 1000, this);
		viewUpdateTimer.setRepeats(true);
	}

	public void addTicket(KitchenTicket ticket) {
		addTicket(ticket, true);
	}

	private synchronized void addTicket(KitchenTicket ticket, boolean updateView) {
		if (!isShowing())
			return;

		Printer selectedPrinter = (Printer) cbPrinters.getSelectedItem();
		if (selectedPrinter != null && !selectedPrinter.equals(ticket.getPrinters())) {
			return;
		}

		OrderType selectedTicketType = (OrderType) cbTicketTypes.getSelectedItem();
		if (selectedTicketType != null && selectedTicketType != ticket.getType()) {
			return;
		}

		if (ticketPanel.addTicket(ticket)) {
			if (updateView) {
				//ticketPanel.revalidate();
				ticketPanel.repaint();
			}
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b) {
			updateTicketView();

			if (!viewUpdateTimer.isRunning()) {
				viewUpdateTimer.start();
			}
		}
		else {
			cleanup();
		}
	}

	public synchronized void cleanup() {
		viewUpdateTimer.stop();
		ticketPanel.removeAll();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() != null && e.getActionCommand().equalsIgnoreCase("log out")) { //$NON-NLS-1$
			Application.getInstance().doLogout();
		}
		if (e.getSource() == viewUpdateTimer) {
			updateTicketView();
		}
		else {
			ticketPanel.removeAll();
			updateTicketView();
		}
	}

	private synchronized void updateTicketView() {
		try {
			viewUpdateTimer.stop();

			List<KitchenTicket> list = KitchenTicketDAO.getInstance().findAllOpen();

			for (KitchenTicket kitchenTicket : list) {
				addTicket(kitchenTicket, false);
			}
			//ticketPanel.updateView();
			ticketPanel.repaint();

		} catch (Exception e2) {
			POSMessageDialog.showError(this, e2.getMessage(), e2);
		} finally {
			viewUpdateTimer.restart();
		}
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	public synchronized static KitchenDisplayView getInstance() {
		if (instance == null) {
			instance = new KitchenDisplayView(false);
		}
		return instance;
	}
}
