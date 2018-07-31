package com.yanfangxiong.multipagerdemo.Utils

import android.os.Parcel
import android.os.Parcelable

data class Boyoung (  val id: String,
                      val imageUri: String,
                      val title: String,
                        val overView: String
): Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageUri)
        parcel.writeString(title)
        parcel.writeString(overView)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Boyoung> {
        override fun createFromParcel(parcel: Parcel): Boyoung {
            return Boyoung(parcel)
        }

        override fun newArray(size: Int): Array<Boyoung?> {
            return arrayOfNulls(size)
        }
    }

}