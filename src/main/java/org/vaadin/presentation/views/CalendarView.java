package org.vaadin.presentation.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import javax.enterprise.event.Observes;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import java.lang.Math;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.BeanItemContainer;

/*
import com.vaadin.ui.components.calendar.BasicWeekClickHandler;
import com.vaadin.ui.components.calendar.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.EventClickHandler;
import com.vaadin.ui.components.calendar.handler.*;
import com.vaadin.ui.components.calendar.*;
import com.vaadin.ui.components.calendar.CalendarComponentEvent.*;
*/

import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;

//button to handle calendar nav
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Alignment;

import org.vaadin.backend.PaymentService;
import org.vaadin.presentation.views.PaymentEvent.Type;
import org.vaadin.backend.CheckService;
import org.vaadin.backend.domain.Payment;
import org.vaadin.backend.domain.Check;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.cdiviewmenu.ViewMenuUI;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import com.vaadin.ui.Alignment;
import org.vaadin.presentation.ScreenSize;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import org.vaadin.backend.domain.Transaction;
import org.vaadin.backend.TransactionService;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.domain.Gender;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
	
import com.vaadin.event.Action;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.ContainerEventProvider;
import com.vaadin.ui.components.calendar.ContainerEventProvider;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import com.vaadin.server.Page;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.UI;
import org.vaadin.presentation.AppUI;
import org.vaadin.presentation.ScreenSize;

@CDIView("calendar")
@ViewMenuItem(icon = FontAwesome.CALENDAR)
public class CalendarView extends MVerticalLayout implements View {


	@Inject
    private PaymentService pservice;
    
    @Inject
    private CheckService cservice;
    
    @Inject
    private  TransactionService tservice;

    @Inject
    PaymentForm paymentEditor;
    
    String authname = "unknown";

	Calendar calendar = new Calendar("Upcoming Payments and Checks");
	
	MHorizontalLayout mainContent = new MHorizontalLayout(calendar).
            withFullWidth().withMargin(true);

	GregorianCalendar currDate = new GregorianCalendar();

	@PostConstruct
	void init() {    	
		
		// Use US English for date/time representation 
		calendar.setLocale(new Locale("en", "US"));
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		// Set start date to first date in this month
	    GregorianCalendar startDate = new GregorianCalendar();
	    //commenting out below to fix start day of calendar to current day
	    startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
	    calendar.setStartDate(startDate.getTime());
	
	    // Set end date to last day of this month
	    GregorianCalendar endDate = new GregorianCalendar();
	    //commenting out to set end date to 30 days after start
	    //endDate.set(java.util.Calendar.DAY_OF_MONTH, 28);
	    
	    //endDate.add((GregorianCalendar.MONTH), 1);
	    //calendar.setEndDate(endDate.getTime());
	    //calendar.expandEndDate();
	    
	    endDate.set(java.util.Calendar.DATE, 1);
        endDate.roll(java.util.Calendar.DATE, -1);
        calendar.setEndDate(endDate.getTime());
	
	
		try {
		        Subject s = WSSubject.getCallerSubject();
				    		
				if (s != null) {
		        Set<Principal> principals = s.getPrincipals();
		        if (principals != null && principals.size() > 0) {
		            authname = principals.iterator().next().getName();
		        }
		    }
    		
	    } catch (Exception exc) {
	        authname = "bad stuff";
	    }
		
		
		//BeanItemContainer<PaymentCalendarEvent> paymentContainer = new BeanItemContainer<PaymentCalendarEvent>(PaymentCalendarEvent.class);
		List<Payment> payments = pservice.findByName(authname);
		//work on sorting later - for now ensure new payments are later than last...
		//payments.sort()
		//payments.sort(new Object[]{"Payment"}, new boolean[]{true});
    	
    	for(int i = 0; i < payments.size(); i++){
   			
   			if(!payments.get(i).getDate().before(currDate.getTime())){   			
	   			if(payments.get(i).getAmount() > 0){
	   				BasicEvent event = new BasicEvent(payments.get(i).getPayee(), Math.abs(payments.get(i).getAmount()) + " from " + payments.get(i).getAccType(),
	            	payments.get(i).getDate(),
	            	payments.get(i).getDate());
	            	
	            	java.util.Calendar cal = java.util.Calendar.getInstance();
				    cal.setTime(payments.get(i).getDate());
				    cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
	            	event.setEnd(cal.getTime());
	            	
	            	event.setStyleName("blue");
	            	calendar.addEvent(event);
            	}
	            else{
	            	BasicEvent event = new BasicEvent(payments.get(i).getPayee(), Math.abs(payments.get(i).getAmount()) + " to " + payments.get(i).getAccType(),
	            	payments.get(i).getDate(),
	            	payments.get(i).getDate());
	            	
	            	java.util.Calendar cal = java.util.Calendar.getInstance();
				    cal.setTime(payments.get(i).getDate());
				    cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
	            	event.setEnd(cal.getTime());
	            	
	            	event.setStyleName("blue");
	            	calendar.addEvent(event);
            	}	    		
    		}
    	}
    	
    	List<Check> checks = cservice.findByName(authname);
    	
    	for(int i = 0; i < checks.size(); i++){
   			
   			if(!checks.get(i).getDate().before(currDate.getTime())){   			
	   			if(checks.get(i).getAmount() > 0){
	   				BasicEvent event = new BasicEvent(checks.get(i).getDescription(), Math.abs(checks.get(i).getAmount()) + " from " + checks.get(i).getAccount(),
	            	checks.get(i).getDate(),
	            	checks.get(i).getDate());
	            	
	            	java.util.Calendar cal = java.util.Calendar.getInstance();
				    cal.setTime(checks.get(i).getDate());
				    cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
	            	event.setEnd(cal.getTime());
	            	
	            	event.setStyleName("green");
	            	calendar.addEvent(event);
            	}
	            else{
	            	BasicEvent event = new BasicEvent(checks.get(i).getDescription(), Math.abs(checks.get(i).getAmount()) + " to " + checks.get(i).getAccount(),
	            	checks.get(i).getDate(),
	            	checks.get(i).getDate());
	            	
	            	java.util.Calendar cal = java.util.Calendar.getInstance();
				    cal.setTime(checks.get(i).getDate());
				    cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
	            	event.setEnd(cal.getTime());
	            	
	            	event.setStyleName("green");
	            	calendar.addEvent(event);
            	}	    		
    		}
    	}
    	
    	List<Transaction> transactions = tservice.findByName(authname);
    	
    	for(int i = 0; i < transactions.size(); i++){
   			  			
   			if(transactions.get(i).getAmount() > 0){
   				BasicEvent event = new BasicEvent(transactions.get(i).getDescription(), Math.abs(transactions.get(i).getAmount()) + " from " + transactions.get(i).getAccount(),
            	transactions.get(i).getDate(),
            	transactions.get(i).getDate());
            	event.setStyleName("green");
            	
            	java.util.Calendar cal = java.util.Calendar.getInstance();
			    cal.setTime(transactions.get(i).getDate());
			    cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
            	event.setEnd(cal.getTime());
            	
            	calendar.addEvent(event);
        	}
            else{
            	BasicEvent event = new BasicEvent(transactions.get(i).getDescription(), Math.abs(transactions.get(i).getAmount()) + " to " + transactions.get(i).getAccount(),
            	transactions.get(i).getDate(),
            	transactions.get(i).getDate());
            	
            	java.util.Calendar cal = java.util.Calendar.getInstance();
			    cal.setTime(transactions.get(i).getDate());
			    cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
            	event.setEnd(cal.getTime());
            	
            	event.setStyleName("green");
            	calendar.addEvent(event);
        	}	    		
    	}
		
		//testing display only now
		//calendar.setContainerDataSource(paymentContainer, "caption", "description", "start", "end", "styleName");

		/* "Responsive Web Design" can be done with plain Java as well. Here we
         * e.g. do selective layouting and configure visible columns in
         * table based on available width */
        layout();
        /* If you wish the UI to adapt on window resize/page orientation
         * change, hook to BrowserWindowResizeEvent */
        UI.getCurrent().setResizeLazy(true);
        Page.getCurrent().addBrowserWindowResizeListener(
                new Page.BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            Page.BrowserWindowResizeEvent browserWindowResizeEvent) {                              
                                layout();
                            }
                });
		
		        
		//addComponent(new MHorizontalLayout(calendar).withFullWidth());        
		//addComponent(calendar);
	}
	
	private void layout() {
        removeAllComponents();
      
      	//GridLayout grid = new GridLayout(2, 3);
      
		calendar.setSizeFull();

		//mainContent.addComponents(calendar).withFullWidth().withMargin(true);

		//addComponents(
        //            new MHorizontalLayout(calendar).withFullWidth().withMargin(true));                 
                    //.alignAll(Alignment.MIDDLE_CENTER),
                    //mainContent);
                    
         //addCalendarEventListeners();
         
        Button monthButton = new Button("Month");
        Button weekButton = new Button("Week");
        Button previousButton = new Button("Previous");
        Button nextButton = new Button("Next");
        //final Date tempCurrDate = currDate;

		//add buttons to view
		addComponents(new MVerticalLayout().withFullHeight().with(
				calendar,
				new MHorizontalLayout(monthButton, weekButton),
                new MHorizontalLayout(previousButton, nextButton)));

		//create listeners for each button above
		monthButton.addClickListener(new Button.ClickListener() {
    		public void buttonClick(ClickEvent event) {
        		//Notification.show("you pressed month");
        		//for testing. replace with layout - month mode
        		//init();
        		GregorianCalendar tempStartDate = new GregorianCalendar();
			    tempStartDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
			    calendar.setStartDate(tempStartDate.getTime());
			    GregorianCalendar tempEndDate = new GregorianCalendar();
			    tempEndDate.set(java.util.Calendar.DATE, 1);
		        tempEndDate.roll(java.util.Calendar.DATE, -1);
		        calendar.setEndDate(tempEndDate.getTime());
    		}
		});
		
		weekButton.addClickListener(new Button.ClickListener() {
    		public void buttonClick(ClickEvent event) {
        		//Notification.show("Do not press this button again");
        		//above for testing. replace with layout - month mode
        		java.util.Calendar tempCal = java.util.Calendar.getInstance();
        		tempCal.setTime(new Date());
        		tempCal.set(java.util.Calendar.DAY_OF_WEEK, tempCal.getFirstDayOfWeek());
				calendar.setStartDate(tempCal.getTime());
				tempCal.add(java.util.Calendar.DATE, 6); 
        		calendar.setEndDate(tempCal.getTime());
    		}
		});
		
		previousButton.addClickListener(new Button.ClickListener() {
    		public void buttonClick(ClickEvent event) {
        		//Notification.show("you pressed previous");
        		//for testing. replace setStartDate with currdate minus one month
        		if(calendar.isMonthlyMode()){
				    java.util.Calendar tempCal = java.util.Calendar.getInstance();
				    tempCal.setTime(calendar.getStartDate());
				    tempCal.add(java.util.Calendar.MONTH, -1);
				    calendar.setStartDate(tempCal.getTime());
				    tempCal.add(java.util.Calendar.DATE, tempCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
				    calendar.setEndDate(tempCal.getTime());
        		}
        		else{
        			java.util.Calendar tempCal = java.util.Calendar.getInstance();
	        		tempCal.setTime(calendar.getStartDate());
	        		tempCal.add(java.util.Calendar.DAY_OF_YEAR, -7);
					calendar.setStartDate(tempCal.getTime());
					tempCal.add(java.util.Calendar.DATE, 6); 
	        		calendar.setEndDate(tempCal.getTime());
        		}
    		}
		});
		
		nextButton.addClickListener(new Button.ClickListener() {
    		public void buttonClick(ClickEvent event) {
        		//Notification.show("you pressed next");
        		//above for testing. replace setStartDate with currdate plus one month
        		if(calendar.isMonthlyMode()){
				    java.util.Calendar tempCal = java.util.Calendar.getInstance();
				    tempCal.setTime(calendar.getStartDate());
				    tempCal.add(java.util.Calendar.MONTH, 1);
				    calendar.setStartDate(tempCal.getTime());
				    tempCal.add(java.util.Calendar.DATE, tempCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
				    calendar.setEndDate(tempCal.getTime());
        		}
        		else{
        			java.util.Calendar tempCal = java.util.Calendar.getInstance();
	        		tempCal.setTime(calendar.getStartDate());
	        		tempCal.add(java.util.Calendar.DAY_OF_YEAR, 7);
					calendar.setStartDate(tempCal.getTime());
					tempCal.add(java.util.Calendar.DATE, 6); 
	        		calendar.setEndDate(tempCal.getTime());
        		}
  
    		}
		});
      
	}  
	    
    void editPayment(Payment trans) {
        if (trans != null) {
            openEditor(trans);
        } else {
            closeEditor();
        }
    }

    void addPayment() {
        openEditor(new Payment());
    }

    private void openEditor(Payment trans) {
        paymentEditor.setEntity(trans);
        // display next to table on desktop class screens
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            mainContent.addComponent(paymentEditor);
            paymentEditor.focusFirst();
        } else {
            // Replace this view with the editor in smaller devices
            AppUI.get().getContentLayout().
                    replaceComponent(this, paymentEditor);
        }
    }

    private void closeEditor() {
        // As we display the editor differently in different devices,
        // close properly in each modes
        if (paymentEditor.getParent() == mainContent) {
            mainContent.removeComponent(paymentEditor);
        } else {
            AppUI.get().getContentLayout().
                    replaceComponent(paymentEditor, this);
        }
    }
	    
	
	    /* These methods gets called by the CDI event system, which is also
	     * available for Vaadin UIs when using Vaadin CDI add-on. In this
	     * example events are arised from TransactionForm. The CDI event system
	     * is a great mechanism to decouple components.
	     */
	    void savePayment(@Observes @PaymentEvent(Type.SAVE) Payment trans) {
	        //listTransactions();
	        closeEditor();
	    }
	
	    void resetPayment(@Observes @PaymentEvent(Type.REFRESH) Payment trans) {
	        //listTransactions();
	        closeEditor();
	    }
	
	    void deletePayment(@Observes @PaymentEvent(Type.DELETE) Payment trans) {
	        closeEditor();
	        //listTransactions();
	    }
	
	    @Override
	    public void enter(ViewChangeListener.ViewChangeEvent event) {
	
	    }
}