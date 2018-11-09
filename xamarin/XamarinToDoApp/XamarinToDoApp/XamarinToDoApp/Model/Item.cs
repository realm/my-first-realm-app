using Realms;
using System;

namespace XamarinToDoApp
{
    public class Item : RealmObject
    {
        [PrimaryKey]
        public string itemId { get; set; } = Guid.NewGuid().ToString(); 
        public string body { get; set; }
        public bool isDone { get; set; }
        public DateTimeOffset timestamp { get; set; }
    }
}