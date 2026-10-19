
package Services.dev1;

import DAO.dev1.UserDAOImpl;
import Model.dev1.Users;

public class AuthService {

    private final UserDAOImpl userDAO =
            UserDAOImpl.getInstance();

    // 1. Đăng nhập
    public Users login(String email, String passwordHash) {

        if (email == null || passwordHash == null) {
            return null;
        }

        return userDAO.checkLogin(email, passwordHash);
    }

    // 2. Kiểm tra quyền Admin
    public boolean isAdmin(Users user) {

        return user != null &&
                "ADMIN".equalsIgnoreCase(user.getRole());
    }

    // 3. Xem thông tin cá nhân
    public Users getProfile(int userID) {

        return userDAO.selectByID(userID);
    }
}
