using API.Modules.Database.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace API.Modules.Database.Configurations;

public class FlareRecipientConfiguration: IEntityTypeConfiguration<FlareRecipient>
{
    public void Configure(EntityTypeBuilder<FlareRecipient> builder)
    {
        builder
            .HasOne<Flare>()
            .WithMany(f => f.Recipients)
            .HasForeignKey(fr => fr.FlareId);

        builder
            .HasOne(fr => fr.Recipient)
            .WithMany()
            .HasForeignKey(fr => fr.RecipientId);

        builder
            .HasOne(fr => fr.Owner)
            .WithMany()
            .HasForeignKey(fr => fr.OwnerId);

        builder
            .Property(fr => fr.Id)
            .HasColumnType("char(36)");

        builder
            .Property(fr => fr.FlareId)
            .HasColumnType("char(36)");

        builder
            .Property(fr => fr.RecipientId)
            .HasColumnType("char(36)");

        builder
            .Property(fr => fr.OwnerId)
            .HasColumnType("char(36)");

        builder
            .Property(fr => fr.HasAccepted)
            .IsRequired();

        var flareRecipientOne = new FlareRecipient
        {
            Id = new Guid("00000000-0000-0000-0000-000000000001"),
            FlareId = new Guid("00000000-0000-0000-0000-000000000002"),
            OwnerId = new Guid("00000000-0000-0000-0000-000000000001"),
            RecipientId = new Guid("00000000-0000-0000-0000-000000000003"),
            HasAccepted = true,
        };

        builder.HasData(flareRecipientOne);
    }
}
