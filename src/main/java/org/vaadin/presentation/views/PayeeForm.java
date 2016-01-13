package org.vaadin.presentation.views;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.backend.PayeeService;
import org.vaadin.backend.domain.Payment;
import org.vaadin.backend.domain.Payee;
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
 * A UI component built to modify Payee entities. The used superclass
 * provides binding to the entity object and e.g. Save/Cancel buttons by
 * default. In larger apps, you'll most likely have your own customized super
 * class for your forms.
 * <p>
 * Note, that the advanced bean binding technology in Vaadin is able to take
 * advantage also from Bean Validation annotations that are used also by e.g.
 * JPA implementation. Check out annotations in Payee objects email field and
 * how they automatically reflect to the configuration of related fields in UI.
 * </p>
 */
public class PayeeForm extends AbstractForm<Payee> {

    @Inject
    PayeeService service;
    
    String authname = "unknown";

	TextField name = new MTextField("Payee Name").withFullWidth();
    TextField account = new MTextField("Account").withFullWidth();

    @Override
    protected Component createContent() {

        setStyleName(ValoTheme.LAYOUT_CARD);

        return new MVerticalLayout(
                new Header("Payee Details").setHeaderLevel(3),
                new MFormLayout(
                        name,
                        account
                ).withFullWidth(),
                getToolbar()
        ).withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);
        
        //set authorized user creds
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
        
        setSavedHandler(new SavedHandler<Payee>() {

            @Override
            public void onSave(Payee entity) {
                try {
                    // make EJB call to save the entity
                    entity.setUsername(authname);
                    service.saveOrPersist(entity);
                    // fire save event to let other UI components know about
                    // the change
                    saveEvent.fire(entity);
                } catch (EJBException e) {
                    /*
                     * The Payee object uses optimitic locking with the 
                     * version field. Notify user the editing didn't succeed.
                     */
                    Notification.show("The Payee was concurrently edited "
                           + "by someone else. Your changes were discarded.",
                            Notification.Type.ERROR_MESSAGE);
                    refrehsEvent.fire(entity);
                }
            }
        });
        setResetHandler(new ResetHandler<Payee>() {

            @Override
            public void onReset(Payee entity) {
                refrehsEvent.fire(entity);
            }
        });
        setDeleteHandler(new DeleteHandler<Payee>() {
            @Override
            public void onDelete(Payee entity) {
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
    @PayeeEvent(PayeeEvent.Type.SAVE)
    javax.enterprise.event.Event<Payee> saveEvent;

    @Inject
    @PayeeEvent(PayeeEvent.Type.REFRESH)
    javax.enterprise.event.Event<Payee> refrehsEvent;

    @Inject
    @PayeeEvent(PayeeEvent.Type.DELETE)
    javax.enterprise.event.Event<Payee> deleteEvent;
}
