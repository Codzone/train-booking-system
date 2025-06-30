package ticket.booking.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil
{
    // Hashes a plain text password using BCrypt
    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Checks if a plain password matches the hashed password
    public static boolean checkPassword(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
