package me.tatolol.choreapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import me.tatolol.choreapp.R

class ProgressDialog : DialogFragment() {

    companion object {
        fun newInstance(message: String): ProgressDialog {
            val instance = ProgressDialog()
            val arguments = Bundle()
            arguments.putString("message", message)
            instance.arguments = arguments
            return instance
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mMessage = requireArguments().getString("message")

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_progress, null)
        val mMessageTextView = view.findViewById(R.id.progress_message) as TextView
        mMessageTextView.text = mMessage
        builder.setView(view)
        return builder.create()
    }
}