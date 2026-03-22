using API.Modules.Database.Models;
using API.Modules.Users.Dtos;

namespace API.Modules.Users.Extensions;

public static class CreateUserRequestDtoExtension
{
    public static User ToModel(this CreateUserRequestDto dto)
    {
        return new User
        {
            Id = Guid.NewGuid(),
            Username = dto.Username,
            Email = dto.Email,
            FirstName = dto.FirstName,
            LastName = dto.LastName,
            BirthDate = dto.BirthDate,
            Password = dto.Password
        };
    }
}
