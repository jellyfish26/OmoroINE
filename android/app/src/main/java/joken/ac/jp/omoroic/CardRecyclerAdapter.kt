package joken.ac.jp.omoroic

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotterknife.bindView
import org.w3c.dom.Text

/**
 * list adapter class
 */
class CardRecyclerAdapter(val context: Context, val itemList: MutableList<StationData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val containerView: View = inflater.inflate(R.layout.layout_recycler, parent, false)
        return StationDataViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = itemList[position]
        holder as StationDataViewHolder
        /**set data **/
        holder.stationNameView.text = item.stationName
        holder.lineNameView.text = item.lineName
        holder.companyCodeView.text = item.company
        holder.miscView.text = item.misc

        //TODO get font
//        val typeFace : Typeface = Typeface.createFromAsset(context.assets, "font_name")
//        holder.stationNameView.typeface = typeFace
//        holder.lineNameView.typeface = typeFace
//        holder.companyCodeView.typeface = typeFace
//        holder.miscView.typeface = typeFace
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    companion object {

        private class StationDataViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            val stationNameView: TextView by bindView(R.id.station_name)
            val lineNameView: TextView by bindView(R.id.line_name)
            val miscView: TextView by bindView(R.id.text_misc)
            val companyCodeView: TextView by bindView(R.id.company_code)
        }

    }
}