package me.tatolol.choreapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatolol.choreapp.R
import me.tatolol.choreapp.data.ChoreListAdapter
import me.tatolol.choreapp.data.Chores
import me.tatolol.choreapp.data.ChoresDao
import me.tatolol.choreapp.data.ChoresDatabase

class ChoreListActivity : AppCompatActivity() {
    private var adapter: ChoreListAdapter? = null
    private var choreList: ArrayList<Chores>? = null
    private var choreListItems: ArrayList<Chores>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var dialogBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null
    private var database: ChoresDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chore_list)
        choreList = ArrayList()
        choreListItems = ArrayList()

        layoutManager = LinearLayoutManager(this)
        adapter = ChoreListAdapter(choreListItems!!, this)

        //setup list
        var recyclerViewId = findViewById<RecyclerView>(R.id.recyclerViewId)
        recyclerViewId.layoutManager = layoutManager
        recyclerViewId.adapter = adapter

        // load our chores
        database = ChoresDatabase.getInstance(applicationContext)
        val dao = database!!.choresDao()

        readChores(dao)

    }

    fun readChores(dao: ChoresDao) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                choreList = dao.selectAll() as ArrayList<Chores>
                choreList!!.reverse()
                Log.d("dbreadAll", "success")
                for (c in choreList!!.iterator()) {
                    val chore = Chores(c.id, c.choreName, c.choreAssignedBy, c.choreAssignedTo, c.choreAssignedTime)

                    choreListItems!!.add(chore)
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    fun addChore(dao: ChoresDao, chores:Chores) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                dao.insert(chores)
                Log.d("dbinsert", "success")

                choreListItems!!.clear()
                choreList = dao.selectAll() as ArrayList<Chores>
                choreList!!.reverse()
                Log.d("dbreadAll", "success")
                for (c in choreList!!.iterator()) {
                    val chore = Chores(c.id, c.choreName, c.choreAssignedBy, c.choreAssignedTo, c.choreAssignedTime)

                    choreListItems!!.add(chore)
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_menu) {
            Log.d("Item Clicked", "Item Clicked")

            createPopupDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    fun createPopupDialog() {
        var view = layoutInflater.inflate(R.layout.popup, null)
        var choreName = view.findViewById<EditText>(R.id.popEnterChore)
        var assignedBy = view.findViewById<EditText>(R.id.popAssignedBy)
        var assignedTo = view.findViewById<EditText>(R.id.popAssignedTo)
        var save = view.findViewById<Button>(R.id.popSaveChore)
        dialogBuilder = AlertDialog.Builder(this).setView(view)
        dialog = dialogBuilder!!.create()
        dialog?.show()

        val dao = database!!.choresDao()

        save.setOnClickListener {
            if (!TextUtils.isEmpty(choreName.text.toString().trim()) &&
                !TextUtils.isEmpty(assignedBy.text.toString().trim()) &&
                !TextUtils.isEmpty(assignedTo.text.toString().trim())) {

                var chore = Chores(0, choreName.text.toString(), assignedBy.text.toString(), assignedTo.text.toString(), System.currentTimeMillis())
                addChore(dao, chore)

                dialog!!.dismiss()

            } else {
                dialog!!.dismiss()
                Toast.makeText(this, "Please Enter Chore", Toast.LENGTH_LONG).show()
            }
        }
    }
}