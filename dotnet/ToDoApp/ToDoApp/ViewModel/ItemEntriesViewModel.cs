using Acr.UserDialogs;
using Realms;
using Realms.Sync;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using Xamarin.Forms;

namespace ToDoApp
{
    public class ItemEntriesViewModel : INotifyPropertyChanged
    {
        private Realm _realm;
        private IEnumerable<Item> _entries;
        public IEnumerable<Item> Entries
        {
            get { return _entries; }
            private set
            {
                if (_entries == value)
                {
                    return;
                }
                _entries = value;
                PropertyChanged.Invoke(this, new PropertyChangedEventArgs(nameof(Entries)));
            }
        }

        public ICommand LogOutCommand { get; private set; }

        public ICommand DeleteEntryCommand { get; private set; }

        public ICommand AddEntryCommand { get; private set; }

        public event PropertyChangedEventHandler PropertyChanged;

        public ItemEntriesViewModel()
        {
            LogOutCommand = new Command(() =>
            {
                if (User.Current == null)
                {
                    return;
                }
                User.Current.LogOutAsync().IgnoreResult();
                _realm = null;
                Entries = null;
                StartLoginCycle().IgnoreResult();
            });

            DeleteEntryCommand = new Command<Item>(DeleteEntry);
            AddEntryCommand = new Command(() => AddEntry().IgnoreResult());

            StartLoginCycle().IgnoreResult();
        }

        private async Task StartLoginCycle()
        {
            do
            {
                await Task.Yield();
            } while (await LogIn() == false);
        }

        private async Task<bool> LogIn()
        {
            try
            {
                var user = User.Current;
                if (user == null)
                {
                    // Not already logged in.
                    LoginResult loginResult;
                    loginResult = await UserDialogs.Instance.LoginAsync("Log in", "Enter a username and password");

                    if (!loginResult.Ok)
                    {
                        return false;
                    }

                    // Create credentials with the given username and password.
                    // Specify whether we are registering a new user or not with `createUser`.
                    var credentials = Realms.Sync.Credentials.UsernamePassword(loginResult.LoginText, loginResult.Password);

                    // Log in as the user.
                    user = await User.LoginAsync(credentials, new Uri(Constants.AuthUrl));
                }

                Debug.Assert(user != null);

                var configuration = new FullSyncConfiguration(new Uri(Constants.RealmPath, UriKind.Relative), user);
                _realm = await Realm.GetInstanceAsync(configuration);

                // Get the list of items.
                Entries = _realm.All<Item>().OrderBy(i => i.Timestamp);

                Console.WriteLine("Login successful.");

                return true;
            }
            catch (Exception ex)
            {
                // Display the error message.
                await Application.Current.MainPage.DisplayAlert("Error", ex.Message, "OK");
                return false;
            }
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
