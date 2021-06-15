package pl.edu.agh.travelagencyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.travelagencyapp.exception.InvalidUserException;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.Reservation;
import pl.edu.agh.travelagencyapp.model.User;
import pl.edu.agh.travelagencyapp.repository.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for(User u: this.userRepository.findAll())
            users.add(new User(u));
        return users;
    }

    @GetMapping("/users/search")
    public List<User> getFilteredUsers(@RequestParam(value = "firstName", required = false) String firstName,
                                       @RequestParam(value = "lastName", required = false) String lastName) {
        List<User> users = new ArrayList<>();
        for(User u: this.userRepository.findAll()){
            if((firstName == null || u.getFirstName().contains(firstName)) &&
                    (lastName == null || u.getLastName().contains(lastName))
            ){
                users.add(new User(u));
            }
        }
        return users;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found!"));
        return ResponseEntity.ok().body(new User(user));
    }

    @GetMapping("/users/{id}/reservations")
    public List<Reservation> getUserReservations(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found!"));

        List<Reservation> reservations = new ArrayList<>();
        for (Reservation r: user.getReservations())
            reservations.add(new Reservation(r));
        return reservations;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return new User(this.userRepository.save(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails)
            throws ResourceNotFoundException, InvalidUserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found!"));

        if(!"A".equals(userDetails.getRole()) && !"R".equals(userDetails.getRole()))
            throw new InvalidUserException("Cannot change user role to different than Admin or Regular");

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        return ResponseEntity.ok(new User(this.userRepository.save(user)));
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found!"));

        this.userRepository.delete(user);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    @DeleteMapping("/users")
    public Map<String, Boolean> deleteAllUsers() {

        this.userRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
