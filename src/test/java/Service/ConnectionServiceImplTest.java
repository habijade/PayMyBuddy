package Service;

import com.example.demo.entity.Connection;
import com.example.demo.entity.User;
import com.example.demo.repository.ConnectionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.ConnectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ConnectionServiceImplTest {
    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFriendValidBuddyEmailShouldReturnTrue() {
        // Arrange
        Long userId = 1L;
        String buddyEmail = "buddy@example.com";

        User user = new User();
        user.setId(userId);

        User buddyUser = new User();
        buddyUser.setId(2L);
        buddyUser.setEmail(buddyEmail);

        Connection connection = new Connection(userId, buddyUser.getId());

        when(userService.isAnExistingEmail(buddyEmail)).thenReturn(true);
        when(userService.findUserByEmail(buddyEmail)).thenReturn(buddyUser);
        when(connectionRepository.save(any(Connection.class))).thenReturn(connection);

        boolean result = connectionService.addFriend(userId, buddyEmail);

        assertTrue(result);
    }
}
