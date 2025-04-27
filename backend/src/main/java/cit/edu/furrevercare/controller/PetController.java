package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.Pet;
import cit.edu.furrevercare.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping(consumes = {"multipart/form-data"})
    public String addPet(
        @PathVariable String userID,
        @RequestPart("pet") Pet pet,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userID)) {
            throw new SecurityException("Unauthorized");
        }
        return petService.addPetToUser(userID, pet, image);
    }

    @GetMapping
    public List<Pet> getUserPets(@PathVariable String userID) throws ExecutionException, InterruptedException {
        return petService.getUserPets(userID);
    }

    @GetMapping("/{petID}")
    public Pet getPetById(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        return petService.getPetById(userID, petID);
    }

    @PutMapping(value = "/{petID}", consumes = {"multipart/form-data"})
    public String updatePet(
        @PathVariable String userID,
        @PathVariable String petID,
        @RequestPart("pet") Pet pet,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userID)) {
            throw new SecurityException("Unauthorized");
        }
        return petService.updatePet(userID, petID, pet, image);
    }

    @DeleteMapping("/{petID}")
    public String deletePet(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userID)) {
            throw new SecurityException("Unauthorized");
        }
        return petService.deletePet(userID, petID);
    }
}