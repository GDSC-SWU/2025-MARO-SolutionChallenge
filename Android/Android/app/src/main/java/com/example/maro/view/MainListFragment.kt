package com.example.maro.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maro.R
import com.example.maro.data.db.AppDatabase
import com.example.maro.databinding.FragmentMainListBinding
import com.example.maro.model.ChildEntity
import com.example.maro.model.VaccineEntity
import com.example.maro.model.VaccineScheduleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MainListFragment : Fragment() {

    // Fragment view binding
    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!

    // Adapter
    private lateinit var vaccineAdapter: MainListAdapter

    // Vaccine list variables
    private lateinit var childList: List<ChildEntity>
    private var vaccineMap = mutableMapOf<Int, List<VaccineEntity>>()

    // Selected child index
    private var selectedChildIndex: Int = 0

    // Reflect changes from detail list screen
    private lateinit var detailLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize adapter
        vaccineAdapter = MainListAdapter(emptyList())
        binding.recyclerVaccines.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vaccineAdapter

            lifecycleScope.launch {
                loadChildAndVaccineData()
            }
            Log.d("초기인덱스", "updateChildInfo index=0")
        }

        // Load child and vaccine data
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(requireContext())
                val childDao = db.childDao()
                val vaccineDao = db.vaccineDao()

                // Fetch child list
                childList = childDao.getAllChildren()

                // Fetch and map vaccine data per child
                childList.forEach { child ->
                    val vaccines = vaccineDao.getVaccinesByChildId(child.id)
                    val today = LocalDate.now()

                    val scheduleList = vaccines.mapNotNull { vaccine ->
                        val (startDate, endDate) = parseDateRange(vaccine.dateRange) ?: return@mapNotNull null
                        val status = when {
                            today.isBefore(startDate) -> "Not yet"
                            today.isAfter(endDate) -> "Completed"
                            else -> "Coming up"
                        }
                        VaccineScheduleItem(vaccine.title, vaccine.dateRange, status)
                    }

                    vaccineMap[child.id] = vaccines
                }
            }

            withContext(Dispatchers.Main) {
                // Dynamically create child filter buttons
                childList.forEachIndexed { index, child ->
                    val button = TextView(requireContext()).apply {
                        text = child.name
                        setPadding(30, 20, 30, 20)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

                        setOnClickListener {
                            // Update screen with selected child
                            updateChildInfo(child = child, vaccineList = vaccineMap[child.id] ?: emptyList())

                            // Move indicator to selected button
                            moveIndicatorToButton(this)
                            // Reset all buttons to normal
                            for (i in 0 until binding.nameFilterLayout.childCount) {
                                val btn = binding.nameFilterLayout.getChildAt(i) as? TextView
                                btn?.setTypeface(null, Typeface.NORMAL)
                            }

                            // Bold the selected button
                            this.setTypeface(null, Typeface.BOLD)

                            // Save selected child index (used for "See All")
                            selectedChildIndex = index
                        }
                    }

                    // Add button to layout
                    binding.nameFilterLayout.addView(button)
                }

                // Auto select first child (click first button)
                if (childList.isNotEmpty()) {
                    binding.nameFilterLayout.getChildAt(0)?.performClick()
                }
            }
        }

        // Callback from detail screen
        detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Reload data when returning
                viewLifecycleOwner.lifecycleScope.launch {
                    loadChildAndVaccineData()
                }
            }
        }

        // Navigate to detail list by child
        binding.seeAllCard.setOnClickListener {
            val intent = Intent(requireContext(), DetailListActivity::class.java)
            intent.putExtra("child_id", childList[selectedChildIndex].id)
            detailLauncher.launch(intent)
        }
        binding.cardChildInfo.setOnClickListener {
            val intent = Intent(requireContext(), DetailListActivity::class.java)
            intent.putExtra("child_id", childList[selectedChildIndex].id)
            detailLauncher.launch(intent)
        }
    }

    // Update child info
    private fun updateChildInfo(child: ChildEntity, vaccineList: List<VaccineEntity>) {
        val context = requireContext()

        // Update UI

        // Display child name
        binding.tvChildName.text = child.name

        // Set gender text
        val genderResId = if (child.gender == "boy") R.string.child_gender_boy else R.string.child_gender_girl
        binding.tvChildGender.text = getString(genderResId) + ","

        // Set gender image
        val imageResId = if (child.gender == "boy") R.drawable.ic_boy else R.drawable.ic_girl
        binding.tvAsset.setImageResource(imageResId)

        // Format age in months or years
        binding.tvChildAge.text = if (child.months < 36) {
            context.getString(R.string.child_age_m, child.months)
        } else {
            context.getString(R.string.child_age_y, child.months / 12)
        }

        val fullList = vaccineList.mapNotNull { vaccine ->
            val (startDate, endDate) = parseDateRange(vaccine.dateRange) ?: return@mapNotNull null

            VaccineScheduleItem(
                title = vaccine.title,
                dateRange = vaccine.dateRange,
                status = vaccine.status
            )
        }

        // Filter only "Coming up" for main view
        val upcomingList = fullList.filter { it.status == "Coming up" }

        // Show empty UI if no upcoming items
        if (upcomingList.isEmpty()) {
            binding.emptyPlaceholder.visibility = View.VISIBLE
        } else {
            binding.emptyPlaceholder.visibility = View.GONE
        }

        // Adapter is initialized only once; just update here
        vaccineAdapter.updateList(upcomingList)

        // Set upcoming vaccine count text
        binding.tvUpcomingCount.text = getString(R.string.main_upcoming_vaccines, upcomingList.size)
    }

    // Load child & vaccine data
    private suspend fun loadChildAndVaccineData() {
        val db = AppDatabase.getInstance(requireContext())
        val childDao = db.childDao()
        val vaccineDao = db.vaccineDao()

        val children = childDao.getAllChildren()
        val map = mutableMapOf<Int, List<VaccineEntity>>()

        for (child in children) {
            val vaccines = vaccineDao.getVaccinesByChildId(child.id)
            Log.d("vaccine", "childId=${child.id}, name=${child.name}, 백신 개수=${vaccines.size}")
            vaccines.forEach {
                Log.d("vaccine", "→ ${it.title} | ${it.dateRange} | status=${it.status}")
            }

            map[child.id] = vaccines
        }

        // Maintain vaccineMap but update with fresh DB values
        childList = children
        vaccineMap = map

        val selectedChild = children.getOrNull(selectedChildIndex) ?: return
        val selectedVaccines = withContext(Dispatchers.IO) {
            db.vaccineDao().getVaccinesByChildId(selectedChild.id)
        }
        updateChildInfo(child = selectedChild, vaccineList = selectedVaccines)
    }

    // Move filter indicator animation
    fun moveIndicatorToButton(button: View) {
        val indicator = binding.selectedIndicator

        indicator.post {
            val location = IntArray(2)
            button.getLocationOnScreen(location)
            val buttonX = location[0]
            val buttonWidth = button.width

            val parentLocation = IntArray(2)
            binding.filterScroll.getLocationOnScreen(parentLocation)
            val scrollX = buttonX - parentLocation[0]

            val params = indicator.layoutParams as ViewGroup.MarginLayoutParams
            params.width = buttonWidth
            params.marginStart = scrollX
            indicator.layoutParams = params
        }
    }

    // Parse date range "yyyy-MM-dd ~ yyyy-MM-dd"
    private fun parseDateRange(dateRange: String): Pair<LocalDate, LocalDate>? {
        return try {
            val parts = dateRange.split("~").map { it.trim() }
            if (parts.size != 2) return null
            val startDate = LocalDate.parse(parts[0])
            val endDate = LocalDate.parse(parts[1])
            Pair(startDate, endDate)
        } catch (e: Exception) {
            null
        }
    }

    // Format child age label
    private fun formatAgeLabel(months: Int): String {
        return if (months < 36) {
            "$months months old"
        } else {
            "${months / 12} years old"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadChildAndVaccineData()
        }
    }
}