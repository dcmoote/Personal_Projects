using API.Modules.Database.Models;
using API.Modules.Users.Dtos;

namespace API.Modules.Users.Repositories;

public interface IUsersRepository
{
    Task<User> AddUser(User user);
    Task<User?> GetUserByGuid(Guid id);
    Task<User?> GetUserByUsername(string username);
    Task<User?> GetUserByEmail(string email);
    Task<bool> IsValidEmail(string email);
    Task<bool> IsValidUsername(string username);
    Task<User?> UpdateUser(User user);
}
