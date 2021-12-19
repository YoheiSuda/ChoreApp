package me.tatolol.choreapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import me.tatolol.choreapp.ui.ProgressDialog

class MainActivity : AppCompatActivity() {
    private val progressDialog = ProgressDialog.newInstance("セーブ中")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var enterChore: EditText = findViewById(R.id.enterChoreId)
        var assignedBy: EditText = findViewById(R.id.assignedById)
        var assignedTo: EditText = findViewById(R.id.assignedToId)
        var saveChore: Button = findViewById(R.id.saveChore)

        val database = ChoresDatabase.getInstance(applicationContext)
        val dao = database.choresDao()

        checkDB(dao, this)

        saveChore.setOnClickListener {
            progressDialog.show(supportFragmentManager,"TAG")

            if (!TextUtils.isEmpty(enterChore.text.toString().trim()) &&
                    !TextUtils.isEmpty(assignedBy.text.toString().trim()) &&
                    !TextUtils.isEmpty(assignedTo.text.toString().trim())) {
                // save to database
                var chore = Chores(0, "" + enterChore.text.toString(), "" +assignedBy.text.toString(), "" +assignedTo.text.toString(), System.currentTimeMillis())

                saveToDB(dao, chore, this)
                progressDialog.dismiss()
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, "Please Enter Chore", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createChore(dao: ChoresDao, chores: Chores, context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                dao.insert(chores)
                Log.d("dbinsert", "success")
                startActivity(Intent(context, ChoreListActivity::class.java))
            }
        }
    }

    fun checkDB(dao: ChoresDao, context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                var choresAll = dao.selectAll()
                var count = choresAll.size
                Log.d("count", count.toString())
                if (count > 0) {
                    startActivity(Intent(context, ChoreListActivity::class.java))
                }
            }
        }
    }

    fun saveToDB(dao: ChoresDao, chores: Chores, context: Context) {
        createChore(dao, chores, context)
    }

}