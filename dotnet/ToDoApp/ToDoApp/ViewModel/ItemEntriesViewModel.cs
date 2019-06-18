using Acr.UserDialogs;
using Realms;
using Realms.Sync;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using Xamarin.Forms;

using Credentials = Realms.Sync.Credentials;

namespace ToDoApp
{
    public class ItemEntriesViewModel : INotifyPropertyChanged
    {
        public IEnumerable<Item> Entries { get; private set; }

        private Realm _realm;

        public event PropertyChangedEventHandler PropertyChanged;

        public ICommand DeleteEntryCommand { get; private set; }

        public ICommand AddEntryCommand { get; private set; }

        public ItemEntriesViewModel()
        {
            DeleteEntryCommand = new Command<Item>(DeleteEntry);
            AddEntryCommand = new Command(() => AddEntry());
            Initialize();
        }

        private async Task Initialize()
        {
            _realm = await OpenRealm();
            Entries = _realm.All<Item>().OrderBy(i => i.Timestamp);
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(Entries)));
        }

        private async Task AddEntry()
        {
            var result = await UserDialogs.Instance.PromptAsync(new PromptConfig
            {
                Title = "New entry",
                Message = "Specify the item text",
            });

            if (result.Ok)
            {
                _realm.Write(() =>
                {
                    _realm.Add(new Item
                    {
                        Timestamp = DateTimeOffset.Now,
                        Body = result.Value,
                    });
                });
            }
        }

        private void DeleteEntry(Item entry) => _realm.Write(() => _realm.Remove(entry));

        private async Task<Realm> OpenRealm()
        {
            return Realm.GetInstance();
        }
    }
}