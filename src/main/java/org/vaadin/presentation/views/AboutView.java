
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import org.vaadin.backend.PaymentService;
import org.vaadin.backend.CheckService;
import org.vaadin.backend.PayeeService;
import org.vaadin.backend.AccountService;

import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.cdiviewmenu.ViewMenuUI;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.label.RichText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;

@CDIView("account")
@ViewMenuItem(icon = FontAwesome.INFO, order = 7)
public class AboutView extends MVerticalLayout implements View {

    @Inject
    AccountService accountService;
    
    @Inject
    PaymentService paymentService;
    
    @Inject
    CheckService checkService;
    
    @Inject 
    PayeeService payeeService;
    
    String authname = "unknown";
    
    @PostConstruct
    void init() {
        add(new RichText().withMarkDownResource("/about.md"));

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

		Button button = new Button("Add demo data", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                accountService.addDemoData(authname);
                payeeService.addDemoData(authname);
               	checkService.addDemoData(authname);
               	paymentService.addDemoData(authname);
            }
        });
        button.setStyleName(ValoTheme.BUTTON_LARGE);
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        add(button);

        setMargin(new MarginInfo(false, true, true, true));
        setStyleName(ValoTheme.LAYOUT_CARD);
        
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }
}
