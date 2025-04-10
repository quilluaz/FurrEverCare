package cit.edu.furrevercare.entity;

import java.util.List;

public class EmergencyProfile {
    private String emergencyProfileID;
    private String bloodType;
    private List<String> chronicConditions;
    private String emergencyContact;
    private String specialInstructions;

    public EmergencyProfile() {}

    public EmergencyProfile(String emergencyProfileID, String bloodType, List<String> chronicConditions, String emergencyContact, String specialInstructions) {
        this.emergencyProfileID = emergencyProfileID;
        this.bloodType = bloodType;
        this.chronicConditions = chronicConditions;
        this.emergencyContact = emergencyContact;
        this.specialInstructions = specialInstructions;
    }

    public String getEmergencyProfileID() {
        return emergencyProfileID;
    }

    public void setEmergencyProfileID(String emergencyProfileID) {
        this.emergencyProfileID = emergencyProfileID;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public List<String> getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(List<String> chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
