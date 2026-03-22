using System.Text.Json;

namespace API.Modules.Database.Models;

public class User
{
    public required Guid Id { get; set; }
    public required string Username { get; set; }
    public required string Email { get; set; }
    public required string FirstName { get; set; }
    public required string LastName { get; set; }
    public required Double BirthDate { get; set; }
    public required string Password { get; set; }

    public string ToJson()
    {
        return JsonSerializer.Serialize(this);
    }
}
