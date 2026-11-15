package com.ujjwal.justcall

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.telecom.Call

class CallActivity : Activity() {
    private val gold = Color.rgb(201,169,110)
    private val bg = Color.rgb(9,11,17)
    private var callback: Call.Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    override fun onResume() { super.onResume(); render() }

    private fun render() {
        val call = CallSession.current
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(bg)
        }
        root.addView(TextView(this).apply {
            text = "JUST CALL"
            textSize = 25f
            letterSpacing = .15f
            setTextColor(gold)
        })
        root.addView(TextView(this).apply {
            text = call?.details?.handle?.schemeSpecificPart ?: "Call"
            textSize = 26f
            setTextColor(Color.WHITE)
            setPadding(0, 28, 0, 18)
        })
        root.addView(TextView(this).apply {
            text = when (call?.state) {
                Call.STATE_RINGING -> "Incoming call"
                Call.STATE_DIALING -> "Calling…"
                Call.STATE_ACTIVE -> "Connected"
                Call.STATE_HOLDING -> "On hold"
                else -> "Call"
            }
            textSize = 16f
            setTextColor(Color.LTGRAY)
        })
        val mute = Button(this).apply {
            text = "Mute / Unmute"
            setOnClickListener {
                call?.let { it.setMuted(!it.details.hasProperty(Call.Details.PROPERTY_CONFERENCE)) }
            }
        }
        root.addView(mute)
        val speaker = Button(this).apply {
            text = "Speaker"
            setOnClickListener {
                val audio = getSystemService(android.telecom.TelecomManager::class.java)
                try { audio?.let { it.setAudioRoute(android.telecom.CallAudioState.ROUTE_SPEAKER) } } catch (_: Exception) {}
            }
        }
        root.addView(speaker)
        if (call?.state == Call.STATE_RINGING) {
            root.addView(Button(this).apply { text = "Answer"; setOnClickListener { call.answer(android.telecom.VideoProfile.STATE_AUDIO_ONLY); render() } })
            root.addView(Button(this).apply { text = "Reject"; setOnClickListener { call.disconnect(); finish() } })
        } else {
            root.addView(Button(this).apply { text = "End Call"; setTextColor(Color.WHITE); setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.RED)); setOnClickListener { call?.disconnect(); finish() } })
        }
        setContentView(root)
    }
}
