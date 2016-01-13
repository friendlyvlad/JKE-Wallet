package org.vaadin.presentation.views;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.backend.PayeeService;
import org.vaadin.backend.TransactionService;
import org.vaadin.backend.domain.Payee;
import org.vaadin.backend.domain.Payment;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.presentation.AppUI;
import org.vaadin.presentation.ScreenSize;
import org.vaadin.presentation.views.PayeeEvent.Type;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.fields.MValueChangeListener;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;

/**
 * A view that lists Payees in a Table and lets user to choose one for
 * editing. There is also RIA features like on the fly filtering.
 */
@CDIView("payees")
@ViewMenuItem(icon = FontAwesome.BUILDING)
public class PayeesView extends MVerticalLayout implements View {

    @Inject
    private PayeeService service;
    
    @Inject
    private TransactionService tservice;

    @Inject
    PayeeForm payeeEditor;
    
    String authname = "unknown";

    // Introduce and configure some UI components used on this view
    MTable<Payee> payeeTable = new MTable(Payee.class).withFullWidth().
            withFullHeight();

    MHorizontalLayout mainContent = new MHorizontalLayout(payeeTable).
            withFullWidth().withMargin(false);

    //TextField filter = new TextField();

    Header header = new Header("Payees").setHeaderLevel(2);

    Button addButton = new MButton(FontAwesome.EDIT,
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    addPayee();
                }
            });

    @PostConstruct
    public void init() {

        /*
         * Add value change listener to table that opens the selected Payee into
         * an editor.
         */
        payeeTable.addMValueChangeListener(new MValueChangeListener<Payee>() {

            @Override
            public void valueChange(MValueChangeEvent<Payee> event) {
                editPayee(event.getValue());
            }
        });

        /*
         * Configure the filter input and hook to text change events to
         * repopulate the table based on given filter. Text change
         * events are sent to the server when e.g. user holds a tiny pause
         * while typing or hits enter.
         * 
        filter.setInputPrompt("Filter Payees...");
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
                listPayees(textChangeEvent.getText());
            }
        });
		*/

        /* "Responsive Web Design" can be done with plain Java as well. Here we
         * e.g. do selective layouting and configure visible columns in
         * table based on available width */
        layout();
        adjustTableColumns();
        /* If you wish the UI to adapt on window resize/page orientation
         * change, hook to BrowserWindowResizeEvent */
        UI.getCurrent().setResizeLazy(true);
        Page.getCurrent().addBrowserWindowResizeListener(
                new Page.BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            Page.BrowserWindowResizeEvent browserWindowResizeEvent) {
                                adjustTableColumns();
                                layout();
                            }
                });
        listPayees();
    }

    /**
     * Do the application layout that is optimized for the screen size.
     * <p>
     * Like in Java world in general, Vaadin developers can modularize their
     * helpers easily and re-use existing code. E.g. In this method we are using
     * extended versions of Vaadins basic layout that has "fluent API" and this
     * way we get bit more readable code. Check out vaadin.com/directory for a
     * huge amount of helper libraries and custom components. They might be
     * valuable for your productivity.
     * </p>
     */
    private void layout() {
        removeAllComponents();
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            addComponents(
                    new MHorizontalLayout(header, addButton)
                    .expand(header)
                    .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );

           // filter.setSizeUndefined();
        } else {
            addComponents(
                    header,
                    new MHorizontalLayout(addButton)
                    //.expand(filter)
                    .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );
        }
        setMargin(new MarginInfo(false, true, true, true));
        expand(mainContent);
    }

    /**
     * Similarly to layouts, we can adapt component configurations based on the
     * client details. Here we configure visible columns so that a sane amount
     * of data is displayed for various devices.
     */
    private void adjustTableColumns() {
        payeeTable.setVisibleColumns("name", "account");
        payeeTable.setColumnHeaders("Name", "Account");
    }

    /* ******* */
    // Controller methods.
    //
    // In a big project, consider using separate controller/presenter
    // for improved testability. MVP is a popular pattern for large
    // Vaadin applications.
    private void listPayees() {
       	//filtered by authorized user from Bluemix SSO
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
       	tservice.updateTransactions(authname);
       	payeeTable.setBeans(new ArrayList<>(service.findByName(authname)));
    }

    private void listPayees(String filterString) {
        payeeTable.setBeans(new ArrayList<>(service.findByName(filterString)));
    }

    void editPayee(Payee trans) {
        if (trans != null) {
            openEditor(trans);
        } else {
            closeEditor();
        }
    }

    void addPayee() {
        openEditor(new Payee());
    }

    private void openEditor(Payee trans) {
        payeeEditor.setEntity(trans);
        // display next to table on desktop class screens
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            mainContent.addComponent(payeeEditor);
            payeeEditor.focusFirst();
        } else {
            // Replace this view with the editor in smaller devices
            AppUI.get().getContentLayout().
                    replaceComponent(this, payeeEditor);
        }
    }

    private void closeEditor() {
        // As we display the editor differently in different devices,
        // close properly in each modes
        if (payeeEditor.getParent() == mainContent) {
            mainContent.removeComponent(payeeEditor);
        } else {
            AppUI.get().getContentLayout().
                    replaceComponent(payeeEditor, this);
        }
    }

    /* These methods gets called by the CDI event system, which is also
     * available for Vaadin UIs when using Vaadin CDI add-on. In this
     * example events are arised from PayeeForm. The CDI event system
     * is a great mechanism to decouple components.
     */
    void savePayee(@Observes @PayeeEvent(Type.SAVE) Payee trans) {
        listPayees();
        closeEditor();
    }

    void resetPayee(@Observes @PayeeEvent(Type.REFRESH) Payee trans) {
        listPayees();
        closeEditor();
    }

    void deletePayee(@Observes @PayeeEvent(Type.DELETE) Payee trans) {
        closeEditor();
        listPayees();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
