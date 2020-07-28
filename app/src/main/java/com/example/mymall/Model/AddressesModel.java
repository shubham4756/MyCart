package com.example.mymall.Model;

public class AddressesModel {

    private boolean selected;
    private String city;
    private String locality;
    private String flatNo;
    private String pincode;
    private String landmark;
    private String name;
    private String moblieNo;
    private String alternateMoblieNo;
    private String state;

    public AddressesModel(boolean selected, String city, String locality, String flatNo, String pincode, String landmark, String name, String moblieNo, String alternateMoblieNo, String state) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.flatNo = flatNo;
        this.pincode = pincode;
        this.landmark = landmark;
        this.name = name;
        this.moblieNo = moblieNo;
        this.alternateMoblieNo = alternateMoblieNo;
        this.state = state;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoblieNo() {
        return moblieNo;
    }

    public void setMoblieNo(String moblieNo) {
        this.moblieNo = moblieNo;
    }

    public String getAlternateMoblieNo() {
        return alternateMoblieNo;
    }

    public void setAlternateMoblieNo(String alternateMoblieNo) {
        this.alternateMoblieNo = alternateMoblieNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
