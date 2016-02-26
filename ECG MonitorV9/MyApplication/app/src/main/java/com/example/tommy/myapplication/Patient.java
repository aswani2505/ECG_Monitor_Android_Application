package com.example.tommy.myapplication;

/**
 * Created by Will Boyd on 1/30/2016.
 */
public class Patient {
    String name;
    int ID;
    int age;
    String gender;
    float[] ecgValues;

    Patient(String Name, int IDNum, int Age, String Gender) {
        this.setData(Name, IDNum, Age, Gender);
        return;
    }

    Patient(String Name, int IDNum, int Age, String Gender, float[] ECGValues) {
        this.setData(Name, IDNum, Age, Gender);
        this.ecgValues = ECGValues;
        return;
    }


    void setData(String Name, int IDNum, int Age, String Gender) {
        this.name = Name;
        this.ID = IDNum;
        this.age = Age;
        this.gender = Gender;
        return;
    }

    void setName(String Name) {
        this.name = Name;
        return;
    }

    void setID(int IDNum) {
        this.ID = IDNum;
        return;
    }

    void setAge(int Age) {
        this.age = Age;
        return;
    }

    void setGender(String Gender) {
        this.gender = Gender;
        return;
    }

    void setECGValues(float[] Values) {
        this.ecgValues = Values;
        return;
    }

    String getName() {
        return this.name;
    }

    int getID() {
        return this.ID;
    }

    int getAge() {
        return this.age;
    }

    String getGender() {
        return this.gender;
    }

    float[] getECGValues() {
        return ecgValues;
    }
}

