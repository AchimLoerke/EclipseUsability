/**
 * 
 */
package com.bredexsw.examples.base;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

/**
 * @author achim
 *
 */
public class NewMessageDialog extends TitleAreaDialog implements PropertyChangeListener {

	private static final String PROP_DIALOG_STATE = "dialogState";
	private static final Status STATUS_OK = new Status(IStatus.OK, Activator.PLUGIN_ID, "Please enter your message.");
	private static final Status STATUS_RECIPIENT_MISSING = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Please enter a recipient.");
	private static final Status STATUS_SUBJECT_MISSING = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "You didn't enter a subject.");
	private static final Status STATUS_TEXT_MISSING = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "You didn't enter any text.");


	private static class Message {
		private PropertyChangeSupport m_changeSupport = new PropertyChangeSupport(this);
		public void addPropertyChangeListener(String propertyName,
				PropertyChangeListener listener) {
			m_changeSupport.addPropertyChangeListener(propertyName, listener);
		}
		@SuppressWarnings("unused")
		public void removePropertyChangeListener(String propertyName,
				PropertyChangeListener listener) {
			m_changeSupport.removePropertyChangeListener(propertyName, listener);
		}
		/**
		 * @return the m_recipients
		 */
		@SuppressWarnings("unused")
		public String getRecipients() {
			return m_recipients;
		}
		/**
		 * @param mRecipients the m_recipients to set
		 */
		@SuppressWarnings("unused")
		public void setRecipients(String recipients) {
			m_changeSupport.firePropertyChange("recipients", recipients, m_recipients = recipients);
		}
		/**
		 * @return the m_subjects
		 */
		@SuppressWarnings("unused")
		public String getSubject() {
			return m_subject;
		}
		/**
		 * @param mSubjects the m_subjects to set
		 */
		@SuppressWarnings("unused")
		public void setSubject(String subject) {
			m_changeSupport.firePropertyChange("subject", m_subject, m_subject = subject);
		}
		/**
		 * @return the m_text
		 */
		@SuppressWarnings("unused")
		public String getText() {
			return m_text;
		}
		/**
		 * @param mText the m_text to set
		 */
		@SuppressWarnings("unused")
		public void setText(String text) {
			m_changeSupport.firePropertyChange("text", m_text, m_text = text);
		}
		/**
		 * @return the dialogState
		 */
		public IStatus getDialogState() {
			return m_dialogState;
		}
		/**
		 * @param dialogState the dialogState to set
		 */
		@SuppressWarnings("unused")
		public void setDialogState(IStatus dialogState) {			
			m_changeSupport.firePropertyChange(PROP_DIALOG_STATE, m_dialogState, m_dialogState = dialogState);
		}
		private String m_recipients;
		private String m_subject;
		private String m_text;
		private IStatus m_dialogState = STATUS_OK;
	}
	
	private Text m_recipientsText;
	private Text m_subjectText;
	private Text m_messageText;
	
	private Message m_message;

	/**
	 * @see TitleAreaDialog#TitleAreaDialog(Shell)
	 */
	public NewMessageDialog(Shell parentShell) {
		super(parentShell);
		m_message = new Message();
		m_message.addPropertyChangeListener(PROP_DIALOG_STATE, this);
	}

	/**
	 * initialize properties
	 */
	private void init() {
		this.getShell().setText("Message Dialog");
		this.setTitle("Create a message");
		this.setMessage("Please enter your message.", IMessageProvider.NONE);
		
		this.setHelpAvailable(true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		init();
		
		Composite dlgArea = (Composite) super.createDialogArea(parent);
		
		Composite dataArea = new Composite(dlgArea, SWT.NONE);
		dataArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		dataArea.setLayout(layout);
		
		m_recipientsText = createTextWithLabel(dataArea, "Recipient(s):");
		m_subjectText = createTextWithLabel(dataArea, "Subject:");

		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		
		Label l = new Label(dataArea, SWT.NONE);
		l.setText("Message:");		

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.verticalSpan = 20;
		
		m_messageText = new Text(dataArea, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		m_messageText.setLayoutData(gd);

		bindData();
		setupHelp(parent.getShell());
		
		return dlgArea;
	}

	private void setupHelp(Control container) {
		IWorkbenchHelpSystem helpSystem = 
			Activator.getDefault().getWorkbench().getHelpSystem();
		
		helpSystem.setHelp(container, Activator.PLUGIN_ID + ".newMessageDialog");
		helpSystem.setHelp(m_recipientsText, Activator.PLUGIN_ID + ".newMessageDialog_recipients");
		helpSystem.setHelp(m_subjectText, Activator.PLUGIN_ID + ".newMessageDialog_subject");
		helpSystem.setHelp(m_messageText, Activator.PLUGIN_ID + ".newMessageDialog_text");
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		// TODO Auto-generated method stub
		super.create();
		handleDialogState(m_message.getDialogState());
	}

	private void bindData() {
		DataBindingContext dbc = new DataBindingContext();
		
		dbc.bindValue(SWTObservables.observeText(m_recipientsText, SWT.Modify), 
				BeansObservables.observeValue(m_message, "recipients"),
				new UpdateValueStrategy().setAfterConvertValidator(new IValidator() {
					
					@Override
					public IStatus validate(Object value) {
						String recipients = (String) value;
						if (recipients.isEmpty()) {
							return STATUS_RECIPIENT_MISSING;
						}
						return STATUS_OK;
					}
				}),
				null);
		dbc.bindValue(SWTObservables.observeText(m_subjectText, SWT.Modify), 
				BeansObservables.observeValue(m_message, "subject"),
				new UpdateValueStrategy().setAfterConvertValidator(new IValidator() {
					
					@Override
					public IStatus validate(Object value) {
						String subject = (String) value;
						if (subject.isEmpty()) {
							return STATUS_SUBJECT_MISSING;
						}
						return STATUS_OK;
					}
				}),
				null);
		dbc.bindValue(SWTObservables.observeText(m_messageText, SWT.Modify), 
				BeansObservables.observeValue(m_message, "text"),
				new UpdateValueStrategy().setAfterConvertValidator(new IValidator() {
					
					@Override
					public IStatus validate(Object value) {
						String text = (String) value;
						if (text.isEmpty()) {
							return STATUS_TEXT_MISSING;
						}
						return STATUS_OK;
					}
				}),
				null);
		
		dbc.bindValue(BeansObservables.observeValue(m_message,PROP_DIALOG_STATE),
				new AggregateValidationStatus(dbc.getBindings(),
						AggregateValidationStatus.MAX_SEVERITY), null, null);
	}

	private Text createTextWithLabel(Composite dataArea, String prompt) {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		
		Label l = new Label(dataArea, SWT.NONE);
		l.setText(prompt);		
		Text t = new Text(dataArea, SWT.BORDER);
		t.setLayoutData(gd);
		
		return t;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PROP_DIALOG_STATE)) {
			IStatus state = (IStatus) evt.getNewValue();
			handleDialogState(state);
		}
	}

	/**
	 * @param state
	 */
	private void handleDialogState(IStatus state) {
		int type = state.getSeverity();
		
		Button okButton = getButton(OK);
		if (okButton != null) {
			boolean enableOK;
			switch (type) {
			case IStatus.CANCEL:
			case IStatus.ERROR:
				enableOK = false;
				break;
			case IStatus.WARNING:
			case IStatus.INFO:
			case IStatus.OK:
				enableOK = true;
				break;
			default:
				enableOK = false;
				
			}
			okButton.setEnabled(enableOK);
		}
		
		if (type == IStatus.CANCEL || type == IStatus.ERROR) {
			setErrorMessage(state.getMessage());
		} else {
			setErrorMessage(null);
			int msgType;
			switch (type) {
			case IStatus.CANCEL:
			case IStatus.ERROR:
				msgType = IMessageProvider.ERROR;
				break;
			case IStatus.WARNING:
				msgType = IMessageProvider.WARNING;
				break;
			case IStatus.INFO:
				msgType = IMessageProvider.INFORMATION;
				break;
			case IStatus.OK:
			default:
				msgType = IMessageProvider.NONE;
				
			}

			setMessage(state.getMessage(), msgType);
		}
	}

}
