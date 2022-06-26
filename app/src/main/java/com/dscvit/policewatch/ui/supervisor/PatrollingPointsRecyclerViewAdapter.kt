package com.dscvit.policewatch.ui.supervisor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.policewatch.databinding.PatrollingPointItemBinding
import com.dscvit.policewatch.models.PatrollingPoint

class PatrollingPointsRecyclerViewAdapter(
    private var patrollingPoints: MutableList<PatrollingPoint>,
    val onClick: (PatrollingPoint) -> Unit
) :
    RecyclerView.Adapter<PatrollingPointsRecyclerViewAdapter.ViewHolder>() {

    fun updatePatrollingPoints(newPatrollingPoints: List<PatrollingPoint>) {
        patrollingPoints = newPatrollingPoints as MutableList<PatrollingPoint>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: PatrollingPointItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(patrollingPoint: PatrollingPoint) {
            binding.patrollingPointName.text = patrollingPoint.name
            binding.patrollingPointCard.setOnClickListener { onClick(patrollingPoint) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PatrollingPointItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(patrollingPoints[position])
    }

    override fun getItemCount() = patrollingPoints.size
}