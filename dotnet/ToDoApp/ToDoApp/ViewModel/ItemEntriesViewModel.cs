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

namespace ToDoApp
{
    public class ItemEntriesViewModel : INotifyPropertyChanged
    {
        private Realm _realm;

        private INavigation _navigation;

        public IEnumerable<Item> Entries { get; private set; }

        public ICommand SignOutCommand { get; private set; }

        public ICommand DeleteEntryCommand { get; private set; }

        public ICommand AddEntryCommand { get; private set; }

        public event PropertyChangedEventHandler PropertyChanged;

        public ItemEntriesViewModel(INavigation navigation, Realm realm)
        {
            _navigation = navigation;
            _realm = realm;

            // Get the list of items.
            Entries = _realm.All<Item>().OrderBy(i => i.Timestamp);

            SignOutCommand = new Command(() =>
            {
                User.Current.LogOutAsync().IgnoreResult();

                // Go to welcome page.
                _navigation.PopToRootAsync().IgnoreResult();
            });

            DeleteEntryCommand = new Command<Item>(DeleteEntry);
            AddEntryCommand = new Command(() => AddEntry().IgnoreResult());
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
    }
}
