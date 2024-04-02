package com.digitalsln.stanserhorn.ui.preference

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.tools.Logger

class UnlockDialogFragment: DialogFragment() {

    interface UnlockDialogListener {
        fun onUnlockDialogSuccessful()
        fun onUnlockDialogUnsuccessful()
        fun onUnlockDialogWrongPassword()
        fun showPasswordDialog()
    }

    private var mUnlockDialogListener: UnlockDialogListener? = null
    private var mPasswordTextView: TextView? = null

    fun registerUnlockDialogListener(unlockDialogListener: UnlockDialogListener) {
        mUnlockDialogListener = unlockDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Logger.d("In UnlockDialogFragment.onCreateDialog: Creating UnlockDialogFragment.")
        if (mUnlockDialogListener == null) {
            throw ClassCastException("mPreferenceFragment must be set before showing the dialog.")
        }
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.preferences_unlocker_dialog_title)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_preferences_unlocker, null)
        builder.setView(view)

        mPasswordTextView = view.findViewById(R.id.unlockerDialogPassword)

        // Create the AlertDialog object and return it
        return builder
            .setPositiveButton(R.string.preferences_unlocker_dialog_ok) { dialog, _ ->
                val password = mPasswordTextView?.text.toString()
                if (password == resources.getString(R.string.preferences_unlocker_dialog_password)) {
                    Logger.i("In UnlockDialogFragment.onClick: Locked preferences unlocked successfully.")
                    mUnlockDialogListener?.onUnlockDialogSuccessful()
                } else {
                    Logger.i("In UnlockDialogFragment.onClick: Locked preferences not unlocked due to wrong password.")
                    mUnlockDialogListener?.onUnlockDialogWrongPassword()
                }
            }
            .setNegativeButton(R.string.preferences_unlocker_dialog_cancel) { dialog, _ ->
                Logger.i("In UnlockDialogFragment.onClick: Locked preferences not unlocked: the dialog was canceled.")
                dialog.cancel()
            }.create()
    }

    class WrongPasswordDialogFragment: DialogFragment() {

        private var unlockDialogListener: UnlockDialogListener? = null

        fun registerUnlockDialogListener(unlockDialogListener: UnlockDialogListener) {
            this.unlockDialogListener = unlockDialogListener
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(context)
                .setMessage(R.string.preferences_unlocker_dialog_wrong_password_text)
                .setPositiveButton(R.string.preferences_unlocker_dialog_ok) { dialog, id ->
                    if (unlockDialogListener == null) {
                        throw java.lang.ClassCastException("mPreferenceFragment must be set before showing the dialog.")
                    }
                    unlockDialogListener?.showPasswordDialog()
                }
                .create()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mUnlockDialogListener?.onUnlockDialogUnsuccessful()
    }
}