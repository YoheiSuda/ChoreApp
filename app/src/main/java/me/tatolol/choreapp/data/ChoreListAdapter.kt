package me.tatolol.choreapp.data

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatolol.choreapp.R

class ChoreListAdapter(private val list: ArrayList<Chores>, private val context: Context): RecyclerView.Adapter<ChoreListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ChoreListAdapter.ViewHolder, position: Int) {
        holder.bindViews(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoreListAdapter.ViewHolder {
        //create our view from xml file
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_row, parent, false)

        return ViewHolder(view, context, list)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View, context: Context, list: ArrayList<Chores>): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mContext = context
        var mList = list
        var choreName = itemView.findViewById<TextView>(R.id.listChoreName)
        var assignedBy = itemView.findViewById<TextView>(R.id.listAssignedBy)
        var assignedDate = itemView.findViewById<TextView>(R.id.listDate)
        var assignedTo = itemView.findViewById<TextView>(R.id.listAssignedTo)

        var deleteButton = itemView.findViewById<ImageButton>(R.id.listDeleteButton)
        var editButton = itemView.findViewById<ImageButton>(R.id.listEditButton)
        var database = ChoresDatabase.getInstance(mContext)
        val dao = database.choresDao()



        fun bindViews(chores: Chores) {
            choreName.text = "Chore: "+ chores.choreName
            assignedBy.text = "AssignedBy: " + chores.choreAssignedBy
            assignedDate.text = "AssignedTo: " + chores.showHumanDate(chores.choreAssignedTime!!)
            assignedTo.text = "Date: " + chores.choreAssignedTo

            deleteButton.setOnClickListener(this)
            editButton.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            var mPosition: Int = adapterPosition
            var chore = mList[mPosition]

            when(v!!.id) {
                deleteButton.id -> {
                    deleteChore(dao, chore.id)
                    mList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                editButton.id -> {
                    editChore(dao, chore)
                }
            }
        }

        fun deleteChore(dao:ChoresDao, id:Int) {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    var readChores = dao.select(id)
                    if (readChores.isNotEmpty()) {
                        Log.d("readChore", readChores.get(0).toString())
                        dao.delete(readChores.get(0))
                        Log.d("dbdelete", "success")
                    }
                }
            }
        }

        fun updateChore(dao:ChoresDao, chores: Chores) {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    var readChores = dao.select(chores.id)
                    if (readChores.isNotEmpty()) {
                        var updatedChore = readChores.get(0).copy(
                            choreName = chores.choreName,
                            choreAssignedBy = chores.choreAssignedBy,
                            choreAssignedTime = System.currentTimeMillis(),
                            choreAssignedTo = chores.choreAssignedTo
                        )
                        dao.update(updatedChore)
                        Log.d("dbupdate", "success")
                    }
                }
            }
        }

        fun editChore(dao: ChoresDao, chore:Chores) {

            var dialogBuilder: AlertDialog.Builder?
            var dialog: AlertDialog?

            var view = LayoutInflater.from(mContext).inflate(R.layout.popup, null)
            var editChoreName = view.findViewById<EditText>(R.id.popEnterChore)
            var editAssignedBy = view.findViewById<EditText>(R.id.popAssignedBy)
            var editAssignedTo = view.findViewById<EditText>(R.id.popAssignedTo)
            var save = view.findViewById<Button>(R.id.popSaveChore)
            dialogBuilder = AlertDialog.Builder(mContext).setView(view)
            dialog = dialogBuilder!!.create()
            dialog?.show()

            save.setOnClickListener {
                if (!TextUtils.isEmpty(editChoreName.text.toString().trim()) &&
                    !TextUtils.isEmpty(editAssignedBy.text.toString().trim()) &&
                    !TextUtils.isEmpty(editAssignedTo.text.toString().trim())) {

                    var newChore = Chores(chore.id, editChoreName.text.toString(), editAssignedBy.text.toString(), editAssignedTo.text.toString(), chore.choreAssignedTime)
                    updateChore(dao, newChore)

                    choreName.text = "Chore: " + editChoreName.text
                    assignedBy.text = "AssignedBy: " + editAssignedBy.text
                    assignedTo.text = "AssignedTo: " + editAssignedTo.text

                    dialog!!.dismiss()

                } else {
                    dialog!!.dismiss()
                    Toast.makeText(mContext, "Please Enter Chore", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}