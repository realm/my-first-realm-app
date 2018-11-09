using Realms;
using Realms.Sync;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows.Input;
using Xamarin.Forms;

namespace XamarinToDoApp
{
    public class ItemEntriesViewModel
    {
        public ItemEntriesViewModel()
        {
            var user = User.Current;
            var configuration = new FullSyncConfiguration(new Uri(Constants.REALM_URL), user);
            realm = Realm.GetInstance(configuration);
            Entries = realm.All<Item>();
            DeleteEntryCommand = new Command<Item>(DeleteEntry);
            AddEntryCommand = new Command(AddEntry);
        }
        public IEnumerable<Item> Entries { get; private set; }

        public INavigation Navigation { get; set; }

        Realm realm;

        public ICommand DeleteEntryCommand { get; private set; }

        public ICommand AddEntryCommand { get; private set; }

        void AddEntry()
        {
            var entry = new Item();
            entry.timestamp = DateTimeOffset.Now;
             realm.Write(() =>
             {
                realm.Add(entry);
             });
        }

        void DeleteEntry(Item entry) => realm.Write(() => realm.Remove(entry));
    }

}