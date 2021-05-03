using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Models
{
    public class Message
    {  
        public string Type { get; set; }
        public string From { get; set; }
        public List<string> To { get; set; }
        public string ContentType { get; set; }
        public string Content { get; set; }
        public long FileSize { get; set; }
    }
}
