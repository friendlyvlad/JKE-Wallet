package org.vaadin.presentation.views;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.backend.TransactionService;
import org.vaadin.backend.domain.Transaction;
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
 * A UI component built to modify Transaction entities. The used superclass
 * provides binding to the entity object and e.g. Save/Cancel buttons by
 * default. In larger apps, you'll most likely have your own customized super
 * class for your forms.
 * <p>
 * Note, that the advanced bean binding technology in Vaadin is able to take
 * advantage also from Bean Validation annotations that are used also by e.g.
 * JPA implementation. Check out annotations in Transaction objects email field and
 * how they automatically reflect to the configuration of related fields in UI.
 * </p>
 */
public class TransactionForm extends AbstractForm<Transaction> {

    @Inject
    TransactionService service;

    // Prepare some basic field components that our bound to entity property
    // by naming convetion, you can also use PropertyId annotation
    DateField transDate = new DateField("Transaction Date");
    TextField description = new MTextField("Description").withFullWidth();
    // Select to another entity, options are populated in the init method
    OptionGroup accounttype = new OptionGroup("Account Type");
    TextField amount = new MTextField("Amount").withFullWidth();

    @Override
    protected Component createContent() {

        setStyleName(ValoTheme.LAYOUT_CARD);

        return new MVerticalLayout(
                new Header("Transaction Details").setHeaderLevel(3),
                new MFormLayout(
                        amount,
                        transDate,
                        description
                ).withFullWidth(),
                getToolbar()
        ).withStyleName(ValoTheme.LAYOUT_CARD);
    }

}
