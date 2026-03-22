using System.IdentityModel.Tokens.Jwt;

namespace API.Utils;

public class JwtHelper : IJwtHelper
{
    public Guid GetUserIdFromToken(string jwt)
    {
        var handler = new JwtSecurityTokenHandler();
        JwtSecurityToken token = handler.ReadJwtToken(jwt);
        return new Guid(token.Claims.First(claim => claim.Type == JwtRegisteredClaimNames.Jti).Value);
    }
    public bool ValidateUserId(string jwt, Guid id)
    {
        return GetUserIdFromToken(jwt).Equals(id);
    }
}
