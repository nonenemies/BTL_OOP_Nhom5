package DAO.dev1;

import Model.dev1.Users;

public interface UserDAO extends BaseDao<Users>{
     Users checkLogin(String email, String passwordHash);

}
