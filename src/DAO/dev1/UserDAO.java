package dao;

import model.Users;

public interface UserDAO extends BaseDao<Users>{
     Users checkLogin(String email, String passwordHash);

}
