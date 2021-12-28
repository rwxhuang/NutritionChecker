package com.nutrition.checker;

import java.io.Serializable;

public class Label implements Serializable
{
    public Label(String name, double carbs, double transFat, double satFat, double sodium, double cholesterol, int score)
    {
        this.name = name;
        this.carbs = carbs;
        this.transFat = transFat;
        this.satFat = satFat;
        this.sodium = sodium;
        this.cholesterol = cholesterol;
        //this.base64String = encodedString;
        this.score = score;
    }

    private String name;

    private double carbs;

    private double satFat;

    private double transFat;

    private double sodium;

    private double cholesterol;

    private String base64String;

    private int score;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getCarbs()
    {
        return carbs;
    }

    public void setCarbs(double carbs)
    {
        this.carbs = carbs;
    }

    public double getSatFat() {
        return satFat;
    }

    public void setSatFat(double satFat)
    {
        this.satFat = satFat;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium)
    {
        this.sodium = sodium;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol)
    {
        this.cholesterol = cholesterol;
    }

    public double getTransFat() {
        return transFat;
    }

    public void setTransFat(double transFat)
    {
        this.transFat = transFat;
    }

    public String getBase64String()
    {
        return base64String;
    }

    public void setBase64String(String base64String)
    {
        this.base64String = base64String;
    }

    public int getScore(){return score;}

    public void setScore(int score){this.score = score;}
}
