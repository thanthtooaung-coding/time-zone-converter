package com.vinn.timezoneconverter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.vinn.timezoneconverter.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var fromTimeZone: TimeZone? = null
    private var toTimeZone: TimeZone? = null

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeUpdater: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTimezoneSpinners()
        setupTimeUpdater()
    }

    private fun setupTimezoneSpinners() {
        val timezones = TimeZone.getAvailableIDs()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, timezones)

        (binding.fromTimezoneLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.toTimezoneLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        (binding.fromTimezoneLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            val selectedTimezoneId = adapter.getItem(position) ?: return@setOnItemClickListener
            fromTimeZone = TimeZone.getTimeZone(selectedTimezoneId)
            updateConversion()
        }

        (binding.toTimezoneLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            val selectedTimezoneId = adapter.getItem(position) ?: return@setOnItemClickListener
            toTimeZone = TimeZone.getTimeZone(selectedTimezoneId)
            updateConversion()
        }

        val deviceTimeZone = TimeZone.getDefault()
        fromTimeZone = deviceTimeZone
        (binding.fromTimezoneLayout.editText as? AutoCompleteTextView)?.setText(deviceTimeZone.id, false)
    }

    private fun setupTimeUpdater() {
        timeUpdater = object : Runnable {
            override fun run() {
                updateConversion()
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun updateConversion() {
        val fromTz = fromTimeZone
        val toTz = toTimeZone
        if (fromTz != null && toTz != null) {
            binding.resultCard.visibility = View.VISIBLE

            val currentTime = Calendar.getInstance()

            val fromFormatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            fromFormatter.timeZone = fromTz
            binding.textViewFromTime.text = fromTz.displayName
            binding.textViewFromTimeValue.text = fromFormatter.format(currentTime.time)

            val toFormatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            toFormatter.timeZone = toTz
            binding.textViewToTime.text = toTz.displayName
            binding.textViewToTimeValue.text = toFormatter.format(currentTime.time)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(timeUpdater)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timeUpdater)
    }
}