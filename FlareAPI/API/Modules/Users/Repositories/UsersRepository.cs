using API.Modules.Database.Extensions;
using API.Modules.Database.Models;
using API.Modules.Database.Services;
using API.Modules.Users.Dtos;

using Microsoft.EntityFrameworkCore.ChangeTracking;

namespace API.Modules.Users.Repositories;

public class UsersRepository : IUsersRepository
{
    private readonly DatabaseContext _context;

    public UsersRepository(DatabaseContext context)
    {
        _context = context;
    }

    public Task<User> AddUser(User user)
    {
        _context.Users.Add(user);
        _context.SaveChanges();

        return Task.FromResult(user);
    }

    public Task<User?> GetUserByGuid(Guid id)
    {
        return Task.FromResult(_context.Users.Find(id));
    }

    public Task<User?> GetUserByUsername(string username)
    {
        return Task.FromResult(_context.Users.FirstOrDefault(u => u.Username.ToLower() == username.ToLower()));
    }

    public Task<User?> GetUserByEmail(string email)
    {
        return Task.FromResult(_context.Users.FirstOrDefault(u => u.Email.ToLower() == email.ToLower()));
    }

    public Task<bool> IsValidEmail(string email)
    {
        return Task.FromResult(!_context.Users.Any(u => u.Email.ToLower() == email.ToLower()));
    }

    public Task<bool> IsValidUsername(string username)
    {
        return Task.FromResult(!_context.Users.Any(u => u.Username.ToLower() == username.ToLower()));
    }

    public Task<User?> UpdateUser(User updatedUser)
    {
        User? existingUser = _context.Users.Find(updatedUser.Id);
        if (existingUser == null)
        {
            return Task.FromResult<User?>(null);
        }
        existingUser.Username = updatedUser.Username;
        existingUser.Email = updatedUser.Email;
        existingUser.FirstName = updatedUser.FirstName;
        existingUser.LastName = updatedUser.LastName;
        existingUser.BirthDate = updatedUser.BirthDate;
        existingUser.Password = updatedUser.Password;
        _context.SaveChanges();

        return Task.FromResult(_context.Users.Find(updatedUser.Id));
    }
}
