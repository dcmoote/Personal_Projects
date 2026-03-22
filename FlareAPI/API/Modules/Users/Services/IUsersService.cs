using API.Modules.Database.Models;
using API.Modules.Users.Dtos;

namespace API.Modules.Users.Services;

public interface IUsersService
{
    Task<User> AddUser(User user);
    Task<User?> GetUserByGuid(Guid id);
    Task<User?> GetUserByUsernameOrEmail(string userNameOrEmail);
    Task<bool> IsValidEmail(string email);
    Task<bool> IsValidUsername(string username);
    Task<User> UpdateUser(User updatedUser);
}
