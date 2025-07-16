namespace API.Modules.Users.Dtos
{
    public class CreateUserRequestDto
    {
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required string FirstName { get; set; }
        public required string LastName { get; set; }
        public required double BirthDate { get; set; }
        public required string Password { get; set; }
    }
}
