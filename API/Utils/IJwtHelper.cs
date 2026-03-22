namespace API.Utils;

public interface IJwtHelper
{
    Guid GetUserIdFromToken(string jwt);
    bool ValidateUserId(string jwt, Guid id);
}
