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

    var warming: Int = -1

    @Ignore
    private var soundClip: String? = null

    var soundClipUri: Uri
    get() {return Uri.parse(soundClip)}
    set(value) {soundClip = value.toString()}

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
        warming = `in`.readInt()
        soundClip = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeInt(duration)
        dest.writeInt(breakTime)
        dest.writeInt(warming)
        dest.writeString(soundClip)
    }



    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel {Timer(it)}
    }

    fun totalSecondsDuration(): Int{
        return duration + breakTime
    }

}

//@TypeConverter fun uriToString(uri: Uri): String{
//    return uri.toString()
//}
//@TypeConverter fun stringToUri(s: String): Uri{
//    return Uri.parse(s)
//}