package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.Resource;
import cit.edu.furrevercare.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/resources") // Base path for user-specific resources
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    private void checkUserAuthorization(String pathUserID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getName().equals(pathUserID)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized for this resource");
        }
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@PathVariable String userID, @RequestBody Resource resource) {
        checkUserAuthorization(userID);
        try {
            Resource createdResource = resourceService.createResource(userID, resource);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interruption status
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating resource", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error creating resource", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources(@PathVariable String userID) {
        checkUserAuthorization(userID);
        try {
            List<Resource> resources = resourceService.getAllResourcesByUserId(userID);
            return ResponseEntity.ok(resources);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching resources", e);
        }
    }

    @GetMapping("/{resourceID}")
    public ResponseEntity<Resource> getResourceById(@PathVariable String userID, @PathVariable String resourceID) {
        checkUserAuthorization(userID);
        try {
            Resource resource = resourceService.getResourceById(userID, resourceID);
            if (resource != null) {
                return ResponseEntity.ok(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching resource", e);
        }
    }

    @PutMapping("/{resourceID}")
    public ResponseEntity<Resource> updateResource(@PathVariable String userID, @PathVariable String resourceID, @RequestBody Resource resource) {
        checkUserAuthorization(userID);
        try {
            Resource updatedResource = resourceService.updateResource(userID, resourceID, resource);
            return ResponseEntity.ok(updatedResource);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating resource", e);
        } catch (Exception e) { // Catch potential RuntimeException for not found during update
            if (e.getMessage() != null && e.getMessage().contains("Resource not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error updating resource", e);
        }
    }

    @DeleteMapping("/{resourceID}")
    public ResponseEntity<String> deleteResource(@PathVariable String userID, @PathVariable String resourceID) {
        checkUserAuthorization(userID);
        try {
            String message = resourceService.deleteResource(userID, resourceID);
            return ResponseEntity.ok(message);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting resource", e);
        }
    }
}