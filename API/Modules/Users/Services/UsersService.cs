using API.Modules.Database.Models;
using API.Modules.Users.Dtos;
using API.Modules.Users.Repositories;

namespace API.Modules.Users.Services;

public class UsersService(IUsersRepository usersRepository) : IUsersService
{
    private readonly IUsersRepository _usersRepository = usersRepository;

    public Task<User> AddUser(User user)
    {
        return _usersRepository.AddUser(user);
    }

    public Task<User?> GetUserByGuid(Guid id)
    {
        return _usersRepository.GetUserByGuid(id);
    }

    public async Task<User?> GetUserByUsernameOrEmail(string usernameOrEmail)
    {
        User? user = await _usersRepository.GetUserByUsername(usernameOrEmail);

        if (user == null)
        {
            user = await _usersRepository.GetUserByEmail(usernameOrEmail);
        }

        return user;
    }

    public async Task<bool> IsValidEmail(string email)
    {
        return await _usersRepository.IsValidEmail(email);
    }

    public async Task<bool> IsValidUsername(string username)
    {
        return await _usersRepository.IsValidUsername(username);
    }
    public async Task<User> UpdateUser(User updatedUser)
    {

        return await _usersRepository.UpdateUser(updatedUser);
    }
}
