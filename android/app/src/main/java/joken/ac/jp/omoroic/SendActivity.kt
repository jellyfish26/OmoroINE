package joken.ac.jp.omoroic

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import kotterknife.bindView

class SendActivity : AppCompatActivity() {

    val recyclerview by bindView<RecyclerView>(R.id.recyclerView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_send)
        recyclerview.adapter = CardRecyclerAdapter(this.applicationContext, this.intent.getSerializableExtra("stationData") as MutableList<StationData>)
    }
}
