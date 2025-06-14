package User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Check if username is being changed and if it's already taken
            if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                    userRepository.existsByUsername(updatedUser.getUsername())) {
                throw new RuntimeException("Username already exists: " + updatedUser.getUsername());
            }

            // Check if email is being changed and if it's already taken
            if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                    userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email already exists: " + updatedUser.getEmail());
            }

            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFullName(updatedUser.getFullName());

            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found with id: " + id);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}