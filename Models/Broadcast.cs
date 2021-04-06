using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Models
{
    public class Broadcast
    {
        public string From { get; set; }
        public List<string> To { get; set; }
        public string Content { get; set; }
    }
}
