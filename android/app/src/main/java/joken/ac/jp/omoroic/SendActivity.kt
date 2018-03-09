package joken.ac.jp.omoroic

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import kotterknife.bindView
import joken.ac.jp.omoroic.R.id.recyclerView
import android.support.v7.widget.LinearLayoutManager



class SendActivity : AppCompatActivity() {

    val recyclerview by bindView<RecyclerView>(R.id.recyclerView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_send)
        var list : MutableList<StationData> = mutableListOf()
        for(i in 0..3){list.add(this.intent.getSerializableExtra("stationData") as StationData)}
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = CardRecyclerAdapter(this, list)
    }
}
