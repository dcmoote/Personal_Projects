using API.Modules.Auth.Dtos;

namespace API.Modules.Auth.Services;
public interface IAuthService
{
    Task<string?> Login(LoginRequestDto request);
}
