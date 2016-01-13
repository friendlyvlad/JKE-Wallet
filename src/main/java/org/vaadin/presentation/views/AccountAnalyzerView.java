package org.vaadin.presentation.views;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.vaadin.backend.TransactionService;
import org.vaadin.backend.domain.Transaction;
import org.vaadin.backend.PaymentService;
import org.vaadin.backend.domain.Payment;
import org.vaadin.backend.AccountService;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.CheckService;
import org.vaadin.backend.domain.Check;

import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.presentation.ScreenSize;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;


import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsFunnel;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

/**
 * An example view that just make some simple analysis for the data and displays
 * it in various charts.
 */
@CDIView("")
@ViewMenuItem(icon = FontAwesome.BAR_CHART_O, order = 1)
public class AccountAnalyzerView extends MVerticalLayout implements View {

    @Inject
    TransactionService tservice;
    
    @Inject
    PaymentService pservice;
    
    @Inject
    AccountService aservice;

	@Inject
    CheckService cservice;

	private String authname = "unknown";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        removeAllComponents();

		try {
		        Subject s = WSSubject.getCallerSubject();
				    		
				if (s != null) {
		        Set<Principal> principals = s.getPrincipals();
		        if (principals != null && principals.size() > 0) {
		            authname = principals.iterator().next().getName();
		        }
		    }
    		
	    } catch (Exception exc) {
	        authname="bad stuff";
	    }
		
		tservice.updateTransactions(authname);
	
        setMargin(new MMarginInfo(false, true));
        add(new Header("Account Analysis for " + "unknown").setHeaderLevel(2));
        //comment line above and uncomment line below for fix of our fake bug
        //add(new Header("Account Analysis for " + authname).setHeaderLevel(2));

        List<Account> accountData = aservice.findByName(authname);
        //add(ageDistribution(accountData));
        final Component accountInfo = createAccountPieChart(accountData);
        //final Component gender = genderDistribution(customerData);
        if (ScreenSize.getScreenSize() == ScreenSize.SMALL) {
            addComponents(accountInfo);
        } else {
            addComponent(new MHorizontalLayout(accountInfo).withFullWidth());
        }

    }
    
    
    private static Panel wrapInPanel(Chart chart, String caption) {
        Panel panel = new Panel(caption, chart);
        panel.setHeight("300px");
        chart.setSizeFull();
        return panel;
    }

	private Component createAccountPieChart(List<Account> accountData) {
        
        Chart chart = getBasicChart(ChartType.PIE);

        Configuration conf = chart.getConfiguration();


        PlotOptionsPie plotOptions = new PlotOptionsPie();
        Labels dataLabels = new Labels();
        dataLabels.setEnabled(true);
        dataLabels.setFormat("{point.name}: {percentage:.0f}%");
        plotOptions.setDataLabels(dataLabels);
        conf.setPlotOptions(plotOptions);

        final DataSeries series = new DataSeries();
        
        //iterate through accounts and create new data series for each one. also sets amount to current balance
        for(int i = 0; i < accountData.size(); i++){
    		series.add(new DataSeriesItem(accountData.get(i).getAccountName(), accountData.get(i).getBalance()));
    	}
        
        conf.setSeries(series);
        return wrapInPanel(chart, "Account Distribution");
    }

/* implement bar chart here with payment details by payee name
    private Component createPaymentBarChart(List<Payment> paymentData) {
        
        Chart chart = getBasicChart(ChartType.BAR);

        Configuration conf = chart.getConfiguration();


        PlotOptionsPie plotOptions = new PlotOptionsBar();
        Labels dataLabels = new Labels();
        dataLabels.setEnabled(true);
        dataLabels.setFormat("{point.name}: {percentage:.0f}%");
        plotOptions.setDataLabels(dataLabels);
        conf.setPlotOptions(plotOptions);

        final DataSeries series = new DataSeries();
        
        //iterate through accounts and create new data series for each one. also sets amount to current balance
        for(int i = 0; i < accountData.size(); i++){
    		series.add(new DataSeriesItem(accountData.get(i).getAccountName(), accountData.get(i).getBalance()));
    	}
        
        conf.setSeries(series);
        return wrapInPanel(chart, "Account Distribution");
    }
*/

    private Chart getBasicChart(ChartType type) {
        Chart chart = new Chart(type);
        // title from panel
        chart.getConfiguration().setTitle("");
        return chart;
    }

}