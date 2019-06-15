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
            AddEntryCommand = new Command(() => AddEntry().IgnoreResult());
            Initialize().IgnoreResult();
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
            var user = User.Current;
            if (user != null)
            {
                var configuration = new FullSyncConfiguration(new Uri(Constants.RealmPath, UriKind.Relative), user);

                // User has already logged in, so we can just load the existing data in the Realm.
                return Realm.GetInstance(configuration);
            }

            // When that is called in the page constructor, we need to allow the UI operation
            // to complete before we can display a dialog prompt.
            await Task.Yield();

            var response = await UserDialogs.Instance.PromptAsync(new PromptConfig
            {
                Title = "Login",
                Message = "Please enter your nickname",
                OkText = "Login",
                IsCancellable = false,
            });
            var credentials = Credentials.Nickname(response.Value, isAdmin: true);

            try
            {
                UserDialogs.Instance.ShowLoading("Logging in...");

                user = await User.LoginAsync(credentials, new Uri(Constants.AuthUrl));

                UserDialogs.Instance.ShowLoading("Loading data");

                var configuration = new FullSyncConfiguration(new Uri(Constants.RealmPath, UriKind.Relative), user);

                // First time the user logs in, let's use GetInstanceAsync so we fully download the Realm
                // before letting them interract with the UI.
                var realm = await Realm.GetInstanceAsync(configuration);

                UserDialogs.Instance.HideLoading();

                return realm;
            }
            catch (Exception ex)
            {
                await UserDialogs.Instance.AlertAsync(new AlertConfig
                {
                    Title = "An error has occurred",
                    Message = $"An error occurred while trying to open the Realm: {ex.Message}"
                });

                // Try again
                return await OpenRealm();
            }
        }
    }
}