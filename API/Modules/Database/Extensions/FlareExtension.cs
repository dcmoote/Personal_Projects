using API.Modules.Database.Models;
using API.Modules.Flares.Dtos;

namespace API.Modules.Database.Extensions;

public static class FlareExtension
{
    public static FlareDto ToDto(this Flare model, List<FlareRecipient> recipients)
    {
        return new FlareDto
        {
            Id = model.Id,
            OwnerId = model.OwnerId,
            Latitude = model.Latitude,
            Longitude = model.Longitude,
            StartTime = model.StartTime,
            EndTime = model.EndTime,
            Description = model.Description,
            Recipients = recipients.Select(r => r.ToDto()).ToList()
        };
    }

    public static Flare ToModel(this FlareDto dto)
    {
        var flareId = Guid.NewGuid();

        return new Flare
        {
            Id = flareId,
            OwnerId = dto.OwnerId,
            Latitude = dto.Latitude,
            Longitude = dto.Longitude,
            StartTime = dto.StartTime,
            EndTime = dto.EndTime,
            Description = dto.Description,
            IsDeleted = false,
            Recipients = dto.Recipients.Select(r => r.ToModel(dto.OwnerId)).ToList()
        };
    }
}
