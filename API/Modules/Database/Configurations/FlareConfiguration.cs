using API.Modules.Database.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace API.Modules.Database.Configurations;

public class FlareConfiguration: IEntityTypeConfiguration<Flare>
{
    public void Configure(EntityTypeBuilder<Flare> builder)
    {
        builder
            .HasOne(f => f.Owner)
            .WithMany()
            .HasForeignKey(f => f.OwnerId);

        builder
            .HasMany(f => f.Recipients)
            .WithOne()
            .HasForeignKey(fr => fr.FlareId);

        builder
            .Property(f => f.Id)
            .HasColumnType("char(36)");

        builder
            .Property(f => f.OwnerId)
            .HasColumnType("char(36)");

        builder
            .Property(f => f.Latitude)
            .IsRequired();

        builder
            .Property(f => f.Longitude)
            .IsRequired();

        builder
            .Property(f => f.StartTime)
            .IsRequired();

        builder
            .Property(f => f.EndTime)
            .IsRequired();

        builder
            .Property(f => f.Description)
            .IsRequired();

        var flareOne = new Flare
        {
            Id = new Guid("00000000-0000-0000-0000-000000000002"),
            Description = "Fulton Library",
            EndTime = 1762832994,
            Latitude = 40.28112680860248,
            Longitude = -111.71632633047479,
            OwnerId = new Guid("00000000-0000-0000-0000-000000000001"),
            StartTime = 1731296994
        };

        var flareTwo = new Flare
        {
            Id = new Guid("00000000-0000-0000-0000-000000000003"),
            Description = "IHop",
            EndTime = 1762832994,
            Latitude = 40.27487350865033,
            Longitude = -111.71566119622896,
            OwnerId = new Guid("00000000-0000-0000-0000-000000000001"),
            StartTime = 1731296994
        };

        var flareThree = new Flare
        {
            Id = new Guid("00000000-0000-0000-0000-000000000004"),
            Description = "Wendy's",
            EndTime = 1762832994,
            Latitude = 40.27962693885382,
            Longitude = -111.71491939904091,
            OwnerId = new Guid("00000000-0000-0000-0000-000000000001"),
            StartTime = 1731296994
        };

        builder.HasData(flareOne, flareTwo, flareThree);
    }
}
