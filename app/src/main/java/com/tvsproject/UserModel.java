package com.tvsproject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class UserModel implements Parcelable {
    private String name, designation, city, employeeNo, date, salary, latitude, longitude;
    private LatLng latLng;

    UserModel(){}

    UserModel(LatLng latLng, String city) {
        this.latLng = latLng;
        this.city = city;
    }


    private UserModel(Parcel in) {
        name = in.readString();
        designation = in.readString();
        city = in.readString();
        employeeNo = in.readString();
        date = in.readString();
        salary = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getDesignation() {
        return designation;
    }

    void setDesignation(String designation) {
        this.designation = designation;
    }

    String getCity() {
        return city;
    }

    void setCity(String city) {
        this.city = city;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    String getSalary() {
        return salary;
    }

    void setSalary(String salary) {
        this.salary = salary;
    }

    LatLng getLatLng() {
        return latLng;
    }

    void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(designation);
        dest.writeString(city);
        dest.writeString(employeeNo);
        dest.writeString(date);
        dest.writeString(salary);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeParcelable(latLng, flags);
    }
}
