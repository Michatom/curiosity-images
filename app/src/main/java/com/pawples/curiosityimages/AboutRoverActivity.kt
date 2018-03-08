package com.pawples.curiosityimages

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.animation.AlphaAnimation
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.LinearInterpolator
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_about_rover.*
import android.R.id.edit
import android.content.SharedPreferences
import android.util.Log
import android.widget.TextView
import android.widget.Toast


class AboutRoverActivity : AppCompatActivity() {

    private var rover: String? = null
    private var maxsol: String? = null
    private var status: String? = null
    private var totalphotos: String? = null
    private var landingdate: String? = null
    private var launchdate: String? = null

    @SuppressLint("SetTextI18n", "ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_rover)

        val name = "prefsname"

        val settings = getSharedPreferences(name, 0)

        if (settings.getBoolean("firstrun", true)) {
            Toast.makeText(this,"Click on the image to read more details.",Toast.LENGTH_SHORT).show()
            settings.edit().putBoolean("firstrun", false).commit()
        }

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                rover = null
                maxsol = null
                status = null
                totalphotos = null
                landingdate = null
                launchdate = null
            } else {
                rover = extras.getString("ROVER")
                maxsol = extras.getString("MAX_SOL")
                status = extras.getString("STATUS")
                totalphotos = extras.getString("TOTAL_PHOTOS")
                landingdate = extras.getString("LANDING_DATE")
                launchdate = extras.getString("LAUNCH_DATE")
            }
        } else {
            rover = savedInstanceState.getSerializable("ROVER") as String
            maxsol = savedInstanceState.getSerializable("MAX_SOL") as String
            status = savedInstanceState.getSerializable("STATUS") as String
            totalphotos = savedInstanceState.getSerializable("TOTAL_PHOTOS") as String
            landingdate = savedInstanceState.getSerializable("LANDING_DATE") as String
            launchdate = savedInstanceState.getSerializable("LAUNCH_DATE") as String
        }

        if (rover == "curiosity") {
            Glide.with(this)
                    .load(R.drawable.curiosity_rover)
                    .transition(GenericTransitionOptions.with(R.anim.img_animation))
                    .into(imgrover)

            textrover.text = "Curiosity"

            linktext.text = "For more information, visit https://en.wikipedia.org/wiki/Curiosity_(rover\nhttps://mars.nasa.gov/msl/"

        } else if (rover == "opportunity") {
            Glide.with(this)
                    .load(R.drawable.opportunity_rover)
                    .transition(GenericTransitionOptions.with(R.anim.img_animation))
                    .into(imgrover)

            textrover.text = "Opportunity"

            linktext.text = "For more information, visit https://en.wikipedia.org/wiki/Opportunity_(rover)\nhttps://mars.nasa.gov/mer/home/"
        }

        cameratext.text = totalphotos
        cameratextlong.text = totalphotos + " images were taken by this rover"
        soltext.text = maxsol
        soltextlong.text = "This rover is active for $maxsol sols on Mars"
        launchtext.text = launchdate
        launchtextlong.text = "Launch - $launchdate; landing - $landingdate"

        val alphaHide = AlphaAnimation(1.0f,0.0f)
        alphaHide.interpolator = LinearInterpolator()
        alphaHide.duration = 500

        val alphaShow = AlphaAnimation(0.0f,1.0f)
        alphaShow.interpolator = LinearInterpolator()
        alphaShow.duration = 500

        cameratextlong.alpha = 0f
        soltextlong.alpha = 0f
        launchtextlong.alpha = 0f
        linktext.alpha = 0f

        imgrover.setOnClickListener{
            cameratextlong.alpha = 1f
            soltextlong.alpha = 1f
            launchtextlong.alpha = 1f
            linktext.alpha = 1f
            updateConstraints(R.layout.activity_about_rover_click)
            cameratext.animation = alphaHide
            cameratextlong.animation = alphaShow
            soltext.animation = alphaHide
            soltextlong.animation = alphaShow
            launchtext.animation = alphaHide
            launchtextlong.animation = alphaShow
            linktext.animation = alphaShow

            Handler().postDelayed({
                cameratext.alpha = 0f
                soltext.alpha = 0f
                launchtext.alpha = 0f
                imgrover.setOnClickListener(null)
            }, 500)
        }

    }

    fun updateConstraints(@LayoutRes id: Int) {
        val newConstraintSet = ConstraintSet()
        newConstraintSet.clone(this, id)
        newConstraintSet.applyTo(root)
        val transition = ChangeBounds()
        transition.duration = 500
        transition.interpolator = AnticipateOvershootInterpolator()
        TransitionManager.beginDelayedTransition(root, transition)
    }

}
