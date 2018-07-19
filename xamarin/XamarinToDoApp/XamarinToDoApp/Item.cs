using System;
using Realms;

namespace XamarinToDoApp
{
    public class Item: RealmObject
    {
        [PrimaryKey]
        public string itemId { get; set; } //set random uuid
        public string body { get; set; }
        public bool isDone { get; set; }
        public DateTimeOffset timestamp { get; set; }
    }
}
