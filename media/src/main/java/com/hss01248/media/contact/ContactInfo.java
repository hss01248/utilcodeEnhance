package com.hss01248.media.contact;

/**
 * @Despciption todo
 * @Author hss
 * @Date 14/12/2021 14:27
 * @Version 1.0
 */
public class ContactInfo {

    public   long sysId ;
    public String name = "";
    public String phoneNumber = "";

    @Override
    public String toString() {
        return "ContactInfo{" +
                "sysId=" + sysId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
