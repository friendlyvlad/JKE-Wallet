package org.vaadin.presentation.views;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.backend.AccountService;
import org.vaadin.backend.domain.Payment;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.domain.AccountType;
import org.vaadin.backend.domain.Gender;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;


/**
 * A UI component built to modify Account entities. The used superclass
 * provides binding to the entity object and e.g. Save/Cancel buttons by
 * default. In larger apps, you'll most likely have your own customized super
 * class for your forms.
 * <p>
 * Note, that the advanced bean binding technology in Vaadin is able to take
 * advantage also from Bean Validation annotations that are used also by e.g.
 * JPA implementation. Check out annotations in Account objects email field and
 * how they automatically reflect to the configuration of related fields in UI.
 * </p>
 */
public class AccountForm extends AbstractForm<Account> {

    @Inject
    AccountService service;
	
	TextField accountName = new MTextField("Account Name").withFullWidth();
	
	//for testing. obviously remove afterwards
	//TextField balance = new MTextField("Balance").withFullWidth();
	Label balance = new Label("Balance");
	
	OptionGroup accountType = new OptionGroup("Account Type");
	
	String authname = "unknown";
	
    @Override
    protected Component createContent() {

        setStyleName(ValoTheme.LAYOUT_CARD);

        return new MVerticalLayout(
                new Header("Account Details").setHeaderLevel(3),
                new MFormLayout(
                		accountName,
                        accountType,
                        balance
                ).withFullWidth(),
                getToolbar()
        ).withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);
        
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
        
        accountType.addItems((Object[]) AccountType.values());
        accountType.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        
        setSavedHandler(new SavedHandler<Account>() {

            @Override
            public void onSave(Account entity) {
                try {
                    // make EJB call to save the entity
                    entity.setUsername(authname);
                    service.saveOrPersist(entity);
                    // fire save event to let other UI components know about
                    // the change
                    saveEvent.fire(entity);
                } catch (EJBException e) {
                    /*
                     * The Account object uses optimitic locking with the 
                     * version field. Notify user the editing didn't succeed.
                     */
                    Notification.show("The Account was concurrently edited "
                           + "by someone else. Your changes were discarded.",
                            Notification.Type.ERROR_MESSAGE);
                    refrehsEvent.fire(entity);
                }
            }
        });
        setResetHandler(new ResetHandler<Account>() {

            @Override
            public void onReset(Account entity) {
                refrehsEvent.fire(entity);
            }
        });
        setDeleteHandler(new DeleteHandler<Account>() {
            @Override
            public void onDelete(Account entity) {
                service.deleteEntity(getEntity());
                deleteEvent.fire(getEntity());
            }
        });
    }

	@Override
    protected void adjustResetButtonState() {
        // always enabled in this form
        getResetButton().setEnabled(true);
        getDeleteButton().setEnabled(getEntity() != null && getEntity().isPersisted());
    }


    /* "CDI interface" to notify decoupled components. Using traditional API to
     * other componets would probably be easier in small apps, but just
     * demonstrating here how all CDI stuff is available for Vaadin apps.
     */
    @Inject
    @AccountEvent(AccountEvent.Type.SAVE)
    javax.enterprise.event.Event<Account> saveEvent;

    @Inject
    @AccountEvent(AccountEvent.Type.REFRESH)
    javax.enterprise.event.Event<Account> refrehsEvent;

    @Inject
    @AccountEvent(AccountEvent.Type.DELETE)
    javax.enterprise.event.Event<Account> deleteEvent;
}
