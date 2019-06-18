using Realms;
using System;

namespace ToDoApp
{
    public class Item : RealmObject
    {
        public string Body { get; set; }

        public DateTimeOffset Timestamp { get; set; }
    }
}