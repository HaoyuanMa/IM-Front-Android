using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Models
{
    public class Group
    {
        [Key]
        public string Id { get; set; }
        public string OwnerId { get; set; }
        public string Name { get; set; }
        public int UsersNum { get; set; }
        public virtual ICollection<ImUser> Members { get; set; }
    }
}
