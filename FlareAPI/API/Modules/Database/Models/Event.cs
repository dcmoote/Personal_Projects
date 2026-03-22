namespace API.Modules.Database.Models;

public class Event
{
    public Guid Id { get; set; }
    public Guid FlareId { get; set; }
    public Flare Flare { get; set; }
    public required string Name { get; set; }
    public string? Tag { get; set; }
    public bool IsDeleted { get; set; } = false;
}
