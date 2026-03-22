using API.Modules.Database.Models;
using API.Modules.Flares.Dtos;

namespace API.Modules.Database.Extensions;

public static class FlareRecipientExtension
{
    public static FlareRecipient ToModel(this FlareRecipientDto dto, Guid ownerId)
    {
        return new FlareRecipient
        {
            Id = Guid.NewGuid(),
            RecipientId = dto.UserId,
            OwnerId = ownerId,
            HasAccepted = dto.HasAccepted
        };
    }

    public static FlareRecipientDto ToDto(this FlareRecipient model)
    {
        return new FlareRecipientDto
        {
            UserId = model.RecipientId,
            HasAccepted = model.HasAccepted
        };
    }
}
