using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Models
{
    public class Email
    {
        [Key]
        public string UserEmail { get; set; }
        
        //public bool InChatroom { get; set; }
    }
}
