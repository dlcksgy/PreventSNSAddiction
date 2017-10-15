package kr.ac.kau.sw.a2016125063.accountkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val accounts = arrayOf(CheckingAccount("123-456-7890",interest = 0.01),
            CheckingAccount("123-123-1234",1000,0.02),
            CheckingAccount("123-456-7890",1000000))

        accounts[0].deposit(30000)
        val list : ListView = findViewById(R.id.account_list)


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, accounts)

        list.adapter = adapter
        list.setOnItemClickListener({parent, itemView, position, id ->
            accounts[position].aMonthHasPassed()
            adapter.notifyDataSetChanged()
        })

    }
}
