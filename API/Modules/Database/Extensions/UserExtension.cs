using API.Modules.Database.Models;
using API.Modules.Users.Dtos;

namespace API.Modules.Database.Extensions;

public static class UserExtension
{
    public static UserDto ToDto(this User model)
    {
        return new UserDto
        {
            Id = model.Id,
            Username = model.Username,
            Email = model.Email,
            FirstName = model.FirstName,
            LastName = model.LastName
        };
    }
}
