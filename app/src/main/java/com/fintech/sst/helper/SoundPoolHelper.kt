package com.fintech.sst.helper

import android.media.SoundPool
import com.fintech.sst.App
import com.fintech.sst.R


class SoundPoolHelper private constructor() {
    private val soundPool: SoundPool = SoundPool.Builder()
            .setMaxStreams(100)
            .build()
    private var warningSourceId: Int = 0
    private var warningStreamId:Int = 0
    private var loadCompletion = false

    init {
        warningSourceId = soundPool.load(App.getAppContext(), R.raw.warning, 1)
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            loadCompletion = true
        }
    }

    companion object {
        @Volatile
        private var instace: SoundPoolHelper? = null

        fun getInstance() =
                instace ?: synchronized(this) {
                    instace ?: SoundPoolHelper().apply { instace = this }
                }
    }

    fun playWarning() {
        if (loadCompletion){
            warningStreamId = soundPool.play(warningSourceId,1f, 1f, 1, 0, 1f)
        }else{
            soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                loadCompletion = true
                warningStreamId = soundPool.play(warningSourceId,1f, 1f, 1, 0, 1f)
            }
        }
    }

    fun stop(){
        soundPool.stop(warningStreamId)
    }
}