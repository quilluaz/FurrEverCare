package cit.edu.furrevercare.entity;

import java.util.List;

public class Pet {
    private String petID;
    private String ownerID; // Maps to ERD's "userID" (FK)
    private String name;
    private String breed;
    private String gender;
    private int age;
    private double weight;
    private String species;
    private List<String> allergies;

    public Pet() {
    }

    public Pet(String petID, String ownerID, String name, String breed, String gender,
               int age, double weight, String species, List<String> allergies) {
        this.petID = petID;
        this.ownerID = ownerID;
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.species = species;
        this.allergies = allergies;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }
}
