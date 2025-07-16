namespace API.Modules.Auth.Dtos;
public class LoginRequestDto
{
    public required string Login { get; set; }
    public required string Password { get; set; }
    public Guid UserId { get; set; }
}
