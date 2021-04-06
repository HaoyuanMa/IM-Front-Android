using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Models
{
    public class ImUser : IdentityUser
    {
        public bool IsWebOnline { get; set; }
        public bool IsAndroidOnline { get; set; }
        public bool IsPcOnline { get; set; }
        public int Sex { get; set; }
        public DateTime BirthDay { get; set; }
        public string Place { get; set; }
        public virtual ICollection<Class> Classes { get; set; }
        public virtual ICollection<Group> Groups { get; set; }
        public  ICollection<Message> UnreadMessages { get; set; }

    }

}
