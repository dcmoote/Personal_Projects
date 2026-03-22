namespace API.Modules.Flares.Dtos;

public class FlareDto
{
    public Guid Id { get; set; }
    public Guid OwnerId { get; set; }
    public required double Latitude { get; set; }
    public required double Longitude { get; set; }
    public required double StartTime { get; set; }
    public required double EndTime { get; set; }
    public required string Description { get; set; }
    public required List<FlareRecipientDto> Recipients { get; set; }
}
