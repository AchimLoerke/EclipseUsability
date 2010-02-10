package com.bredexsw.examples.base;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;


public class MessagePopupAction extends Action {

    private final IWorkbenchWindow window;

    MessagePopupAction(String text, IWorkbenchWindow window) {
        super(text);
        this.window = window;
        // The id is used to refer to the action in a menu or toolbar
        setId(ICommandIds.CMD_OPEN_MESSAGE);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_OPEN_MESSAGE);
        setImageDescriptor(com.bredexsw.examples.base.Activator.getImageDescriptor("/icons/sample3.gif"));
    }

    public void run() {
    	Dialog msgDialog = new NewMessageDialog(window.getShell());
    	msgDialog.open();
    	msgDialog.getReturnCode();
    }
}