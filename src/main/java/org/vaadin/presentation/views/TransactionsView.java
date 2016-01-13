package org.vaadin.presentation.views;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.backend.TransactionService;
import org.vaadin.backend.CheckService;
import org.vaadin.backend.PaymentService;
import org.vaadin.backend.domain.Transaction;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.presentation.AppUI;
import org.vaadin.presentation.ScreenSize;
import org.vaadin.presentation.views.TransactionEvent.Type;
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
 * A view that lists Transactions in a Table and lets user to choose one for
 * editing. There is also RIA features like on the fly filtering.
 */
@CDIView("transactions")
@ViewMenuItem(icon = FontAwesome.LOCK)
public class TransactionsView extends MVerticalLayout implements View {

    @Inject
    private TransactionService service;
    
    @Inject
    PaymentService pservice;
    
    @Inject
    CheckService cservice;

    @Inject
    TransactionForm transactionEditor;

	String authname = "unknown";

    // Introduce and configure some UI components used on this view
    MTable<Transaction> transactionTable = new MTable(Transaction.class).withFullWidth().
            withFullHeight();

    MHorizontalLayout mainContent = new MHorizontalLayout(transactionTable).
            withFullWidth().withMargin(false);

    //TextField filter = new TextField();

    Header header = new Header("Transactions").setHeaderLevel(2);

    @PostConstruct
    public void init() {

        /*
         * Add value change listener to table that opens the selected transaction into
         * an editor.
         
        transactionTable.addMValueChangeListener(new MValueChangeListener<Transaction>() {

            @Override
            public void valueChange(MValueChangeEvent<Transaction> event) {
                editTransaction(event.getValue());
            }
        });
		*/


        /*
         * Configure the filter input and hook to text change events to
         * repopulate the table based on given filter. Text change
         * events are sent to the server when e.g. user holds a tiny pause
         * while typing or hits enter.
         * 
        filter.setInputPrompt("Filter transactions...");
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
                listTransactions(textChangeEvent.getText());
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

        listTransactions();
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
                    new MHorizontalLayout(header)
                    .expand(header)
                    .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );

            //filter.setSizeUndefined();
        } else {
            addComponents(
                    header,
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
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            transactionTable.setVisibleColumns("date", "amount", "description");
            transactionTable.setColumnHeaders("Date", "Amount", "Description");
        } else {
            if (ScreenSize.getScreenSize() == ScreenSize.MEDIUM) {
                transactionTable.setVisibleColumns("date", "amount", "description");
                transactionTable.setColumnHeaders("Date", "Amount", "description");
            } else {
                transactionTable.setVisibleColumns("amount", "description");
                transactionTable.setColumnHeaders("Amount", "description");
            }
        }
    }

    /* ******* */
    // Controller methods.
    //
    // In a big project, consider using separate controller/presenter
    // for improved testability. MVP is a popular pattern for large
    // Vaadin applications.
    private void listTransactions() {
        // Here we just fetch data straight from the EJB.
        //
        // If you expect a huge amount of data, do proper paging,
        // or use lazy loading Vaadin Container like LazyQueryContainer
        // See: https://vaadin.com/directory#addon/lazy-query-container:vaadin
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
        service.updateTransactions(authname);
        transactionTable.setBeans(new ArrayList<>(service.findByName(authname)));
    }

    private void listTransactions(String filterString) {
        transactionTable.setBeans(new ArrayList<>(service.findByName(filterString)));
    }

    void editTransaction(Transaction trans) {
        if (trans != null) {
            openEditor(trans);
        } else {
            closeEditor();
        }
    }

    void addTransaction() {
        openEditor(new Transaction());
    }

    private void openEditor(Transaction trans) {
        transactionEditor.setEntity(trans);
        // display next to table on desktop class screens
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            mainContent.addComponent(transactionEditor);
            transactionEditor.focusFirst();
        } else {
            // Replace this view with the editor in smaller devices
            AppUI.get().getContentLayout().
                    replaceComponent(this, transactionEditor);
        }
    }

    private void closeEditor() {
        // As we display the editor differently in different devices,
        // close properly in each modes
        if (transactionEditor.getParent() == mainContent) {
            mainContent.removeComponent(transactionEditor);
        } else {
            AppUI.get().getContentLayout().
                    replaceComponent(transactionEditor, this);
        }
    }

    /* These methods gets called by the CDI event system, which is also
     * available for Vaadin UIs when using Vaadin CDI add-on. In this
     * example events are arised from TransactionForm. The CDI event system
     * is a great mechanism to decouple components.
     */
    void saveTransaction(@Observes @TransactionEvent(Type.SAVE) Transaction trans) {
        listTransactions();
        closeEditor();
    }

    void resetTransaction(@Observes @TransactionEvent(Type.REFRESH) Transaction trans) {
        listTransactions();
        closeEditor();
    }

    void deleteTransaction(@Observes @TransactionEvent(Type.DELETE) Transaction trans) {
        closeEditor();
        listTransactions();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
