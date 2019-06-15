using Realms;
using System;

namespace ToDoApp
{
    public class Item : RealmObject
    {
        [PrimaryKey]
        [MapTo("itemId")]
        public string ItemId { get; set; } = Guid.NewGuid().ToString();

        [MapTo("body")]
        public string Body { get; set; }

        [MapTo("isDone")]
        public bool IsDone { get; set; }

        [MapTo("timestamp")]
        public DateTimeOffset Timestamp { get; set; }
    }
}