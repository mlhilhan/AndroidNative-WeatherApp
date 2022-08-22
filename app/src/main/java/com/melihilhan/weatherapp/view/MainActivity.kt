package com.melihilhan.weatherapp.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import androidx.lifecycle.Observer
import com.melihilhan.weatherapp.R
import com.melihilhan.weatherapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName", "ankara")
        editTextCityName.setText(cName)

        viewModel.refreshData(cName!!)

        getLiveData()

        swipe_refresh_layout.setOnRefreshListener {
            layoutDataView.visibility = View.GONE
            txtError.visibility = View.GONE
            progressBarLoading.visibility = View.GONE
            var cityName = GET.getString("cityName", cName)?.toLowerCase()
            editTextCityName.setText(cityName)
            viewModel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }

        imageSearch.setOnClickListener {
            val cityName = editTextCityName.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
            Log.i(TAG, "onCreate: " + cityName)
        }

    }


    private fun getLiveData() {
        viewModel.weather_data.observe(this) { data ->
            data?.let {
                layoutDataView.visibility = View.VISIBLE
                progressBarLoading.visibility = View.GONE
                textDegree.text = data.main.temp.toString() + " °C"
                textCountryCode.text = data.sys.country.toString()
                textCityName.text = data.name.toString()
                textHumidity.text = ": " + data.main.humidity.toString() + " %"
                textSpeed.text = ": " + data.wind.speed.toString() + " km/s"
                textLat.text = ": " + data.coord.lat.toString()
                textLon.text = ": " + data.coord.lon.toString()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather[0].icon + "@2x.png")
                    .into(imageWeatherIcon)
            }
        }

        viewModel.weather_error.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    txtError.visibility = View.VISIBLE
                    progressBarLoading.visibility = View.GONE
                    layoutDataView.visibility = View.GONE
                } else {
                    txtError.visibility = View.GONE
                }
            }
        })

        viewModel.weather_loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    progressBarLoading.visibility = View.VISIBLE
                    txtError.visibility = View.GONE
                    layoutDataView.visibility = View.GONE
                } else {
                    progressBarLoading.visibility = View.GONE
                }
            }
        })
    }
}