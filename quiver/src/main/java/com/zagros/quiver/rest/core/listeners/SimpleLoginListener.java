package com.zagros.quiver.rest.core.listeners;

import android.content.DialogInterface;

/**
 * Listener interface to be implemented by the class that is interested in the
 * results
 *
 * @author Mostafa.Hadian
 */
public interface SimpleLoginListener {

    /**
     * Callback for when the user is signed in successfully
     *
     * @param loginDialog
     */
    public abstract void onSignedIn(DialogInterface loginDialog);

    /**
     * Callback for when an error accrued and sign in could not be fulfilled.
     *
     * @param loginDialog
     */
    public abstract void onFailure(DialogInterface loginDialog);

    /**
     * Callback for when the user wants to signout and sign in with a different
     * username
     *
     * @param loginDialog
     */
    public abstract void onSignOut(DialogInterface loginDialog);

    /**
     * Callback for when the user cancels the dialog
     *
     * @param loginDialog
     */
    public abstract void onCancel(DialogInterface loginDialog);

}
