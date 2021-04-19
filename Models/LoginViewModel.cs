using System.ComponentModel.DataAnnotations;

namespace IM_Api.Models
{
    public class LoginViewModel
    {
        [Required]
        //[EmailAddress]
        public string Email { get; set; }

        [Required]
        //[DataType(DataType.Password)]
        public string Password { get; set; }
    }
}
