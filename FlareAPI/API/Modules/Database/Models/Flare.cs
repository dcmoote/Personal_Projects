namespace API.Modules.Database.Models;

public class Flare
{
    public Guid Id { get; set; }
    public Guid OwnerId { get; set; }
    public User Owner { get; set; }
    public List<FlareRecipient> Recipients { get; set; }
    public required double Latitude { get; set; }
    public required double Longitude { get; set; }
    public required double StartTime { get; set; }
    public required double EndTime { get; set; }
    public required string Description { get; set; }
    public bool IsDeleted { get; set; } = false;
}
