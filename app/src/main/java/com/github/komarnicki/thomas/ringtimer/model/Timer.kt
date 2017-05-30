package com.github.komarnicki.thomas.ringtimer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.github.komarnicki.thomas.ringtimer.createParcel

@Entity
class Timer : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var duration: Int = 0

    var breakTime: Int = 0

    var almostDone: Int = -1

    var soundClip: Uri? = null

    @Ignore
    constructor(duration: Int, breakTime: Int) {
        this.duration = duration
        this.breakTime = breakTime
    }

    constructor()

    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        duration = `in`.readInt()
        breakTime = `in`.readInt()
        almostDone = `in`.readInt()
        soundClip = Uri.parse(`in`.readString())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeInt(duration)
        dest.writeInt(breakTime)
        dest.writeInt(almostDone)
        dest.writeString(soundClip.toString())

    }

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel {Timer(it)}
    }

    fun totalSecondsDuration(): Int{
        return duration + breakTime
    }

}