package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.Pet;
import cit.edu.furrevercare.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    public String addPet(
        @PathVariable String userID,
        @RequestBody Pet pet
    ) throws Exception {
        // The JwtFilter already validated the token and set authentication
        // Just check if the authenticated user matches the requested userID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userID)) {
            throw new SecurityException("Unauthorized");
        }
        return petService.addPetToUser(userID, pet);
    }

    @GetMapping
    public List<Pet> getUserPets(@PathVariable String userID) throws ExecutionException, InterruptedException {
        return petService.getUserPets(userID);
    }

    @GetMapping("/{petID}")
    public Pet getPetById(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        return petService.getPetById(userID, petID);
    }

    @PutMapping("/{petID}")
    public String updatePet(@PathVariable String userID, @PathVariable String petID, @RequestBody Pet updatedPet) throws ExecutionException, InterruptedException {
        return petService.updatePet(userID, petID, updatedPet);
    }

    @DeleteMapping("/{petID}")
    public String deletePet(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        return petService.deletePet(userID, petID);
    }

   
}
