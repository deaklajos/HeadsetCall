package vfv9w6.headsetcall

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_set_press_count.*
import kotlinx.android.synthetic.main.dialog_set_press_count.view.*
import vfv9w6.headsetcall.adapter.ContactRecyclerViewAdapter
import vfv9w6.headsetcall.data.Contact
import android.support.design.widget.Snackbar




class MainActivity : AppCompatActivity() {


    companion object {
        private const val SELECT_PHONE_NUMBER = 1
    }

    private lateinit var adapter: ContactRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //TODO maybe not here
        adapter = ContactRecyclerViewAdapter(this)
        rc_contact_list.adapter = adapter
        rc_contact_list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            if(adapter.availablePresses.size == 0)
            {
                Snackbar.make(main_layout, "Remove a contact before adding a new one!", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, SELECT_PHONE_NUMBER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            val contactUri = data!!.data!!
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val cursor = this.contentResolver.query(contactUri, projection,
                    null, null, null)

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val number = cursor.getString(numberIndex)
                val name = cursor.getString(nameIndex)

                // TODO put it outside and close cursor
                showNumberPickerDialog(name, number)
            }

            //TODO proper null check
            cursor.close()
        }

    }

    private fun showNumberPickerDialog(name: String, number: String)
    {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_set_press_count, null, false)
        val values = ArrayList<String>()
        adapter.availablePresses.forEach{
            values.add(it.toString())
        }
        view.np_press_count.minValue = 0
        view.np_press_count.maxValue = values.size - 1
        view.np_press_count.displayedValues = values.toTypedArray()

        view.btn_ok.setOnClickListener {
            val presses = adapter.availablePresses[view.np_press_count.value]
            val contact = Contact(name, number, presses)
            adapter.addItem(contact)
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
