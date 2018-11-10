package com.example.vutuan.bkres_nb.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Phung Dinh Phuc on 29/07/2017.
 */

public class Device implements Parcelable {
    public int Id;
    public String Name;
    public String Imei;
    public String CreateTime;
    public String WarningNumberPhone;
    public String WarningMail;
    public int LakeId;

    public Device(int id, String name, String imei, String createTime, String warningNumberPhone, String warningMail) {
        Id = id;
        Name = name;
        Imei = imei;
        CreateTime = createTime;
        WarningNumberPhone = warningNumberPhone;
        WarningMail = warningMail;
    }

    protected Device(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Imei = in.readString();
        CreateTime = in.readString();
        WarningNumberPhone = in.readString();
        WarningMail = in.readString();
        LakeId = in.readInt();
    }


    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getWarningNumberPhone() {
        return WarningNumberPhone;
    }

    public void setWarningNumberPhone(String warningNumberPhone) {
        WarningNumberPhone = warningNumberPhone;
    }

    public String getWarningMail() {
        return WarningMail;
    }

    public void setWarningMail(String warningMail) {
        WarningMail = warningMail;
    }

    public int getLakeId() {
        return LakeId;
    }

    public void setLakeId(int lakeId) {
        LakeId = lakeId;
    }


    @Override
    public String toString() {
        return "Device{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Imei='" + Imei + '\'' +
                ", CreateTime='" + CreateTime + '\'' +
                ", WarningNumberPhone='" + WarningNumberPhone + '\'' +
                ", WarningMail='" + WarningMail + '\'' +
                ", LakeId=" + LakeId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeString(Name);
        parcel.writeString(Imei);
        parcel.writeString(CreateTime);
        parcel.writeString(WarningNumberPhone);
        parcel.writeString(WarningMail);
        parcel.writeInt(LakeId);
    }
}
